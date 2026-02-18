package com.ecommerce.project.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ecommerce.project.dto.UserDTO;
import com.ecommerce.project.model.User;
import com.ecommerce.project.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@RequestBody User user) {

        UserDTO userDTO = userService.registerUser(user);
        return new ResponseEntity<>(userDTO, HttpStatus.CREATED);
    }
}
