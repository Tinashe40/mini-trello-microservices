package com.tinashe.projectService.mapper;

import com.tinashe.projectService.dto.ProjectDTO;
import com.tinashe.projectService.model.Project;

public class ProjectMapper {

    public static ProjectDTO toDTO(Project project) {
        if (project == null) return null;

        return ProjectDTO.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .build();
    }

    public static Project toEntity(ProjectDTO dto) {
        if (dto == null) return null;

        Project project = new Project();
        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        return project;
    }
}
