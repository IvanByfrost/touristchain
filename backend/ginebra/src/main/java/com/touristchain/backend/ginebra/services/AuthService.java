package com.touristchain.backend.ginebra.services;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.touristchain.backend.ginebra.dto.AuthResponse;
import com.touristchain.backend.ginebra.dto.LoginRequest;
import com.touristchain.backend.ginebra.dto.UserResponse;
import com.touristchain.backend.ginebra.models.entities.User;
import com.touristchain.backend.ginebra.repositories.UserRepository;

@Service
@Transactional
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Login con email y contraseña
    public AuthResponse login(LoginRequest request) {
        // 1. Buscar por email
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

        if (userOpt.isEmpty()) {
            throw new RuntimeException("Credenciales incorrectas");
        }

        User user = userOpt.get();

        // 2. Validar contraseña (si tiene)
        if (user.getPassword() != null) {
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new RuntimeException("Credenciales incorrectas");
            }
        } else {
            // Usuario sin contraseña (solo wallet)
            throw new RuntimeException("Este usuario requiere autenticación con wallet");
        }

        // 3. Validar activo
        if (!user.getActive()) {
            throw new RuntimeException("Usuario desactivado");
        }

        // 4. Actualizar último login
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        // 5. Token simple
        String token = "TOKEN-" + user.getId() + "-" + System.currentTimeMillis();

        // 6. Convertir a UserResponse
        UserResponse userResponse = toUserResponse(user);

        return new AuthResponse(token, "Bearer", userResponse);
    }

    // Registro
    public User register(User user) {
        // 1. Validar email único
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email ya registrado");
        }

        // 2. Hashear contraseña si existe
        if (user.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        // 3. Timestamps
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setActive(true);

        return userRepository.save(user);
    }

    // Validar token
    public boolean validateToken(String token) {
        return token != null && token.startsWith("TOKEN-");
    }

    // Obtener usuario desde token
    public Optional<UserResponse> getUserFromToken(String token) {
        if (!validateToken(token)) {
            return Optional.empty();
        }

        try {
            String[] parts = token.split("-");
            Long userId = Long.parseLong(parts[1]);
            return userRepository.findById(userId)
                    .map(this::toUserResponse);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    // Convertir User → UserResponse
    private UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getWalletAddress(),
                user.getUserType(),
                user.getVerified(),
                user.getActive(),
                user.getCreatedAt(),
                user.getLastLogin());
    }
}