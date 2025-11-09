package com.touristchain.services;

import com.touristchain.models.MainUser;
import com.touristchain.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MainUserService {
    private final MainUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public MainUserService(MainUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<MainUser> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public MainUser register(MainUser user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
}
