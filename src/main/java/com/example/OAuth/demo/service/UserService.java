package com.example.OAuth.demo.service;

import com.example.OAuth.demo.config.SecurityConfig;
import com.example.OAuth.demo.details.UserEntityDetails;
import com.example.OAuth.demo.entity.User;
import com.example.OAuth.demo.repository.UserRepository;
import jakarta.mail.MessagingException;
import org.aspectj.apache.bcel.classfile.Module;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UserService implements UserDetailsService{
    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public String join(User user) throws MessagingException {
        user.setPassword(new BCryptPasswordEncoder()
                .encode(user.getPassword()));
        user.setRole("USER");
        user.setUsername(user.getId());
        validateDuplicateMember(user);
        userRepository.save(user);
        return user.getId();
    }
    public void changeVerified(String userId){
        userRepository.updateVerified(userId);
    }
    public User getUserById(String id) {
        return userRepository.findOne(id);
    }
    public User getUserByToken(String token) {
        return userRepository.findByToken(token);
    }

    private boolean validateDuplicateMember(User user) {
        Optional.ofNullable(userRepository.findOne(user.getId())).map(u -> false).ifPresent(u -> {
            throw new IllegalStateException("이미 존재하는 회원");
        });
        return true;
    }
    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        User user = userRepository.findOne(id);
        return new UserEntityDetails(user);
    }
}
