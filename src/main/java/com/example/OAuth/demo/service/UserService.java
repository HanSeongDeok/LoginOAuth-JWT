package com.example.OAuth.demo.service;

import com.example.OAuth.demo.entity.User;
import com.example.OAuth.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UserService {
    private UserRepository userRepository;
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public String join(User user) {
        validateDuplicateMember(user);
        userRepository.save(user);
        return user.getId();
    }

    public Optional<User> getUserByToken(String token) {
        return Optional.ofNullable(userRepository.findByToken(token));
    }

    private void validateDuplicateMember(User user) {
        List<User> findUsers = userRepository.findByUsername(user.getUsername());
        Optional.ofNullable(findUsers)
                .orElseThrow(()->new IllegalStateException("이미 존재하는 회원."));
        /*if (!findUsers.isEmpty()){
            throw new IllegalStateException("이미 존재하는 회원.");
        }*/
    }
}
