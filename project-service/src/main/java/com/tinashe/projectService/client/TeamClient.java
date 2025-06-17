package com.tinashe.projectService.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.tinashe.projectService.dto.TeamDTO;

@FeignClient(name = "team-service")
public interface TeamClient {

    @GetMapping("/api/teams/{teamId}/exists")
    Boolean exists(@PathVariable Long teamId);

    @GetMapping("/api/teams/{teamId}")
    TeamDTO getTeamById(@PathVariable Long teamId);

    
}

