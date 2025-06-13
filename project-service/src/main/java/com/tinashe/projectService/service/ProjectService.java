package com.tinashe.projectService.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.tinashe.projectService.dto.ProjectDTO;
import com.tinashe.projectService.mapper.ProjectMapper;
import com.tinashe.projectService.model.Project;
import com.tinashe.projectService.model.Team;
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

    public ProjectDTO createProject(ProjectDTO dto) {
        String username = getUsername();
        Project project = ProjectMapper.toEntity(dto);
        project.setOwnerUsername(username);

        log.info("User '{}' is creating a new project: {}", username, dto.getName());
        return ProjectMapper.toDTO(projectRepo.save(project));
    }

    public ProjectDTO updateProject(Long id, ProjectDTO dto) {
        Project project = findByIdWithCheck(id);
        if (!canEdit(project)) {
            log.warn("Unauthorized update attempt by '{}' on project {}", getUsername(), id);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        log.info("Project '{}' updated by '{}'", project.getId(), getUsername());
        return ProjectMapper.toDTO(projectRepo.save(project));
    }

    public void deleteProject(Long id) {
        Project project = findByIdWithCheck(id);
        if (!canEdit(project)) {
            log.warn("Unauthorized delete attempt by '{}' on project {}", getUsername(), id);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        projectRepo.deleteById(id);
        log.info("Project '{}' deleted by '{}'", id, getUsername());
    }

    public Page<ProjectDTO> getAllProjects(int page, int size) {
        log.info("User '{}' fetching paginated projects - Page: {}, Size: {}", getUsername(), page, size);
        return projectRepo.findAll(PageRequest.of(page, size)).map(ProjectMapper::toDTO);
    }

    public ProjectDTO assignTeamToProject(Long projectId, Long teamId) {
        Project project = findByIdWithCheck(projectId);
        if (!canEdit(project)) {
            log.warn("Unauthorized team assignment by '{}' on project {}", getUsername(), projectId);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        Team team = teamRepo.findById(teamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Team not found"));

        project.getTeams().add(team);
        log.info("Team '{}' assigned to project '{}' by '{}'", teamId, projectId, getUsername());
        return ProjectMapper.toDTO(projectRepo.save(project));
    }

    // 🔒 Helper methods

    private Project findByIdWithCheck(Long id) {
        return projectRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));
    }

    private boolean canEdit(Project project) {
        String username = getUsername();
        return hasRole("ADMIN") || (hasRole("PROJECT_OWNER") && username.equals(project.getOwnerUsername()));
    }

    private String getUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private boolean hasRole(String role) {
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_" + role));
    }

    public Page<ProjectDTO> searchProjects(String q, Long teamId, int page, int size, String sortField, String sortDir) {
    PageRequest pageRequest = PageRequest.of(page, size,
            Sort.by(Sort.Direction.fromString(sortDir), sortField));

    Page<Project> projects;

    if (teamId != null) {
        projects = projectRepo.findByTeamId(teamId, pageRequest);
    } else if (q != null && !q.isEmpty()) {
        projects = projectRepo.findByNameContainingIgnoreCaseOrOwnerUsernameContainingIgnoreCase(q, q, pageRequest);
    } else {
        projects = projectRepo.findAll(pageRequest);
    }

    return projects.map(ProjectMapper::toDTO);
}

}
