package com.touristchain.geneva.users;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.touristchain.geneva.users.UserService.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Registro de nuevo usuario.
     * POST /api/v1/users/register
     */
    @PostMapping("/register")
    public ResponseEntity<UserEntity> register(@Valid @RequestBody UserRegistrationRequest request) {
        UserEntity created = userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Inicio de sesión.
     * POST /api/v1/users/login
     */
    @PostMapping("/login")
    public ResponseEntity<UserEntity> login(@Valid @RequestBody LoginRequest request) {
        UserEntity loggedIn = userService.login(request.email(), request.password());
        return ResponseEntity.ok(loggedIn);
    }

    /**
     * Obtener usuario por ID.
     * GET /api/v1/users/{userId}
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserEntity> getUser(@PathVariable UUID userId) {
        UserEntity user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    /**
     * Actualizar perfil de usuario (sin contraseña).
     * PUT /api/v1/users/{userId}
     */
    @PutMapping("/{userId}")
    public ResponseEntity<Void> updateProfile(
            @PathVariable UUID userId,
            @Valid @RequestBody UserProfileUpdateRequest request) {
        userService.updateProfile(userId, request);
        return ResponseEntity.noContent().build();
    }

    /**
     * Cambiar contraseña.
     * PUT /api/v1/users/{userId}/password
     */
    @PutMapping("/{userId}/password")
    public ResponseEntity<Void> changePassword(
            @PathVariable UUID userId,
            @RequestBody ChangePasswordRequest request) {
        userService.changePassword(userId, request.currentPassword(), request.newPassword());
        return ResponseEntity.noContent().build();
    }

    /**
     * Eliminar usuario (deshabilitar cuenta).
     * DELETE /api/v1/users/{userId}
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {
        userService.disableUser(userId);
        return ResponseEntity.noContent().build();
    }
}