package com.hugo.metalbroker.controller;

import com.hugo.metalbroker.model.user.UserDTO;
import com.hugo.metalbroker.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RestController
public class UserController {

    private final UserService userServiceImpl;

    public UserController(UserService userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }

    @PostMapping("register")
    public ResponseEntity<Boolean> registerUser(@RequestBody UserDTO user) {
        boolean response = userServiceImpl.addUsersToDB(user);
        if (response) {
            return new ResponseEntity<>(Boolean.TRUE, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody UserDTO user) {
        if (userServiceImpl.login(user) == user) {
            return new ResponseEntity<>("You are authorized to use your services!", HttpStatus.OK);
        }
        return new ResponseEntity<>("You are not authorized user!", HttpStatus.NOT_FOUND);
    }
}
