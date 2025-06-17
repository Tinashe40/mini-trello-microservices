package com.tinashe.teamService.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TeamDTO {
    @NotBlank(message = "Team name is required")
    private String name;

    private String description;
}
