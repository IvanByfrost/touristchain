package com.touristchain.backend.ginebra.services;

import com.touristchain.backend.ginebra.models.User;
import com.touristchain.backend.ginebra.models.Provider;
import com.touristchain.backend.ginebra.models.LoginRequest;
import com.touristchain.backend.ginebra.models.AuthResponse;
import com.touristchain.backend.ginebra.repositories.UserRepository;
import com.touristchain.backend.ginebra.repositories.ProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProviderRepository providerRepository;

    // Obtener todos los usuarios
    public List<User> findAll() {
        return userRepository.findAll();
    }

    // Buscar usuario por ID
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    // Buscar usuario por wallet address
    public Optional<User> findByWalletAddress(String walletAddress) {
        return userRepository.findByWalletAddress(walletAddress);
    }

    // Buscar usuario por email
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Registrar nuevo usuario
    public User register(User user) {
        // Validar que el wallet no exista
        if (userRepository.existsByWalletAddress(user.getWalletAddress())) {
            throw new RuntimeException("Wallet address ya está registrado");
        }

        // Validar que el email no exista
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email ya está registrado");
        }

        // Validar que el username no exista
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username ya está registrado");
        }

        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setActive(true);
        user.setVerified(false);

        return userRepository.save(user);
    }

    // Login con wallet
    public AuthResponse login(LoginRequest loginRequest) {
        Optional<User> userOpt = userRepository.findByWalletAddress(loginRequest.getWalletAddress());
        
        if (userOpt.isEmpty()) {
            return new AuthResponse(null, null, "Usuario no encontrado");
        }

        User user = userOpt.get();
        
        if (!user.getActive()) {
            return new AuthResponse(null, null, "Usuario desactivado");
        }

        // Aquí iría la validación de la firma blockchain
        boolean isValidSignature = validateSignature(loginRequest.getWalletAddress(), loginRequest.getSignature());
        
        if (!isValidSignature) {
            return new AuthResponse(null, null, "Firma inválida");
        }

        // Actualizar último login
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        // Generar token (simulado)
        String token = generateToken(user);

        return new AuthResponse(user, token, "Login exitoso");
    }

    // Actualizar usuario
    public Optional<User> updateUser(Long id, User userDetails) {
        Optional<User> userOpt = userRepository.findById(id);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            // Actualizar campos permitidos
            if (userDetails.getUsername() != null) {
                user.setUsername(userDetails.getUsername());
            }
            if (userDetails.getEmail() != null) {
                user.setEmail(userDetails.getEmail());
            }
            if (userDetails.getUserType() != null) {
                user.setUserType(userDetails.getUserType());
            }
            
            user.setUpdatedAt(LocalDateTime.now());
            return Optional.of(userRepository.save(user));
        }
        
        return Optional.empty();
    }

    // Desactivar usuario (soft delete)
    public boolean deactivateUser(Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setActive(false);
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
            return true;
        }
        
        return false;
    }

    // Verificar usuario
    public boolean verifyUser(Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setVerified(true);
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
            return true;
        }
        
        return false;
    }

    // Obtener usuarios por tipo
    public List<User> getUsersByType(String userType) {
        return userRepository.findByUserType(userType);
    }

    // Obtener usuarios activos
    public List<User> getActiveUsers() {
        return userRepository.findByActiveTrue();
    }

    // Registrar proveedor
    public Provider registerProvider(Provider provider) {
        // Validar que el usuario existe
        if (provider.getUser() == null || provider.getUser().getId() == null) {
            throw new RuntimeException("Usuario no válido");
        }

        // Validar que el usuario no sea ya proveedor
        Optional<Provider> existingProvider = providerRepository.findByUserId(provider.getUser().getId());
        if (existingProvider.isPresent()) {
            throw new RuntimeException("El usuario ya es un proveedor registrado");
        }

        provider.setCreatedAt(LocalDateTime.now());
        provider.setVerified(false);

        return providerRepository.save(provider);
    }

    // Verificar proveedor
    public boolean verifyProvider(Long providerId) {
        Optional<Provider> providerOpt = providerRepository.findById(providerId);
        
        if (providerOpt.isPresent()) {
            Provider provider = providerOpt.get();
            provider.setVerified(true);
            provider.setVerifiedAt(LocalDateTime.now());
            providerRepository.save(provider);
            return true;
        }
        
        return false;
    }

    // Obtener proveedor por usuario
    public Optional<Provider> getProviderByUserId(Long userId) {
        return providerRepository.findByUserId(userId);
    }

    // Obtener proveedores verificados
    public List<Provider> getVerifiedProviders() {
        return providerRepository.findByVerifiedTrue();
    }

    // Métodos auxiliares privados
    private boolean validateSignature(String walletAddress, String signature) {
        // Aquí iría la lógica real de validación de firma blockchain
        // Por ahora simulamos que siempre es válida
        return signature != null && !signature.trim().isEmpty();
    }

    private String generateToken(User user) {
        // Aquí iría la generación real del JWT
        // Por ahora simulamos un token
        return "token-" + user.getId() + "-" + System.currentTimeMillis();
    }

    // Estadísticas
    public Object getUserStatistics() {
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.findByActiveTrue().size();
        long verifiedUsers = userRepository.findByVerifiedTrue().size();
        
        return new Object() {
            public final long total = totalUsers;
            public final long active = activeUsers;
            public final long verified = verifiedUsers;
            public final long providers = providerRepository.count();
        };
    }
}