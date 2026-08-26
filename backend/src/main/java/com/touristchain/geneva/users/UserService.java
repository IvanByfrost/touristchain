package com.touristchain.geneva.users;

import com.touristchain.geneva.users.enums.AccountStatus;
import com.touristchain.geneva.users.enums.CredentialType;
import com.touristchain.geneva.users.enums.Gender;
import com.touristchain.geneva.users.enums.ProfileStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Transactional
    public UserEntity registerUser(UserRegistrationRequest request) {
        // Validar que email y credentialNumber no existan
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email ya registrado");
        }
        if (userRepository.existsByCredentialNumber(request.credentialNumber())) {
            throw new IllegalArgumentException("Número de identificación ya registrado");
        }

        UserEntity user = new UserEntity();
        user.setUserId(UUID.randomUUID());
        user.setFirstName(request.firstName());
        user.setSecondName(request.secondName());
        user.setLastName(request.lastName());
        user.setSecondLastName(request.secondLastName());
        user.setDateOfBirth(request.dateOfBirth());
        user.setGender(request.gender());
        user.setCredentialType(request.credentialType());
        user.setCredentialNumber(request.credentialNumber());
        user.setEmail(request.email());
        user.setPhone(request.phone());
        user.setAddress(request.address());
        user.setPasswordHash(passwordEncoder.encode(request.password()).getBytes());
        user.setStatusAccount(AccountStatus.pending);
        user.setStatusProfile(ProfileStatus.pending_profile);

        return userRepository.save(user);
    }

    @Transactional
    public UserEntity login(String email, String rawPassword) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Verificar estado de la cuenta
        if (user.getStatusAccount() == AccountStatus.locked) {
            throw new IllegalStateException("Cuenta bloqueada. Intente más tarde.");
        }
        if (user.getStatusAccount() != AccountStatus.active && user.getStatusAccount() != AccountStatus.pending) {
            throw new IllegalStateException("Cuenta no disponible");
        }

        // Verificar contraseña
        String storedHash = new String(user.getPasswordHash());
        if (!passwordEncoder.matches(rawPassword, storedHash)) {
            user.setFailedAttempts((short) (user.getFailedAttempts() + 1));
            if (user.getFailedAttempts() >= 5) {
                user.setStatusAccount(AccountStatus.locked);
                user.setLockedUntil(LocalDateTime.now().plusMinutes(30));
                user.setLockReason("Demasiados intentos fallidos");
            }
            userRepository.save(user);
            throw new IllegalArgumentException("Credenciales inválidas");
        }

        // Resetear intentos fallidos
        user.setFailedAttempts((short) 0);
        user.setLockedUntil(null);
        user.setLockReason(null);

        // Si estaba pending, activarlo
        if (user.getStatusAccount() == AccountStatus.pending) {
            user.setStatusAccount(AccountStatus.active);
        }

        return userRepository.save(user);
    }

    @Transactional
    public void updateProfile(UUID userId, UserProfileUpdateRequest request) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (request.firstName() != null)
            user.setFirstName(request.firstName());
        if (request.secondName() != null)
            user.setSecondName(request.secondName());
        if (request.lastName() != null)
            user.setLastName(request.lastName());
        if (request.secondLastName() != null)
            user.setSecondLastName(request.secondLastName());
        if (request.phone() != null)
            user.setPhone(request.phone());
        if (request.address() != null)
            user.setAddress(request.address());
        if (user.getStatusProfile() == ProfileStatus.pending_profile &&
                user.getFirstName() != null && user.getLastName() != null &&
                user.getEmail() != null && user.getPhone() != null) {
            user.setStatusProfile(ProfileStatus.active);
        }
        userRepository.save(user);
    }

    @Transactional
    public void changePassword(UUID userId, String currentPassword, String newPassword) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        String storedHash = new String(user.getPasswordHash());
        if (!passwordEncoder.matches(currentPassword, storedHash)) {
            throw new IllegalArgumentException("Contraseña actual incorrecta");
        }
        user.setPasswordHash(passwordEncoder.encode(newPassword).getBytes());
        userRepository.save(user);
    }

    @Transactional
    public void disableUser(UUID userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        user.setStatusAccount(AccountStatus.disabled);
        userRepository.save(user);
    }

    public UserEntity getUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }

    public record UserRegistrationRequest(
            String firstName,
            String secondName,
            String lastName,
            String secondLastName,
            LocalDate dateOfBirth,
            Gender gender,
            String bloodType,
            CredentialType credentialType,
            String credentialNumber,
            String email,
            String phone,
            String address,
            String password) {
    }

    public record LoginRequest(
            String email,
            String password) {
    }

    public record UserProfileUpdateRequest(
            String firstName,
            String secondName,
            String lastName,
            String secondLastName,
            String phone,
            String address) {
    }

    public record ChangePasswordRequest(
            String currentPassword,
            String newPassword) {
    }

}