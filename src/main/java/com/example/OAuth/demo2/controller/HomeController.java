package com.example.OAuth.demo2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping({"/home", "/"})
    public String viewUserLoginForm() {
        return "home";
    }
}
