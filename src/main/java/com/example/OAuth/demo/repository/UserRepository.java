package com.example.OAuth.demo.repository;

import com.example.OAuth.demo.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.w3c.dom.stylesheets.LinkStyle;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {
    private EntityManager entityManager;
    @Autowired
    public UserRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void save(User user) {
        entityManager.persist(user);
    }

    public boolean updateVerified(String userId) {
        Optional.ofNullable(entityManager.find(User.class, userId))
                .ifPresentOrElse(u -> u.setVerified(true)
                        ,NullPointerException::new);
        return true;
    }

    public User findOne(String id) {
        return entityManager.find(User.class, id);
    }

    public List<User> findAll() {
        return entityManager.createQuery("select u from Users u",User.class)
                .getResultList();
    }

    public User findByToken(String token) {
        return entityManager.find(User.class, token);
    }

    public List<User> findByUsername(String username){
        return entityManager.createQuery("select u from Users u where u.username =: username", User.class)
                .setParameter("username", username)
                .getResultList();
    }
    public List<User> findByEmail(String email) {
        return entityManager.createQuery("select u from Users u where u.email =: email", User.class)
                .setParameter("email", email)
                .getResultList();
    }
}
