package com.example.OAuth.demo2.repository;

import com.example.OAuth.demo2.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface  UserRepository extends JpaRepository<User, Long> {
    User findByUserId(String userId);

}
