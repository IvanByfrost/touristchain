package com.touristchain.backend.ginebra.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.touristchain.backend.ginebra.dto.UserResponse;
import com.touristchain.backend.ginebra.models.entities.User;
import com.touristchain.backend.ginebra.models.entities.UserType;
import com.touristchain.backend.ginebra.repositories.UserRepository;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // ========== CONSULTAS ==========

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<UserResponse> getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Optional<User> findEntityById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<UserResponse> getUserByWallet(String walletAddress) {
        return userRepository.findByWalletAddress(walletAddress)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Optional<UserResponse> getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getUsersByType(UserType userType) {
        return userRepository.findByUserType(userType)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getActiveUsers() {
        return userRepository.findByActiveTrue()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getVerifiedUsers() {
        return userRepository.findByVerifiedTrue()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ========== MUTACIONES ==========

    public UserResponse updateUser(Long id, User userUpdates) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Actualizar solo campos permitidos
        if (userUpdates.getUsername() != null) {
            user.setUsername(userUpdates.getUsername());
        }
        if (userUpdates.getEmail() != null) {
            // Validar email único
            if (!user.getEmail().equals(userUpdates.getEmail()) &&
                    userRepository.existsByEmail(userUpdates.getEmail())) {
                throw new RuntimeException("Email ya está en uso");
            }
            user.setEmail(userUpdates.getEmail());
        }
        if (userUpdates.getWalletAddress() != null) {
            // Validar wallet único
            if (!userUpdates.getWalletAddress().equals(user.getWalletAddress()) &&
                    userRepository.existsByWalletAddress(userUpdates.getWalletAddress())) {
                throw new RuntimeException("Wallet ya está registrado");
            }
            user.setWalletAddress(userUpdates.getWalletAddress());
        }
        if (userUpdates.getUserType() != null) {
            user.setUserType(userUpdates.getUserType());
        }

        user.setUpdatedAt(LocalDateTime.now());
        User updatedUser = userRepository.save(user);

        return toResponse(updatedUser);
    }

    public void deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        user.setActive(false);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    public void activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        user.setActive(true);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    public void verifyUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        user.setVerified(true);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    // ========== UTILIDADES ==========

    private UserResponse toResponse(User user) {
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

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    public boolean existsByWallet(String walletAddress) {
        return userRepository.existsByWalletAddress(walletAddress);
    }

    @Transactional(readOnly = true)
    public long countUsers() {
        return userRepository.count();
    }
}