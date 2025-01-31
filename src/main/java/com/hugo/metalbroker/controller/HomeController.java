package com.hugo.metalbroker.controller;

import com.hugo.metalbroker.utils.JWTUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RestController
public class HomeController {
    @GetMapping("/")
    public String greet() {
        return "Welcome to metal broker where you can trade gold and silver!";
    }
}
