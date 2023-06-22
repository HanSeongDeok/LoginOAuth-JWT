package com.example.OAuth.demo2.api;

import com.example.OAuth.demo2.Service.UserService;
import com.example.OAuth.demo2.domain.Role;
import com.example.OAuth.demo2.domain.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserResource {
    private final UserService userService;
    @GetMapping("/users")
    public ResponseEntity<List<User>> getUser() {
        return ResponseEntity.ok().body(userService.getUsers());
    }

    @PostMapping("/user/save")
    public ResponseEntity<User> saveUser(@RequestBody User user) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/user/save").toUriString());
        return ResponseEntity.created(uri).body(userService.saveUser(user));
    }

    @PostMapping("/role/save")
    public ResponseEntity<Role> saveUser(@RequestBody Role role) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/role/save").toUriString());
        return ResponseEntity.created(uri).body(userService.saveRole(role));
    }

    @PostMapping("/role/addtouser")
    public ResponseEntity<?> addRoleToUser(@RequestBody RoleToUserForm form) {
        userService.addRoleToUser(form.getUserName(), form.getRoleName());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                String refresh_token = authorizationHeader.substring("Bearer ".length());
                JwtParser jwtParser = Jwts.parserBuilder()
                        .setSigningKey(new SecretKeySpec(Base64.getDecoder().decode("JWTSECRETTOKENDECODERKEYTESTTESTLONGSTRINGRANDOMSTRING"), "HmacSHA256"))
                        .build();

                Jws<Claims> decodeJwt = jwtParser.parseClaimsJws(refresh_token);
                String userId = decodeJwt.getBody().getId();
                User user = userService.getUser(userId);

                Claims claims = Jwts.claims().setSubject(user.getUserId());
                claims.put("roles", user.getRoles().stream().map(Role::getName).collect(Collectors.toList()));

                String access_token = Jwts.builder()
                        .setClaims(claims)
                        .setExpiration(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                        .setIssuer(request.getRequestURL().toString())
                        .signWith(new SecretKeySpec(Base64.getDecoder().decode("JWTSECRETTOKENDECODERKEYTESTTESTLONGSTRINGRANDOMSTRING"), "HmacSHA256"))
                        .compact();

                Map<String, String> tokens = new HashMap<>();
                tokens.put("access_token", access_token);
                tokens.put("refresh_token", refresh_token);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), tokens);

            } catch (Exception e) {
                response.setHeader("error", e.getMessage());
                //response.sendError(FORBIDDEN.value());
                response.setStatus(FORBIDDEN.value());
                Map<String, String> error = new HashMap<>();
                error.put("error_message", e.getMessage());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }
        } else {
            throw new RuntimeException("Refresh token is missing");
        }
    }

    @Data
    class RoleToUserForm {
        private String userName;
        private String roleName;
    }
}
