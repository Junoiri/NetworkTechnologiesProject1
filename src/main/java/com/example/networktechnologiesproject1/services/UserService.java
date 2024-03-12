package com.example.networktechnologiesproject1.services;

import com.example.networktechnologiesproject1.entities.User;
import com.example.networktechnologiesproject1.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public Optional<User> findById(Integer userId) {
        return userRepository.findById(userId);
    }

    public Iterable<User> findAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(Integer userId) {
        userRepository.deleteById(userId);
    }
}
