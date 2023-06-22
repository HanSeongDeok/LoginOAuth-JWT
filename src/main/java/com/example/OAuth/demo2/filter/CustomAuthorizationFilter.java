package com.example.OAuth.demo2.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.util.*;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@Slf4j
public class CustomAuthorizationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getServletPath().equals("/api/login") || request.getServletPath().equals("/api/token/refresh")) {
            filterChain.doFilter(request, response);
        } else {
            String authorizationHeader = request.getHeader(AUTHORIZATION);
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                try {
                    String token = authorizationHeader.substring("Bearer ".length());
                    JwtParser jwtParser = Jwts.parserBuilder()
                            .setSigningKey(new SecretKeySpec(Base64.getDecoder().decode("JWTSECRETTOKENDECODERKEYTESTTESTLONGSTRINGRANDOMSTRING"), "HmacSHA256"))
                            .build();

                    Jws<Claims> decodeJwt = jwtParser.parseClaimsJws(token);
                    String userId = decodeJwt.getBody().getId();
                    List<String> roles = (List<String>) decodeJwt.getBody().get("roles");
                    Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    roles.stream().forEach(r -> {
                        authorities.add(new SimpleGrantedAuthority(r));
                    });

                    //** 인가를 위한 AuthenticationToken 객체 생성
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userId, null, authorities);

                    // TODO
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    filterChain.doFilter(request, response);

                } catch (Exception e) {
                    log.error("Error logging in: {}", e.getMessage());
                    response.setHeader("error", e.getMessage());
                    //response.sendError(FORBIDDEN.value());
                    response.setStatus(FORBIDDEN.value());
                    Map<String, String> error = new HashMap<>();
                    error.put("error_message", e.getMessage());
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    new ObjectMapper().writeValue(response.getOutputStream(), error);
                }
            } else {
                filterChain.doFilter(request, response);
            }
        }
    }
}

/**
 * Jws 로 payload 부분 body 부분의 데이터를 가져와 인가 여부를 check 한다.
 *
 *  public String getToken(String token, byte[] decodedKey) {
 *         return Jwts.parserBuilder()
 *                 .setSigningKey(new SecretKeySpec(decodedKey, "HmacSHA256"))
 *                 .build()
 *                 .parseClaimsJws(token)
 *                 .getBody()
 *                 .getSubject();
 *     }
 */
