package com.hugo.onboard.controller;

import com.hugo.onboard.model.user.User;
import com.hugo.onboard.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("registerUser")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        return userService.addUserToDatabase(user);
    }

    @PostMapping("loginUser")
    public ResponseEntity<String> loginUser(@RequestBody User user) {
        return userService.authenticateUser(user);
    }

    @PutMapping("updateUser")
    public ResponseEntity<String> updateUserInfo(@RequestBody User user) {
        return userService.updateUserInfo(user);
    }
}
