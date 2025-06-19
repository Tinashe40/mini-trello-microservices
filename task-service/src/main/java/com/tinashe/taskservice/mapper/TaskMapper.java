package com.tinashe.taskservice.mapper;

import com.tinashe.taskservice.dto.TaskDTO;
import com.tinashe.taskservice.model.Task;

public class TaskMapper {

    public static TaskDTO toDTO(Task task) {
        return TaskDTO.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .projectId(task.getProjectId())
                .status(task.getStatus())
                .assignedToUsername(task.getAssignedToUsername())
                .assignedToTeamId(task.getAssignedToTeamId()) 
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }

    public static Task toEntity(TaskDTO dto) {
        Task task = new Task();
        task.setId(dto.getId());
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setProjectId(dto.getProjectId());
        task.setStatus(dto.getStatus());
        task.setAssignedToUsername(dto.getAssignedToUsername());
        task.setAssignedToTeamId(dto.getAssignedToTeamId()); 
        task.setCreatedAt(dto.getCreatedAt());
        task.setUpdatedAt(dto.getUpdatedAt());
        return task;
    }
}
