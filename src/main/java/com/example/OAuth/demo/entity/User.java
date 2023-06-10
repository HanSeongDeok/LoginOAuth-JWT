package com.example.OAuth.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Table(name = "Users")
public class User{
    @Id
    @Column(name = "user_id")
    private String id;
    private String username;
    private String password;
    private String role;
    private String email;
    @Column(nullable = false)
    private boolean verified;
    @CreationTimestamp
    private Timestamp createDate;
}
