package com.example.OAuth.demo.controller;

import com.example.OAuth.demo.service.VerifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    private VerifyService verifyService;

    @Autowired
    public LoginController(VerifyService verifyService) {
        this.verifyService = verifyService;
    }

    @GetMapping("/")
    public String welcome(Model model) {
        return "welcome/home";
    }
    @GetMapping("/login")
    public String login(Model model) {
        return "login";
    }

}
