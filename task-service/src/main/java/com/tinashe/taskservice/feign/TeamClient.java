package com.tinashe.taskservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.tinashe.taskservice.dto.TeamDTO;

@FeignClient(name = "TEAM-SERVICE")
public interface TeamClient {
    @GetMapping("/api/teams/{teamId}")
    TeamDTO getTeamById(@PathVariable Long teamId);
}

