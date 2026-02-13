package com.shbvn.jms.service;

import com.shbvn.jms.exception.ResourceNotFoundException;
import com.shbvn.jms.model.Project;
import com.shbvn.jms.model.ProjectMember;
import com.shbvn.jms.model.User;
import com.shbvn.jms.repository.ProjectMemberRepository;
import com.shbvn.jms.repository.ProjectRepository;
import com.shbvn.jms.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;

    public ProjectService(ProjectRepository projectRepository,
                          ProjectMemberRepository projectMemberRepository,
                          UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.userRepository = userRepository;
    }

    public Page<Project> getProjectsByWorkspaceId(String workspaceId, Pageable pageable) {
        return projectRepository.findByWorkspaceId(workspaceId, pageable);
    }

    public Project getProjectById(String id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));

        // Trigger lazy loading for tasks and members
        project.getTasks().size();
        project.getMembers().size();

        return project;
    }

    @Transactional
    public Project createProject(Project project) {
        Project savedProject = projectRepository.save(project);

        // Add team lead as project member
        if (project.getTeamLead() != null) {
            ProjectMember teamLeadMember = new ProjectMember();
            teamLeadMember.setProject(savedProject);
            User teamLeadUser = userRepository.findById(project.getTeamLead())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", project.getTeamLead()));
            teamLeadMember.setUser(teamLeadUser);
            projectMemberRepository.save(teamLeadMember);
        }

        return savedProject;
    }

    @Transactional
    public Project updateProject(String id, Project updates) {
        Project existing = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));

        if (updates.getName() != null) {
            existing.setName(updates.getName());
        }
        if (updates.getDescription() != null) {
            existing.setDescription(updates.getDescription());
        }
        if (updates.getStartDate() != null) {
            existing.setStartDate(updates.getStartDate());
        }
        if (updates.getEndDate() != null) {
            existing.setEndDate(updates.getEndDate());
        }
        if (updates.getStatus() != null) {
            existing.setStatus(updates.getStatus());
        }
        if (updates.getTeamLead() != null) {
            existing.setTeamLead(updates.getTeamLead());
        }

        return projectRepository.save(existing);
    }

    @Transactional
    public void deleteProject(String id) {
        if (!projectRepository.existsById(id)) {
            throw new ResourceNotFoundException("Project", "id", id);
        }
        projectRepository.deleteById(id);
    }

    @Transactional
    public ProjectMember addMember(String projectId, String userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        ProjectMember member = new ProjectMember();
        member.setProject(project);
        member.setUser(user);

        return projectMemberRepository.save(member);
    }

    @Transactional
    public void removeMember(String projectId, String userId) {
        projectMemberRepository.deleteByUserIdAndProjectId(userId, projectId);
    }
}
