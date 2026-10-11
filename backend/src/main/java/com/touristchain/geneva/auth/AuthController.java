package com.touristchain.geneva.auth;

import com.touristchain.geneva.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request.email(), request.password());
    }

    @GetMapping("/me")
    public AuthUser me(@AuthenticationPrincipal UserPrincipal principal) {
        return authService.me(principal.getUserId());
    }

    @PutMapping("/profile")
    public AuthUser updateProfile(@AuthenticationPrincipal UserPrincipal principal,
            @RequestBody ProfileUpdateRequest request) {
        return authService.updateProfile(principal.getUserId(), request);
    }

    @PatchMapping("/password")
    public Map<String, String> changePassword(@AuthenticationPrincipal UserPrincipal principal,
            @RequestBody ChangePasswordRequest request) {
        authService.changePassword(principal.getUserId(), request.currentPassword(), request.newPassword());
        return Map.of("message", "Contraseña actualizada exitosamente");
    }
}
