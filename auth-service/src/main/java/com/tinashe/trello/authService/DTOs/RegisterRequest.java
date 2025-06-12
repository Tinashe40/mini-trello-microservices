package com.tinashe.trello.authService.DTOs;

import com.tinashe.trello.authService.model.Role;
import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    private Role role;
}