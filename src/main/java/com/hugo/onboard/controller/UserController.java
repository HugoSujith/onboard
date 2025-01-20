package com.hugo.onboard.controller;

import com.hugo.onboard.model.user.User;
import com.hugo.onboard.repository.UserRepo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RestController
public class UserController {

    private final UserRepo userRepo;

    public UserController(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @PostMapping("registerUser")
    public ResponseEntity<Boolean> registerUser(@RequestBody User user) {
        boolean response = userRepo.addUsersToDB(user);
        if (response) {
            return new ResponseEntity<>(Boolean.TRUE, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
    }

    @PostMapping("loginUser")
    public ResponseEntity<String> loginUser(@RequestBody User user) {
        return userRepo.authenticateUser(user);
    }

    @GetMapping("getUser/{username}")
    public User getUserByUsername(@PathVariable String username) {
        return userRepo.getUserByUsername(username);
    }
}
