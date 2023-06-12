package com.example.OAuth.demo.controller;

import com.example.OAuth.demo.details.MemberDetails;
import com.example.OAuth.demo.entity.User;
import com.example.OAuth.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {
    private UserService userService;
   // private EmailService emailService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
       // this.emailService = emailService;
    }

    @GetMapping("/signup")
    public String showSignUpPage(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/signup")
    public String signUp(@ModelAttribute("user") User user) {
        userService.join(user);
        System.out.println("이메일로 들어가 인증 받아야 함.");
        //emailService.sendVerificationEmail(userService.getUserById(userId));
        return "login";
    }

    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        if (userService.verifyEmail(token)) {
            return ResponseEntity.ok("Email verification successful");
        } else {
            return ResponseEntity.badRequest().body("Email verification failed");
        }
    }

    /**
     * 유저 페이지
     * @param model
     * @param authentication
     * @return
     */
    @GetMapping("/")
    public String userAccess(Model model, Authentication authentication) {
        //Authentication 객체를 통해 유저 정보를 가져올 수 있다.
        MemberDetails userDetail = (MemberDetails)authentication.getPrincipal();  //userDetail 객체를 가져옴
        model.addAttribute("info", userDetail.getUsername());      //유저 이메일
        return "user-access";
    }
}
