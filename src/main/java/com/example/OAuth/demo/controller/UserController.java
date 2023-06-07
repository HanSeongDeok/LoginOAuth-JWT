package com.example.OAuth.demo.controller;

import com.example.OAuth.demo.entity.User;
import com.example.OAuth.demo.service.EmailService;
import com.example.OAuth.demo.service.UserService;
import jakarta.validation.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class UserController {
    private UserService userService;
    private EmailService emailService;

    @Autowired
    public UserController(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    @GetMapping("/signup")
    public String showSignUpPage(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/signup")
    public String signUp(@ModelAttribute("user") User user) {
        //String userId = userService.join(user);
        emailService.sendVerificationEmail(user);
        return "signup-wait";
    }

    @GetMapping("/verify")
    public String verifyEmail(@RequestParam("token") String token) {
        Optional<User> user = userService.getUserByToken(token);
        if (user != null) {
            // 토큰 값이 일치하고 유효한 경우
            userService.join(user.orElseThrow(NullPointerException::new));
            return "verify-success";
        } else {
            // 토큰 값이 일치하지 않거나 만료된 경우
            return "verify-failure";
        }
    }
}
