package com.touristchain.geneva.auth;

import com.touristchain.geneva.administrators.AdministratorRepository;
import com.touristchain.geneva.partners.PartnerRepository;
import com.touristchain.geneva.security.JwtService;
import com.touristchain.geneva.tourists.TouristEntity;
import com.touristchain.geneva.tourists.TouristRepository;
import com.touristchain.geneva.tourists.TouristService;
import com.touristchain.geneva.tourists.TouristService.CreateTouristRequest;
import com.touristchain.geneva.users.UserEntity;
import com.touristchain.geneva.users.UserService;
import com.touristchain.geneva.users.UserService.UserProfileUpdateRequest;
import com.touristchain.geneva.users.UserService.UserRegistrationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    static final String ROLE_ADMIN = "admin";
    static final String ROLE_PARTNER = "socio";
    static final String ROLE_TOURIST = "user";

    private static final int MIN_PASSWORD_LENGTH = 8;

    private final UserService userService;
    private final TouristService touristService;
    private final TouristRepository touristRepository;
    private final AdministratorRepository administratorRepository;
    private final PartnerRepository partnerRepository;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse login(String email, String password) {
        if (isBlank(email) || isBlank(password)) {
            throw new IllegalArgumentException("Email y contraseña son requeridos");
        }
        return issueToken(userService.login(email, password));
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (isBlank(request.email()) || isBlank(request.password())) {
            throw new IllegalArgumentException("Email y contraseña son requeridos");
        }
        if (request.password().length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres");
        }
        Name name = Name.resolve(request.name(), request.firstName(), request.lastName());
        if (name.first() == null) {
            throw new IllegalArgumentException("El nombre es requerido");
        }

        UserEntity user = userService.registerUser(new UserRegistrationRequest(
                name.first(), request.secondName(), name.last(), request.secondLastName(),
                request.dateOfBirth(), request.gender(), request.bloodType(),
                request.credentialType(), request.credentialNumber(),
                request.email(), blankToNull(request.phone()), request.address(), request.password()));

        touristService.createTouristProfile(user.getUserId(),
                new CreateTouristRequest(user.getUserId(), null, null, null, null, null));

        return issueToken(user);
    }

    @Transactional(readOnly = true)
    public AuthUser me(UUID userId) {
        UserEntity user = userService.getUserById(userId);
        return toAuthUser(user, resolveRole(userId));
    }

    @Transactional
    public AuthUser updateProfile(UUID userId, ProfileUpdateRequest request) {
        Name name = Name.resolve(request.name(), request.firstName(), request.lastName());
        userService.updateProfile(userId, new UserProfileUpdateRequest(
                name.first(), null, name.last(), null, request.phone(),
                request.address() != null ? request.address() : request.location()));
        return me(userId);
    }

    @Transactional
    public void changePassword(UUID userId, String currentPassword, String newPassword) {
        if (isBlank(currentPassword) || newPassword == null || newPassword.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("Contraseña actual y nueva válida (mínimo 8 caracteres) son requeridas");
        }
        userService.changePassword(userId, currentPassword, newPassword);
    }

    private AuthResponse issueToken(UserEntity user) {
        String role = resolveRole(user.getUserId());
        AuthUser authUser = toAuthUser(user, role);
        String token = jwtService.generateToken(user.getUserId(), user.getEmail(), authUser.name(), role);
        return new AuthResponse(token, authUser);
    }

    private String resolveRole(UUID userId) {
        if (administratorRepository.existsByUserId(userId)) {
            return ROLE_ADMIN;
        }
        if (partnerRepository.existsByUserId(userId)) {
            return ROLE_PARTNER;
        }
        return ROLE_TOURIST;
    }

    private AuthUser toAuthUser(UserEntity user, String role) {
        Integer loyaltyPoints = touristRepository.findByUserId(user.getUserId())
                .map(TouristEntity::getLoyaltyPoints)
                .orElse(0);
        return AuthUser.from(user, role, loyaltyPoints);
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static String blankToNull(String value) {
        return isBlank(value) ? null : value.trim();
    }

    record Name(String first, String last) {
        static Name resolve(String fullName, String firstName, String lastName) {
            if (!isBlank(firstName)) {
                return new Name(firstName.trim(), blankToNull(lastName));
            }
            if (isBlank(fullName)) {
                return new Name(null, null);
            }
            String[] parts = fullName.trim().split("\\s+", 2);
            return new Name(parts[0], parts.length > 1 ? parts[1] : null);
        }
    }
}
