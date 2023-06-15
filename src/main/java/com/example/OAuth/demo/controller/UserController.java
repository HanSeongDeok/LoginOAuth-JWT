package com.example.OAuth.demo.controller;

import com.example.OAuth.demo.details.UserEntityDetails;
import com.example.OAuth.demo.entity.User;
import com.example.OAuth.demo.service.EmailService;
import com.example.OAuth.demo.service.UserService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
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
    public String signUp(@ModelAttribute("user") User user) throws MessagingException {
        userService.join(user);
        System.out.println("이메일로 들어가 인증 받아야 함.");
        emailService.sendEmailVerification(userService.getUserById(user.getId()));
        return "redirect:login";
    }

    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam("token") String token) {
        if (emailService.verifyEmail(token, userService)) {
            System.out.println("인증 성공했습니다.");
            userService.changeVerified(emailService.getUserIdForAuthentication(token));
            return "redirect:login";
        } else {
            return "redirect:logout";
        }
    }

    /**
     * 유저 페이지
     * @param model
     * @param authentication
     * @return
     */
   @GetMapping("/user-access")
    public String userAccess(Model model, Authentication authentication) {
        //Authentication 객체를 통해 유저 정보를 가져올 수 있다.
        UserEntityDetails userDetail = (UserEntityDetails)authentication.getPrincipal();    //userDetail 객체를 가져옴
        model.addAttribute("info", userDetail.getUsername());      //유저 이메일
        return "user-access";
    }
}
