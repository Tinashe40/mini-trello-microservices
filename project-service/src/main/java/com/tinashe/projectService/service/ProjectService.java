package com.tinashe.projectService.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.tinashe.projectService.client.TeamClient;
import com.tinashe.projectService.client.UserClient;
import com.tinashe.projectService.dto.ProjectDTO;
import com.tinashe.projectService.mapper.ProjectMapper;
import com.tinashe.projectService.model.Project;
import com.tinashe.projectService.repository.ProjectRepository;
import com.tinashe.projectService.repository.TeamRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {

    private final ProjectRepository projectRepo;
    private final TeamRepository teamRepo;
    private final TeamClient teamClient;
    private final UserClient userClient;

    public ProjectDTO createProject(ProjectDTO dto) {
        String username = getCurrentUsername();
        Project project = ProjectMapper.toEntity(dto);
        project.setOwnerUsername(username);

        log.info("User '{}' is creating project: {}", username, dto.getName());
        return ProjectMapper.toDTO(projectRepo.save(project));
    }

    public ProjectDTO updateProject(Long id, ProjectDTO dto) {
        Project project = findByIdOrThrow(id);
        validateEditAccess(project);

        project.setName(dto.getName());
        project.setDescription(dto.getDescription());

        log.info("Project '{}' updated by '{}'", id, getCurrentUsername());
        return ProjectMapper.toDTO(projectRepo.save(project));
    }

    public void deleteProject(Long id) {
        Project project = findByIdOrThrow(id);
        validateEditAccess(project);

        projectRepo.deleteById(id);
        log.info("Project '{}' deleted by '{}'", id, getCurrentUsername());
    }

    public Page<ProjectDTO> getAllProjects(int page, int size) {
        log.info("Fetching projects: page={}, size={}, user={}", page, size, getCurrentUsername());
        return projectRepo.findAll(PageRequest.of(page, size)).map(ProjectMapper::toDTO);
    }

    public ProjectDTO assignTeamToProject(Long projectId, Long teamId) {
        var project = findByIdOrThrow(projectId);
        verifyEdit(project);

        if (!Boolean.TRUE.equals(teamClient.exists(teamId))) {
            log.warn("Team {} not found via Feign", teamId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Team not found");
        }

        var team = teamRepo.findById(teamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Team not found"));

        project.getTeams().add(team);
        log.info("Team {} assigned to project {}", teamId, projectId);
        return ProjectMapper.toDTO(projectRepo.save(project));
    }

    public ProjectDTO assignUserToProject(Long projectId, String username) {
        var project = findByIdOrThrow(projectId);
        verifyEdit(project);

        userClient.getByUsername(username); // throws if not exists
        project.getAssignedUsers().add(username);

        log.info("User {} assigned to project {}", username, projectId);
        return ProjectMapper.toDTO(projectRepo.save(project));
    }

    public Page<ProjectDTO> searchProjects(String q, Long teamId, int page, int size, String sortField, String sortDir) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDir), sortField));
        Page<Project> result;

        if (teamId != null) {
            result = projectRepo.findByTeamId(teamId, pageRequest);
        } else if (q != null && !q.trim().isEmpty()) {
            result = projectRepo.findByNameContainingIgnoreCaseOrOwnerUsernameContainingIgnoreCase(q, q, pageRequest);
        } else {
            result = projectRepo.findAll(pageRequest);
        }

        return result.map(ProjectMapper::toDTO);
    }

    // 🔒 Helper methods

    private Project findByIdOrThrow(Long id) {
        return projectRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));
    }
    private void verifyEdit(Project project) {
    if (project.isArchived()) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot edit an archived project.");
    }
}

    private void validateEditAccess(Project project) {
        if (!canEdit(project)) {
            log.warn("Unauthorized access by '{}' on project '{}'", getCurrentUsername(), project.getId());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    private boolean canEdit(Project project) {
        String username = getCurrentUsername();
        return hasRole("ADMIN") || (hasRole("PROJECT_OWNER") && username.equals(project.getOwnerUsername()));
    }

    private String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private boolean hasRole(String role) {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_" + role));
    }
}
