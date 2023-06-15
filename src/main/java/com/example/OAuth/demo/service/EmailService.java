package com.example.OAuth.demo.service;

import com.example.OAuth.demo.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

@Service
public class EmailService {
    private final String ADMIN_ADDRESS = "hsd970730@naver.com";
    private String jwtSecret;
    private long jwtExpirationMs;
    private JavaMailSender javaMailSender;
    private UserService userService;

    @Autowired
    public EmailService(JavaMailSender javaMailSender,
                        UserService userService,
                        @Value("${jwt.secret}") String jwtSecret,
                        @Value("${jwt.expiration-ms}") long jwtExpirationMs) {
        this.javaMailSender = javaMailSender;
        this.jwtSecret = jwtSecret;
        this.jwtExpirationMs = jwtExpirationMs;
        this.userService = userService;
    }

    /**
     * 토큰을 보낸 메일을 인증 하기 위한 기능
     * @param token
     * @param userService
     * @return
     */
    public boolean verifyEmail(String token, UserService userService) {
        // Extract user ID from the token
            return Optional.ofNullable(userService
                            .getUserById(getUserIdForAuthentication(token)))
                    .map(user -> true)
                    .orElseGet(() -> false);
    }

    /**
     * 인증을 위한 이메일 전송 기능
     * @param user
     * @throws MessagingException
     */
    public void sendEmailVerification(User user) throws MessagingException {
        // Send email with verification link containing the token
        String verificationLink = "http://localhost:8080/verify-email?token="
                + createHmacSHA256Token(user);

        //SimpleMailMessage message = new SimpleMailMessage();
        MimeMessage message = javaMailSender.createMimeMessage();
        message.addRecipients(Message.RecipientType.TO, user.getEmail());
        message.setSubject("Email Verification");
        message.setText("Click the link below to verify your email:\n" + verificationLink);
        message.setFrom(ADMIN_ADDRESS);

        javaMailSender.send(message);
    }

    // Pipe Line ------------------------------------------------------------

    // 권한정보 획득
    // Spring Security 인증과정에서 권한확인을 위한 기능
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userService.loadUserByUsername(getUserIdForAuthentication(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }


    /**
     * 메일로 인증되어 받아온 토큰 값과 Decode 키로 UserId 받아옴
     * @param token
     * @return
     */
    public String getUserIdForAuthentication(String token) {
        return getToken(token, getDecodeKey(jwtSecret));
    }

    /**
     * 인증 토큰 생성
     * @param user
     * @return
     */
    private String createHmacSHA256Token(User user) {
        return Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .setClaims(Jwts.claims().setSubject(user.getRole()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(new SecretKeySpec(getDecodeKey(jwtSecret), "HmacSHA256"))
                .compact();
    }

    /**
     * DecodeKey 가져옴
     * @return
     */
    private byte[] getDecodeKey(String jwtSecret) {
        byte[] decodedKey = Base64.getDecoder().decode(jwtSecret);
        // 바이트 배열 길이가 32바이트 미만인 경우, 길이를 32바이트로 확장
        if (decodedKey.length < 32) {
            decodedKey = getExtendDecodeKey(decodedKey);
        }
        return decodedKey;
    }

    /**
     * decodedKey 배열 길이가 32바이트 미만인 경우 확장 32바이트로 확장
     * @param decodedKey
     * @return
     */
    private byte[] getExtendDecodeKey(byte[] decodedKey) {
        byte[] extendedKey = new byte[32];
        System.arraycopy(decodedKey, 0, extendedKey, 0, decodedKey.length);
        decodedKey = extendedKey;
        return decodedKey;
    }

    /**
     * 인증을 위한 토큰 가져오기
     * @param token
     * @param decodedKey
     * @return
     */
    private String getToken(String token, byte[] decodedKey) {
        return Jwts.parserBuilder()
                .setSigningKey(new SecretKeySpec(decodedKey, "HmacSHA256"))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
