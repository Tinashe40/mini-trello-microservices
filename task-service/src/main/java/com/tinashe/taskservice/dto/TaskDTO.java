package com.tinashe.taskservice.dto;

import java.time.LocalDateTime;

import com.tinashe.taskservice.model.TaskStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private String assignedToUsername;
    private Long assignedToTeamId;
    private Long projectId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
