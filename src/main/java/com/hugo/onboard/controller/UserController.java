package com.hugo.onboard.controller;

import com.hugo.onboard.model.user.User;
import com.hugo.onboard.service.UserService;
import org.springframework.stereotype.Controller;
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
    public String registerUser(@RequestBody User user) {
        userService.addUserToDatabase(user);
        return "User added to database";
    }
}
