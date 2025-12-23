package com.touristchain.backend.ginebra.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.touristchain.backend.ginebra.dto.AuthResponse;
import com.touristchain.backend.ginebra.dto.LoginRequest;
import com.touristchain.backend.ginebra.dto.UserResponse;
import com.touristchain.backend.ginebra.models.entities.User;
import com.touristchain.backend.ginebra.services.AuthService;
import com.touristchain.backend.ginebra.services.UserService;

@RestController
@RequestMapping("/api/v1/ginebra")
public class UserController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    // ========== 🔐 AUTH ==========

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        AuthResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody User user) {
        User registeredUser = authService.register(user);
        UserResponse response = new UserResponse(
                registeredUser.getId(),
                registeredUser.getUsername(),
                registeredUser.getEmail(),
                registeredUser.getWalletAddress(),
                registeredUser.getUserType(),
                registeredUser.getVerified(),
                registeredUser.getActive(),
                registeredUser.getCreatedAt(),
                registeredUser.getLastLogin());
        return ResponseEntity.ok(response);
    }

    // ========== 👥 USUARIOS ==========

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/users/wallet/{walletAddress}")
    public ResponseEntity<UserResponse> getUserByWallet(@PathVariable String walletAddress) {
        return userService.getUserByWallet(walletAddress)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @RequestBody User userUpdates) {
        try {
            UserResponse updatedUser = userService.updateUser(id, userUpdates);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/users/{id}/deactivate")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long id) {
        try {
            userService.deactivateUser(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/users/{id}/activate")
    public ResponseEntity<Void> activateUser(@PathVariable Long id) {
        try {
            userService.activateUser(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/users/{id}/verify")
    public ResponseEntity<Void> verifyUser(@PathVariable Long id) {
        try {
            userService.verifyUser(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ========== 🔍 FILTROS ==========

    @GetMapping("/users/type/{userType}")
    public ResponseEntity<List<UserResponse>> getUsersByType(@PathVariable String userType) {
        try {
            // Convertir String a Enum
            com.touristchain.backend.ginebra.models.entities.UserType type = com.touristchain.backend.ginebra.models.entities.UserType
                    .valueOf(userType.toUpperCase());

            List<UserResponse> users = userService.getUsersByType(type);
            return ResponseEntity.ok(users);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/users/active")
    public ResponseEntity<List<UserResponse>> getActiveUsers() {
        List<UserResponse> users = userService.getActiveUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/verified")
    public ResponseEntity<List<UserResponse>> getVerifiedUsers() {
        List<UserResponse> users = userService.getVerifiedUsers();
        return ResponseEntity.ok(users);
    }

    // ========== 📊 ESTADÍSTICAS ==========

    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        long totalUsers = userService.countUsers();
        long activeUsers = userService.getActiveUsers().size();
        long verifiedUsers = userService.getVerifiedUsers().size();

        return ResponseEntity.ok(new Object() {
            public final long total = totalUsers;
            public final long active = activeUsers;
            public final long verified = verifiedUsers;
        });
    }
}