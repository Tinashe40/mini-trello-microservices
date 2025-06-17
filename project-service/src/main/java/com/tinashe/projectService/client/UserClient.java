package com.tinashe.projectService.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.tinashe.projectService.dto.UserDTO;

@FeignClient(name = "user-service")
public interface UserClient {
    @GetMapping("/api/users/{username}")
    UserDTO getByUsername(@PathVariable String username);
}