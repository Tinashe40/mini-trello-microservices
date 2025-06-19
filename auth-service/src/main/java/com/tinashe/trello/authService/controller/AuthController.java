package com.tinashe.trello.authService.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tinashe.trello.authService.DTOs.AuthResponse;
import com.tinashe.trello.authService.DTOs.LoginRequest;
import com.tinashe.trello.authService.DTOs.RegisterRequest;
import com.tinashe.trello.authService.repository.UserRepository;
import com.tinashe.trello.authService.service.AuthService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "Username is already taken"));
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "Email is already taken"));
        }

        authService.register(request);
        return ResponseEntity.ok(Map.of("message", "User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            return ResponseEntity
                    .status(401)
                    .body(Map.of("error", ex.getMessage()));
        }
    }
}
