package com.tinashe.trello.userService.service.impl;

import com.tinashe.trello.userService.dto.UserDTO;
import com.tinashe.trello.userService.model.User;
import com.tinashe.trello.userService.repository.UserRepository;
import com.tinashe.trello.userService.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public UserDTO createUser(UserDTO dto) {
        User user = User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .role(dto.getRole())
                .active(true)
                .build();
        return toDTO(repository.save(user));
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return repository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO getUserByUsername(String username) {
        return repository.findByUsername(username)
                .map(this::toDTO)
                .orElse(null);
    }

    private UserDTO toDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .active(user.isActive())
                .build();
    }
}
