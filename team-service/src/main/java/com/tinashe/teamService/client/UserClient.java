package com.tinashe.teamService.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import com.tinashe.teamService.dto.UserDTO;

@FeignClient(name = "user-service", url= "${USER_SERVICE_URL}")
public interface UserClient {
    @GetMapping("/api/users/{Id}")
    UserDTO find(@PathVariable("id") Long id,
                @RequestHeader("Authorization") String authHeader);
}
