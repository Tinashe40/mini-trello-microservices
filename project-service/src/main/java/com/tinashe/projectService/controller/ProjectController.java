package com.tinashe.projectService.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tinashe.projectService.dto.ProjectCreateDTO;
import com.tinashe.projectService.dto.ProjectDTO;
import com.tinashe.projectService.dto.ProjectUpdateDTO;
import com.tinashe.projectService.service.ProjectService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROJECT_OWNER')")
    public ResponseEntity<ProjectDTO> createProject(@RequestBody ProjectCreateDTO dto) {
        ProjectDTO created = projectService.createProject(dto);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROJECT_OWNER')")
    public ResponseEntity<ProjectDTO> updateProject(
            @PathVariable Long id,
            @RequestBody ProjectUpdateDTO dto) {
        ProjectDTO updated = projectService.updateProject(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROJECT_OWNER')")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<ProjectDTO>> getAllProjects(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ProjectDTO> projects = projectService.getAllProjects(page, size);
        return ResponseEntity.ok(projects);
    }

    @PostMapping("/{projectId}/assign-team/{teamId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROJECT_OWNER')")
    public ResponseEntity<ProjectDTO> assignTeam(
            @PathVariable Long projectId,
            @PathVariable Long teamId) {
        ProjectDTO result = projectService.assignTeamToProject(projectId, teamId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{projectId}/assign-user/{username}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROJECT_OWNER')")
    public ResponseEntity<ProjectDTO> assignUser(
            @PathVariable Long projectId,
            @PathVariable String username) {
        ProjectDTO result = projectService.assignUserToProject(projectId, username);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_OWNER', 'MEMBER')")
    public ResponseEntity<Page<ProjectDTO>> searchProjects(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long teamId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String dir) {
        Page<ProjectDTO> results = projectService.searchProjects(q, teamId, page, size, sort, dir);
        return ResponseEntity.ok(results);
    }
}
