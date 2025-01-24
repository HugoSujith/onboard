package com.hugo.metalbroker.controller;

import java.util.logging.Logger;

import com.hugo.metalbroker.model.user.UserDTO;
import com.hugo.metalbroker.repository.UserRepo;
import com.hugo.metalbroker.service.implementation.UserService;
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

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("register")
    public ResponseEntity<Boolean> registerUser(@RequestBody UserDTO user) {
        Logger.getLogger(this.getClass().getName()).info("Entering User Controller register");
        Logger.getLogger(this.getClass().getName()).info(user.toString() + ": Controller");
        boolean response = userService.addUsersToDB(user);
        if (response) {
            Logger.getLogger(this.getClass().getName()).info(user.toString() + ": Controller");
            return new ResponseEntity<>(Boolean.TRUE, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody UserDTO user) {
        Logger.getLogger(this.getClass().getName()).info("Entering User Controller login");
        if (userService.login(user) == user) {
            Logger.getLogger(this.getClass().getName()).info(user.toString() + ": Controller");
            return new ResponseEntity<>("You are authorized to use your services!", HttpStatus.OK);
        }
        return new ResponseEntity<>("You are not authorized user!", HttpStatus.NOT_FOUND);
    }
}
