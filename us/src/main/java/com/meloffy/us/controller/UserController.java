package com.meloffy.us.controller;

import com.meloffy.us.dto.UserDTO;
import com.meloffy.us.model.User;
import com.meloffy.us.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getUserById(Authentication authentication) {
        return new ResponseEntity<UserDTO>(userService.getUserByid(authentication), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getUsers(Authentication authentication) {
        return new ResponseEntity<List<UserDTO>>(userService.getUsers(authentication), HttpStatus.OK);
    }
}
