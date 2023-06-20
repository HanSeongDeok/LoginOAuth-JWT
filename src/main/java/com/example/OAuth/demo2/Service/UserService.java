package com.example.OAuth.demo2.Service;

import com.example.OAuth.demo2.domain.Role;
import com.example.OAuth.demo2.domain.User;

import java.util.List;

public interface UserService {
    User saveUser(User user);
    Role saveRole(Role role);
    void addRoleToUser(String userId, String roleName);
    User getUser(String userId);
    List<User> getUsers();
}
