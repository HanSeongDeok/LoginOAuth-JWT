package com.example.OAuth.demo.service;

import com.example.OAuth.demo.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(User user) {
        String verificationUrl = "http://localhost/verify?token=" + user.getId(); // 인증 URL 생성
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("회원 가입 인증 이메일");
        message.setText("회원 가입을 완료하려면 아래 링크를 클릭하여 이메일을 인증해주세요.\n" + verificationUrl);

        mailSender.send(message);
    }
}
