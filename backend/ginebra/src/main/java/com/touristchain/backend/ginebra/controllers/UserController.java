package com.touristchain.backend.ginebra.controllers;

import com.touristchain.backend.ginebra.models.User;
import com.touristchain.backend.ginebra.models.Provider;
import com.touristchain.backend.ginebra.models.LoginRequest;
import com.touristchain.backend.ginebra.models.AuthResponse;
import com.touristchain.backend.ginebra.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/ginebra")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserServices userService;

    // 🔐 AUTH endpoints
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            User newUser = userService.register(user);
            return ResponseEntity.ok(newUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        AuthResponse response = userService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    // 👥 USER management
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.findById(id);
        return user.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/users/wallet/{walletAddress}")
    public ResponseEntity<User> getUserByWallet(@PathVariable String walletAddress) {
        Optional<User> user = userService.findByWalletAddress(walletAddress);
        return user.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        Optional<User> updatedUser = userService.updateUser(id, userDetails);
        return updatedUser.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/users/{id}/deactivate")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long id) {
        if (userService.deactivateUser(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/users/{id}/verify")
    public ResponseEntity<Void> verifyUser(@PathVariable Long id) {
        if (userService.verifyUser(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    // 🏢 PROVIDER endpoints
    @PostMapping("/providers")
    public ResponseEntity<?> registerProvider(@RequestBody Provider provider) {
        try {
            Provider newProvider = userService.registerProvider(provider);
            return ResponseEntity.ok(newProvider);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/providers")
    public ResponseEntity<List<Provider>> getAllProviders() {
        List<Provider> providers = userService.getVerifiedProviders();
        return ResponseEntity.ok(providers);
    }

    @GetMapping("/providers/user/{userId}")
    public ResponseEntity<Provider> getProviderByUserId(@PathVariable Long userId) {
        Optional<Provider> provider = userService.getProviderByUserId(userId);
        return provider.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/providers/{id}/verify")
    public ResponseEntity<Void> verifyProvider(@PathVariable Long id) {
        if (userService.verifyProvider(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    // 📊 STATISTICS
    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        Object stats = userService.getUserStatistics();
        return ResponseEntity.ok(stats);
    }

    // 🔍 FILTERS
    @GetMapping("/users/type/{userType}")
    public ResponseEntity<List<User>> getUsersByType(@PathVariable String userType) {
        List<User> users = userService.getUsersByType(userType);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/active")
    public ResponseEntity<List<User>> getActiveUsers() {
        List<User> users = userService.getActiveUsers();
        return ResponseEntity.ok(users);
    }
}