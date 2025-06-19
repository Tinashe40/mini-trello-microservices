package com.tinashe.taskservice.dto;

import com.tinashe.taskservice.model.TaskStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskUpdateDTO {
    private String title;
    private String description;
    private TaskStatus status;
}
