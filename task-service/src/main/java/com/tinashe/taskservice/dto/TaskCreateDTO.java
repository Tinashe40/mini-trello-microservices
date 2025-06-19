package com.tinashe.taskservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskCreateDTO {
    private String title;
    private String description;
    private Long projectId;
}
