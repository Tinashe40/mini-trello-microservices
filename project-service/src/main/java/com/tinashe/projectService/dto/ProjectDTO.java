package com.tinashe.projectService.dto;

import java.time.LocalDateTime;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDTO {
    private Long id;
    private String name;
    private String description;
    private String ownerUsername;
    private LocalDateTime createdAt;
    private Integer priority;
    private boolean archived;
    private Set<String> assignedUsers;
    private Set<TeamDTO> teams;
}
