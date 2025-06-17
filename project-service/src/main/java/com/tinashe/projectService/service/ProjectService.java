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
import com.tinashe.projectService.dto.ProjectCreateDTO;
import com.tinashe.projectService.dto.ProjectDTO;
import com.tinashe.projectService.dto.ProjectUpdateDTO;
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

    public ProjectDTO createProject(ProjectCreateDTO dto) {
        String username = getCurrentUsername();
        Project project = new Project();
        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        project.setOwnerUsername(username);

        log.info("Creating project '{}' by user '{}'", dto.getName(), username);
        return ProjectMapper.toDTO(projectRepo.save(project));
    }

    public ProjectDTO updateProject(Long id, ProjectUpdateDTO dto) {
        Project project = findByIdOrThrow(id);
        validateEditAccess(project);

        project.setName(dto.getName());
        project.setDescription(dto.getDescription());

        log.info("Updating project ID '{}' by user '{}'", id, getCurrentUsername());
        return ProjectMapper.toDTO(projectRepo.save(project));
    }

    public void deleteProject(Long id) {
        Project project = findByIdOrThrow(id);
        validateEditAccess(project);

        projectRepo.delete(project);
        log.info("Deleted project '{}' by '{}'", id, getCurrentUsername());
    }

    public Page<ProjectDTO> getAllProjects(int page, int size) {
        log.info("Fetching all projects for user '{}'", getCurrentUsername());
        return projectRepo.findAll(PageRequest.of(page, size))
                          .map(ProjectMapper::toDTO);
    }

    public Page<ProjectDTO> getMyProjects(int page, int size) {
        String username = getCurrentUsername();
        return projectRepo.findByOwnerUsernameOrAssignedUsersContaining(
                username, username, PageRequest.of(page, size))
                .map(ProjectMapper::toDTO);
    }

    public ProjectDTO assignTeamToProject(Long projectId, Long teamId) {
        Project project = findByIdOrThrow(projectId);
        verifyEdit(project);

        if (!Boolean.TRUE.equals(teamClient.exists(teamId))) {
            log.warn("Team ID '{}' not found", teamId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Team not found");
        }

        var team = teamRepo.findById(teamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Team not found"));

        project.getTeams().add(team);
        log.info("Assigned team '{}' to project '{}'", teamId, projectId);
        return ProjectMapper.toDTO(projectRepo.save(project));
    }

    public ProjectDTO assignUserToProject(Long projectId, String username) {
        Project project = findByIdOrThrow(projectId);
        verifyEdit(project);

        userClient.getByUsername(username); // will throw if user not found
        project.getAssignedUsers().add(username);

        log.info("Assigned user '{}' to project '{}'", username, projectId);
        return ProjectMapper.toDTO(projectRepo.save(project));
    }

    public Page<ProjectDTO> searchProjects(String q, Long teamId, int page, int size, String sortField, String sortDir) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDir), sortField));
        Page<Project> result;

        if (teamId != null) {
            result = projectRepo.findByTeamId(teamId, pageable);
        } else if (q != null && !q.trim().isEmpty()) {
            result = projectRepo.findByNameContainingIgnoreCaseOrOwnerUsernameContainingIgnoreCase(q, q, pageable);
        } else {
            result = projectRepo.findAll(pageable);
        }

        return result.map(ProjectMapper::toDTO);
    }

    // --- 🔒 Private Helpers ---

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
            log.warn("Access denied: User '{}' attempted to modify project '{}'", getCurrentUsername(), project.getId());
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
