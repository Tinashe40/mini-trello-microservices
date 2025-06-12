package com.tinashe.trello.authService.DTOs;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
