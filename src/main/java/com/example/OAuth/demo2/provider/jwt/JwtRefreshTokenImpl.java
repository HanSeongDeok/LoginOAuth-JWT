package com.example.OAuth.demo2.provider.jwt;

import com.example.OAuth.demo2.provider.secrestkey.SecretKeyImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@RequiredArgsConstructor
public class JwtRefreshTokenImpl {
    private final Claims claims;
    private final HttpServletRequest request;
    private SecretKeyImpl secretKey = new SecretKeyImpl();
    private IJwtToken createRefreshToken (Claims claims, HttpServletRequest request) {
        return () -> Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + 30 * 60 * 1000))
                .setIssuer(request.getRequestURL().toString())
                .signWith(secretKey.getSecretKey())
                .compact();
    }
    public String getRefreshToken() {
        return createRefreshToken(claims,request).createJwtToken();
    }
}