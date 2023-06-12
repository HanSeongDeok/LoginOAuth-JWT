package com.example.OAuth.demo.service;

import com.example.OAuth.demo.config.SecurityConfig;
import com.example.OAuth.demo.details.MemberDetails;
import com.example.OAuth.demo.entity.User;
import com.example.OAuth.demo.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.validation.constraints.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Service
@Transactional
public class UserService implements UserDetailsService{
    private UserRepository userRepository;
    private JavaMailSender javaMailSender;
    private SecurityConfig securityConfig;
    private String jwtSecret;
    private long jwtExpirationMs;

    @Autowired
    public UserService(SecurityConfig securityConfig, UserRepository userRepository, JavaMailSender javaMailSender,
                       @Value("${jwt.secret}") String jwtSecret,
                       @Value("${jwt.expiration-ms}") long jwtExpirationMs) {
        this.securityConfig = securityConfig;
        this.userRepository = userRepository;
        this.javaMailSender = javaMailSender;
        this.jwtSecret = jwtSecret;
        this.jwtExpirationMs = jwtExpirationMs;
    }
    public String join(User user) {
        user.setPassword(securityConfig.passwordEncoder()
                .encode(user.getPassword()));
        user.setRole("USER");
        user.setUsername(user.getId());
        validateDuplicateMember(user);
        userRepository.save(user);
        sendEmailVerification(user);
        return user.getId();
    }

    public boolean verifyEmail(String token) {
        try {
            byte[] decodedKey = Base64.getDecoder().decode(jwtSecret);

            // 바이트 배열 길이가 32바이트 미만인 경우, 길이를 32바이트로 확장
            if (decodedKey.length < 32) {
                byte[] extendedKey = new byte[32];
                System.arraycopy(decodedKey, 0, extendedKey, 0, decodedKey.length);
                decodedKey = extendedKey;
            }

            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(new SecretKeySpec(decodedKey, "HmacSHA256"))
                    .build()
                    .parseClaimsJws(token);

            // Extract user ID from the token
            String userId = claimsJws.getBody().getSubject();

            User user = userRepository.findOne(userId);
            if (user == null) {
                throw new NullPointerException("Null");
            } else {
                user.setVerified(true);
            }

            userRepository.save(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void sendEmailVerification(User user) {
        byte[] decodedKey = Base64.getDecoder().decode(jwtSecret);

        // 바이트 배열 길이가 32바이트 미만인 경우, 길이를 32바이트로 확장
        if (decodedKey.length < 32) {
            byte[] extendedKey = new byte[32];
            System.arraycopy(decodedKey, 0, extendedKey, 0, decodedKey.length);
            decodedKey = extendedKey;
        }

        String token = Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(new SecretKeySpec(decodedKey,"HmacSHA256"))
                .compact();

        // Send email with verification link containing the token
        String verificationLink = "http://localhost:8080/verify-email?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Email Verification");
        message.setText("Click the link below to verify your email:\n" + verificationLink);

        javaMailSender.send(message);
    }

    public User getUserById(String id) {
        return userRepository.findOne(id);
    }

    public User getUserByToken(String token) {
        return userRepository.findByToken(token);
    }

    private boolean validateDuplicateMember(User user) {
        User findUser = getUserById(user.getId());
        if (findUser!=null){
            throw new IllegalStateException("이미 존재하는 회원.");
        } else {
            return true;
        }
    }
    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        User user = userRepository.findOne(id);
        return new MemberDetails(user);
    }
}
