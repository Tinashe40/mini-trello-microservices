package com.tinashe.trello.authService.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tinashe.trello.authService.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
