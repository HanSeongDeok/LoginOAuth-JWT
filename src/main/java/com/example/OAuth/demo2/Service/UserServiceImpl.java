package com.example.OAuth.demo2.Service;

import com.example.OAuth.demo2.domain.Role;
import com.example.OAuth.demo2.domain.User;
import com.example.OAuth.demo2.repository.RoleRepository;
import com.example.OAuth.demo2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.apache.bcel.classfile.Module;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    /**
     * 개인 연습 람다로 implement functional interface 구현
     * @param user
     * @return
     */
   /* public UserDetailsService detailsService(){
        return userId -> {
            User user = userRepository.findByUserId(userId);
            Optional.ofNullable(user)
                    .ifPresent(u -> log.info("User Found in the DB And Show Info about User: {}", u.getUserId()));
            if (!Optional.ofNullable(user).isPresent()) {
                log.error("User Not Found in the DB");
                throw new UsernameNotFoundException("User Not Found in the DB");
            }
            return null;
        };
    }  */

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user = userRepository.findByUserId(userId);
        Optional.ofNullable(user)
                .ifPresent(u -> log.info("User Found in the DB And Show Info about User: {}", u.getUserId()));
        if (!Optional.ofNullable(user).isPresent()) {
            log.error("User Not Found in the DB");
            throw new UsernameNotFoundException("User Not Found in the DB");
        }
        return null;
    }

    @Override
    public User saveUser(User user) {
        log.info("Saving new user {} to the database", user.getUserId());
        return userRepository.save(user);
    }

    @Override
    public Role saveRole(Role role) {
        log.info("Saving new role {} to the database", role.getName());
        return roleRepository.save(role);
    }

    @Override
    public void addRoleToUser(String userId, String roleName) {
        log.info("Adding role {} to user {}", roleName, userId);
        User user = userRepository.findByUserId(userId);
        Role role = roleRepository.findByName(roleName);
        user.getRoles().add(role);
    }

    @Override
    public User getUser(String userId) {
        log.info("Fetching user {}", userId);
        return userRepository.findByUserId(userId);
    }

    @Override
    public List<User> getUsers() {
        log.info("Fetching user all users");
        return userRepository.findAll();
    }
}
