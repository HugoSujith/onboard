package com.hugo.metalbroker.controller;

import java.util.Map;

import com.hugo.metalbroker.model.user.BalanceDTO;
import com.hugo.metalbroker.model.user.UserDTO;
import com.hugo.metalbroker.repository.UserRepo;
import com.hugo.metalbroker.service.UserService;
import com.hugo.metalbroker.utils.JWTUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
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

    private final UserService userServiceImpl;
    private final JWTUtils jwtUtils;

    public UserController(UserService userServiceImpl, JWTUtils jwtUtils) {
        this.userServiceImpl = userServiceImpl;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("register")
    public ResponseEntity<Boolean> registerUser(@RequestBody UserDTO user) {
        boolean serviceResponse = userServiceImpl.addUsersToDB(user);
        if (serviceResponse) {
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
    public ResponseEntity<String> fetchUserBalance(HttpServletRequest request, HttpServletResponse response) {
        BalanceDTO userBalance = userServiceImpl.getBalance(request);
        jwtUtils.generateRefreshToken(request, response);
        return new ResponseEntity<>("Your current balance is: " + String.format("%.2f", userBalance.getBalance()), HttpStatus.OK);
    }

    @PutMapping("logout")
    public ResponseEntity<String> logoutUser(HttpServletRequest request) {
        if (userServiceImpl.logout(request)) {
            return new ResponseEntity<>("You have successfully logged out.", HttpStatus.OK);
        }
        return new ResponseEntity<>("You are not authorized user!", HttpStatus.NOT_FOUND);
    }
}
