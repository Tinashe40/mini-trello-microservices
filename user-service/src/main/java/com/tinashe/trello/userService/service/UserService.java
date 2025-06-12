package com.tinashe.trello.userService.service;

import java.util.List;
import com.tinashe.trello.userService.DTOs.UserDTO;

public interface UserService {
    UserDTO createUser(UserDTO userDTO);
    List<UserDTO> getAllUsers();
    UserDTO getUserByUsername(String username);
}
