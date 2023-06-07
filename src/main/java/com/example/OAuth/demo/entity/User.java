package com.example.OAuth.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class User {
    @Id
    @Column(name = "user_id")
    private String id;
    private String username;
    private String password;
    private String email;
    private boolean verified;
}
