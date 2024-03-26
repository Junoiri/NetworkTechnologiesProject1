package com.example.networktechnologiesproject1.services;

import com.example.networktechnologiesproject1.entities.User;
import com.example.networktechnologiesproject1.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.server.ResponseStatusException;


import java.util.Collections;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return new org.springframework.security.core.userdetails.User(
                userEntity.getUsername(),
                userEntity.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(userEntity.getRole()))
        );
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public Optional<User> findById(Integer userId) {
        return userRepository.findById(userId);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }


    public Iterable<User> findAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(Integer userId) {
        userRepository.deleteById(userId);
    }
}
