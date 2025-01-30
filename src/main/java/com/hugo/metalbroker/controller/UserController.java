package com.hugo.metalbroker.controller;

import java.util.Map;

import com.hugo.metalbroker.model.user.BalanceDTO;
import com.hugo.metalbroker.model.user.UserDTO;
import com.hugo.metalbroker.repository.UserRepo;
import com.hugo.metalbroker.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RestController
public class UserController {

    private final UserService userServiceImpl;
    private final UserRepo userRepo;

    public UserController(UserService userServiceImpl, UserRepo userRepo) {
        this.userServiceImpl = userServiceImpl;
        this.userRepo = userRepo;
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
    public ResponseEntity<String> loginUser(@RequestBody UserDTO user, HttpServletResponse response) {
        Map.Entry<UserDTO, String> loginResponse = userServiceImpl.login(user, response);
        if (loginResponse.getKey() == user) {
            return new ResponseEntity<>(loginResponse.getValue(), HttpStatus.OK);
        }
        return new ResponseEntity<>("You are not authorized user!", HttpStatus.NOT_FOUND);
    }

    @GetMapping("fetchBalance")
    public ResponseEntity<String> fetchUserBalance(HttpServletRequest request) {
        BalanceDTO userBalance = userServiceImpl.getBalance(request);
        return new ResponseEntity<>("Your current balance is: " + String.format("%.2f", userBalance.getBalance()), HttpStatus.OK);
    }
}
