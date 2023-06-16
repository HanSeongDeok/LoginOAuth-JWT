package com.example.OAuth.demo.provider;

import com.example.OAuth.demo.service.EmailService;
import com.example.OAuth.demo.service.UserService;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {
    private UserService userService;
    private EmailService emailService;
    private final String jwtSecret;
    private final long jwtExpirationMs;
    @Autowired
    public JwtProvider(UserService userService, EmailService emailService,
                       @Value("${jwt.secret}") String jwtSecret,
                       @Value("${jwt.expiration-ms}") long jwtExpirationMs){
        this.userService = userService;
        this.emailService = emailService;
        this.jwtSecret = jwtSecret;
        this.jwtExpirationMs = jwtExpirationMs;
    }
    // 권한정보 획득
    // Spring Security 인증과정에서 권한확인을 위한 기능
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userService.loadUserByUsername(getUserIDbyToken(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 토큰에 담겨있는 유저 ID 획득
    public String getUserIDbyToken(String token) {
        return emailService.getToken(token, emailService.getDecodeKey(jwtSecret));
    }

    // Authorization Header 를 통해 인증을 한다.
    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }
}
