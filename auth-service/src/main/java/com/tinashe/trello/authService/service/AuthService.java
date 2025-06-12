package com.tinashe.trello.authService.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.tinashe.trello.authService.DTOs.AuthResponse;
import com.tinashe.trello.authService.DTOs.LoginRequest;
import com.tinashe.trello.authService.DTOs.RegisterRequest;
import com.tinashe.trello.authService.DTOs.UserDTO;
import com.tinashe.trello.authService.model.Role;
import com.tinashe.trello.authService.model.User;
import com.tinashe.trello.authService.repository.UserRepository;
import com.tinashe.trello.authService.security.JwtUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RestTemplate restTemplate;

    public void register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already taken");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        userRepository.save(user);

        // Sync with User Service
        syncUserWithUserService(request, user.getRole());
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
        return new AuthResponse(token);
    }

    private void syncUserWithUserService(RegisterRequest request, Role role) {
        UserDTO userDTO = UserDTO.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .role(role.name())
                .active(true)
                .build();

        try {
            restTemplate.postForEntity("http://localhost:8082/api/users", userDTO, Void.class);
        } catch (Exception e) {
            log.error("Failed to sync user with user-service", e);
        }
    }
}
