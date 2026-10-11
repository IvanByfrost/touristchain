package com.touristchain.geneva.admin;

import com.touristchain.geneva.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Endpoints del panel de administración (restringidos a admin en SecurityConfig). */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AdminAccountController {

    private final AdminAccountService adminAccountService;

    @GetMapping("/users")
    public List<AdminUser> listUsers() {
        return adminAccountService.listUsers();
    }

    @PostMapping("/users")
    public ResponseEntity<AdminUser> createUser(@RequestBody AdminUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminAccountService.createUser(request));
    }

    @PutMapping("/users/{userId}")
    public AdminUser updateUser(@AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID userId, @RequestBody AdminUserRequest request) {
        return adminAccountService.updateUser(principal.getUserId(), userId, request);
    }

    @DeleteMapping("/users/{userId}")
    public Map<String, String> deleteUser(@AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID userId) {
        adminAccountService.disableUser(principal.getUserId(), userId);
        return Map.of("message", "Usuario eliminado");
    }

    @PostMapping("/partners/accounts")
    public ResponseEntity<AdminUser> createPartnerAccount(@RequestBody CreatePartnerAccountRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminAccountService.createPartnerAccount(request));
    }

    @PutMapping("/partners/accounts/{userId}")
    public AdminUser updatePartnerAccount(@PathVariable UUID userId,
            @RequestBody UpdatePartnerAccountRequest request) {
        return adminAccountService.updatePartnerAccount(userId, request);
    }
}
