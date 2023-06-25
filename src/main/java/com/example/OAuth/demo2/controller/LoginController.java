package com.example.OAuth.demo2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    @GetMapping("/login")
    public String viewUserLoginForm() {
        return "login/login-form";
    }

    @GetMapping("/signup")
    public String viewUserSignupForm() {
        return "login/signup-form";
    }
}
