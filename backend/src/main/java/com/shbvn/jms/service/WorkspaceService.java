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

    public WorkspaceService(WorkspaceRepository workspaceRepository,
                            WorkspaceMemberRepository workspaceMemberRepository,
                            UserRepository userRepository) {
        this.workspaceRepository = workspaceRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.userRepository = userRepository;
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
        workspaceMemberRepository.deleteByUserIdAndWorkspaceId(userId, workspaceId);
    }

    public Page<WorkspaceMember> getMembers(String workspaceId, Pageable pageable) {
        return workspaceMemberRepository.findByWorkspaceId(workspaceId, pageable);
    }
}
