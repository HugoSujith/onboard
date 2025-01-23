package com.hugo.metalbroker.controller;

import com.hugo.metalbroker.model.user.UserDTO;
import com.hugo.metalbroker.repository.UserRepo;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    public ResponseEntity<Boolean> registerUser(@RequestBody UserDTO user) {
        boolean response = userRepo.addUsersToDB(user);
        if (response) {
            return new ResponseEntity<>(Boolean.TRUE, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
    }

    @PostMapping("loginUser")
    public ResponseEntity<String> loginUser(@RequestBody UserDTO user) {
        if (userRepo.authenticateUser(user)) {
            return new ResponseEntity<>("You are authorized to use your services!", HttpStatus.OK);
        }
        return new ResponseEntity<>("You are not authorized user!", HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "getUser/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDTO getUserByUsername(@PathVariable String username) {
        return userRepo.getUserByUsername(username);
    }
}
