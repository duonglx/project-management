package com.shbvn.jms.service;

import com.shbvn.jms.exception.ResourceNotFoundException;
import com.shbvn.jms.model.User;
import com.shbvn.jms.model.Workspace;
import com.shbvn.jms.model.WorkspaceMember;
import com.shbvn.jms.model.enums.WorkspaceRole;
import com.shbvn.jms.repository.UserRepository;
import com.shbvn.jms.repository.WorkspaceMemberRepository;
import com.shbvn.jms.repository.WorkspaceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final UserRepository userRepository;
    private final TaskStatusService taskStatusService;

    public WorkspaceService(WorkspaceRepository workspaceRepository,
                            WorkspaceMemberRepository workspaceMemberRepository,
                            UserRepository userRepository,
                            TaskStatusService taskStatusService) {
        this.workspaceRepository = workspaceRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.userRepository = userRepository;
        this.taskStatusService = taskStatusService;
    }

    @Transactional(readOnly = true)
    public Page<Workspace> getWorkspacesByUserId(String userId, Pageable pageable) {
        List<WorkspaceMember> workspaceMembers = workspaceMemberRepository.findByUserId(userId);
        List<String> workspaceIds = workspaceMembers.stream()
                .map(WorkspaceMember::getWorkspaceId)
                .collect(Collectors.toList());

        List<Workspace> workspaces = workspaceRepository.findAllById(workspaceIds);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), workspaces.size());

        // For list view, clear nested collections and proxies to avoid lazy loading issues
        workspaces.forEach(ws -> {
            ws.setMembers(null);
            ws.setProjects(null);
            ws.setOwner(null);
        });

        return new PageImpl<>(
                workspaces.subList(start, end),
                pageable,
                workspaces.size()
        );
    }

    @Transactional(readOnly = true)
    public Workspace getWorkspaceById(String id) {
        Workspace workspace = workspaceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace", "id", id));

        // Trigger lazy loading for members, projects, tasks, and project members
        workspace.getMembers().forEach(member -> {
            member.getUser().getName();
        });
        workspace.getProjects().forEach(project -> {
            project.getTasks().forEach(task -> {
                task.getAssignee();
                task.getComments().size();
            });
            project.getMembers().forEach(member -> {
                member.getUser().getName();
            });
        });
        workspace.getOwner().getName();

        return workspace;
    }

    @Transactional
    public Workspace createWorkspace(Workspace workspace, String ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", ownerId));

        workspace.setOwner(owner);
        Workspace savedWorkspace = workspaceRepository.save(workspace);

        // Add owner as ADMIN member
        WorkspaceMember ownerMember = new WorkspaceMember();
        ownerMember.setWorkspaceId(savedWorkspace.getId());
        ownerMember.setUserId(owner.getId());
        ownerMember.setRole(WorkspaceRole.ADMIN);
        workspaceMemberRepository.save(ownerMember);

        // Seed default task statuses for the new workspace
        taskStatusService.seedDefaults(savedWorkspace.getId());

        return savedWorkspace;
    }

    @Transactional
    public Workspace updateWorkspace(String id, Workspace updates) {
        Workspace existing = workspaceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace", "id", id));

        if (updates.getName() != null) {
            existing.setName(updates.getName());
        }
        if (updates.getSlug() != null) {
            existing.setSlug(updates.getSlug());
        }
        if (updates.getDescription() != null) {
            existing.setDescription(updates.getDescription());
        }
        if (updates.getImageUrl() != null) {
            existing.setImageUrl(updates.getImageUrl());
        }
        if (updates.getSettings() != null) {
            // Only allow known settings keys to prevent arbitrary JSONB injection
            java.util.Set<String> allowedKeys = java.util.Set.of("timezone", "language");
            java.util.Map<String, Object> currentSettings = existing.getSettings();
            if (currentSettings == null) {
                currentSettings = new java.util.HashMap<>();
            }
            for (java.util.Map.Entry<String, Object> entry : updates.getSettings().entrySet()) {
                if (allowedKeys.contains(entry.getKey())) {
                    currentSettings.put(entry.getKey(), entry.getValue());
                }
            }
            existing.setSettings(currentSettings);
        }

        Workspace saved = workspaceRepository.save(existing);

        // Trigger lazy loading for members, projects, tasks, and project members
        saved.getMembers().forEach(member -> {
            member.getUser().getName();
        });
        saved.getProjects().forEach(project -> {
            project.getTasks().forEach(task -> {
                task.getAssignee();
                task.getComments().size();
            });
            project.getMembers().forEach(member -> {
                member.getUser().getName();
            });
        });
        saved.getOwner().getName();

        return saved;
    }

    @Transactional
    public void deleteWorkspace(String id) {
        if (!workspaceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Workspace", "id", id);
        }
        workspaceRepository.deleteById(id);
    }

    @Transactional
    public WorkspaceMember addMember(String workspaceId, String userId, WorkspaceRole role, String message) {
        if (role == WorkspaceRole.OWNER) {
            throw new IllegalArgumentException("Cannot assign OWNER role via addMember. Use transfer ownership.");
        }
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace", "id", workspaceId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        WorkspaceMember member = new WorkspaceMember();
        member.setWorkspaceId(workspace.getId());
        member.setUserId(user.getId());
        member.setRole(role);
        member.setMessage(message);

        return workspaceMemberRepository.save(member);
    }

    @Transactional
    public void removeMember(String workspaceId, String userId) {
        WorkspaceMember member = workspaceMemberRepository
                .findByUserIdAndWorkspaceId(userId, workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Member", "userId", userId));
        if (member.getRole() == WorkspaceRole.OWNER) {
            throw new IllegalArgumentException("Cannot remove workspace owner. Use transfer ownership first.");
        }
        workspaceMemberRepository.delete(member);
    }

    public Page<WorkspaceMember> getMembers(String workspaceId, Pageable pageable) {
        return workspaceMemberRepository.findByWorkspaceId(workspaceId, pageable);
    }

    @Transactional
    public WorkspaceMember updateMemberRole(String workspaceId, String userId, WorkspaceRole role) {
        WorkspaceMember member = workspaceMemberRepository
                .findByUserIdAndWorkspaceId(userId, workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Member", "userId", userId));
        if (member.getRole() == WorkspaceRole.OWNER) {
            throw new IllegalArgumentException("Cannot change owner role. Use transfer ownership.");
        }
        member.setRole(role);
        return workspaceMemberRepository.save(member);
    }

    @Transactional
    public void transferOwnership(String workspaceId, String newOwnerId) {
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace", "id", workspaceId));

        // Verify new owner is an ADMIN in the workspace
        WorkspaceMember newOwnerMember = workspaceMemberRepository
                .findByUserIdAndWorkspaceId(newOwnerId, workspaceId)
                .orElseThrow(() -> new IllegalArgumentException("User is not a workspace member"));

        if (newOwnerMember.getRole() != WorkspaceRole.ADMIN) {
            throw new IllegalArgumentException("Can only transfer ownership to an ADMIN");
        }

        // Demote old owner to ADMIN
        WorkspaceMember oldOwnerMember = workspaceMemberRepository
                .findByUserIdAndWorkspaceId(workspace.getOwnerId(), workspaceId)
                .orElse(null);
        if (oldOwnerMember != null) {
            oldOwnerMember.setRole(WorkspaceRole.ADMIN);
            workspaceMemberRepository.save(oldOwnerMember);
        }

        // Promote new owner
        newOwnerMember.setRole(WorkspaceRole.OWNER);
        workspaceMemberRepository.save(newOwnerMember);

        // Update workspace owner
        workspace.setOwnerId(newOwnerId);
        workspaceRepository.save(workspace);
    }
}
