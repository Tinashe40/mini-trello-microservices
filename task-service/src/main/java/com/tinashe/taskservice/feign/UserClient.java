package com.tinashe.taskservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.tinashe.taskservice.dto.UserDTO;
@FeignClient(name = "USER-SERVICE")
public interface UserClient {
  @GetMapping("/api/users/{username}")
  UserDTO getUserByUsername(String username);
}
