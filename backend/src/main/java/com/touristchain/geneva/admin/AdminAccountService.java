package com.touristchain.geneva.admin;

import com.touristchain.geneva.administrators.AdministratorEntity;
import com.touristchain.geneva.administrators.AdministratorEntity.AdminLevel;
import com.touristchain.geneva.administrators.AdministratorRepository;
import com.touristchain.geneva.administrators.AdministratorService;
import com.touristchain.geneva.partners.PartnerEntity;
import com.touristchain.geneva.partners.PartnerRepository;
import com.touristchain.geneva.partners.PartnerService;
import com.touristchain.geneva.partners.PartnerService.UpdatePartnerRequest;
import com.touristchain.geneva.tourists.TouristRepository;
import com.touristchain.geneva.tourists.TouristService;
import com.touristchain.geneva.tourists.TouristService.CreateTouristRequest;
import com.touristchain.geneva.users.PersonName;
import com.touristchain.geneva.users.UserEntity;
import com.touristchain.geneva.users.UserRepository;
import com.touristchain.geneva.users.UserService;
import com.touristchain.geneva.users.UserService.UserProfileUpdateRequest;
import com.touristchain.geneva.users.UserService.UserRegistrationRequest;
import com.touristchain.geneva.users.enums.AccountStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.touristchain.geneva.auth.AuthService.ROLE_ADMIN;
import static com.touristchain.geneva.auth.AuthService.ROLE_PARTNER;
import static com.touristchain.geneva.auth.AuthService.ROLE_TOURIST;

/**
 * Gestión de cuentas desde el panel de administración: usuarios (turistas y
 * administradores) y socios (usuario de ingreso + perfil de empresa con NIT).
 */
@Service
@RequiredArgsConstructor
public class AdminAccountService {

    private static final int MIN_PASSWORD_LENGTH = 8;

    private final UserService userService;
    private final UserRepository userRepository;
    private final TouristService touristService;
    private final TouristRepository touristRepository;
    private final AdministratorService administratorService;
    private final AdministratorRepository administratorRepository;
    private final PartnerService partnerService;
    private final PartnerRepository partnerRepository;

    @Transactional(readOnly = true)
    public List<AdminUser> listUsers() {
        Set<UUID> adminIds = administratorRepository.findAll().stream()
                .map(AdministratorEntity::getUserId)
                .collect(Collectors.toSet());
        Map<UUID, PartnerEntity> partners = partnerRepository.findAll().stream()
                .collect(Collectors.toMap(PartnerEntity::getUserId, Function.identity()));

        return userRepository.findAll().stream()
                .filter(user -> user.getStatusAccount() != AccountStatus.disabled)
                .sorted(Comparator.comparing(UserEntity::getCreatedAt,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .map(user -> {
                    PartnerEntity partner = partners.get(user.getUserId());
                    String role = adminIds.contains(user.getUserId()) ? ROLE_ADMIN
                            : partner != null ? ROLE_PARTNER : ROLE_TOURIST;
                    return AdminUser.from(user, role, partner);
                })
                .toList();
    }

    @Transactional
    public AdminUser createUser(AdminUserRequest request) {
        String role = requireUserOrAdminRole(request.role());
        if (isBlank(request.email())) {
            throw new IllegalArgumentException("El email es requerido");
        }
        requireValidPassword(request.password());
        PersonName name = requireName(request.name());

        UserEntity user = userService.registerUser(new UserRegistrationRequest(
                name.first(), null, name.last(), null, null, null, null, null, null,
                request.email(), blankToNull(request.phone()), blankToNull(request.address()),
                request.password()));
        UUID userId = user.getUserId();

        if (ROLE_ADMIN.equals(role)) {
            administratorService.createAdministratorProfile(userId, AdminLevel.MODERATOR);
        } else {
            createTouristProfile(userId);
        }
        AccountStatus status = AccountStatus.fromLabel(request.status());
        userService.setAccountStatus(userId, status != null ? status : AccountStatus.active);
        return getUser(userId);
    }

    @Transactional
    public AdminUser updateUser(UUID currentUserId, UUID userId, AdminUserRequest request) {
        UserEntity user = userService.getUserById(userId);
        String currentRole = resolveRole(userId);
        boolean self = userId.equals(currentUserId);

        if (!isBlank(request.role()) && !request.role().equals(currentRole)) {
            if (ROLE_PARTNER.equals(currentRole)) {
                throw new IllegalArgumentException("Los socios se gestionan desde el módulo de socios");
            }
            String newRole = requireUserOrAdminRole(request.role());
            if (self) {
                throw new IllegalArgumentException("No puedes quitarte el rol de administrador");
            }
            if (ROLE_ADMIN.equals(newRole)) {
                administratorService.createAdministratorProfile(userId, AdminLevel.MODERATOR);
            } else {
                administratorService.deleteAdministratorProfileByUserId(userId);
                if (!touristRepository.existsByUserId(userId)) {
                    createTouristProfile(userId);
                }
            }
        }

        AccountStatus status = AccountStatus.fromLabel(request.status());
        if (status != null && status != user.getStatusAccount()) {
            if (self) {
                throw new IllegalArgumentException("No puedes cambiar el estado de tu propia cuenta");
            }
            userService.setAccountStatus(userId, status);
        }

        PersonName name = PersonName.resolve(request.name(), null, null);
        userService.updateProfile(userId, new UserProfileUpdateRequest(
                name.first(), null, name.last(), null, request.phone(), request.address()));
        if (!isBlank(request.email())) {
            userService.changeEmail(userId, request.email());
        }
        if (!isBlank(request.password())) {
            requireValidPassword(request.password());
            userService.resetPassword(userId, request.password());
        }
        return getUser(userId);
    }

    /** Las cuentas no se borran: se deshabilitan y dejan de aparecer en el panel. */
    @Transactional
    public void disableUser(UUID currentUserId, UUID userId) {
        if (userId.equals(currentUserId)) {
            throw new IllegalArgumentException("No puedes eliminar tu propia cuenta");
        }
        userService.disableUser(userId);
    }

    @Transactional
    public AdminUser createPartnerAccount(CreatePartnerAccountRequest request) {
        if (isBlank(request.companyName()) || isBlank(request.nit()) || isBlank(request.contactName())
                || isBlank(request.email()) || isBlank(request.password())) {
            throw new IllegalArgumentException("Empresa, NIT, contacto, email y contraseña son requeridos");
        }
        requireValidPassword(request.password());
        String nit = request.nit().trim();
        if (partnerRepository.existsByTaxId(nit)) {
            throw new IllegalStateException("El NIT ya está registrado");
        }

        PersonName contact = PersonName.resolve(request.contactName(), null, null);
        UserEntity user = userService.registerUser(new UserRegistrationRequest(
                contact.first(), null, contact.last(), null, null, null, null, null, null,
                request.email(), blankToNull(request.phone()), blankToNull(request.address()),
                request.password()));
        userService.setAccountStatus(user.getUserId(), AccountStatus.active);

        PartnerEntity partner = new PartnerEntity();
        partner.setUserId(user.getUserId());
        partner.setCompanyName(request.companyName().trim());
        partner.setTaxId(nit);
        partner.setBusinessType(isBlank(request.type()) ? "hotel" : request.type());
        partner.setCompanyDescription(blankToNull(request.description()));
        partner.setWebsite(blankToNull(request.website()));
        partner.setContactName(request.contactName().trim());
        partner.setContactEmail(user.getEmail());
        partner.setContactPhone(blankToNull(request.phone()));
        partner.setAddress(blankToNull(request.address()));
        partner.setCity(blankToNull(request.city()));
        partnerRepository.save(partner);

        return getUser(user.getUserId());
    }

    @Transactional
    public AdminUser updatePartnerAccount(UUID userId, UpdatePartnerAccountRequest request) {
        PartnerEntity partner = partnerRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Socio no encontrado"));

        String nit = blankToNull(request.nit());
        if (nit != null && !nit.equals(partner.getTaxId()) && partnerRepository.existsByTaxId(nit)) {
            throw new IllegalStateException("El NIT ya está registrado");
        }
        partnerService.updatePartnerProfile(partner.getPartnerId(), new UpdatePartnerRequest(
                request.companyName(), nit, request.type(), null, null, null, request.phone(), null,
                request.contactName(), request.address(), request.city(), null, null));

        AccountStatus status = AccountStatus.fromLabel(request.status());
        if (status != null) {
            userService.setAccountStatus(userId, status);
        }
        return getUser(userId);
    }

    private AdminUser getUser(UUID userId) {
        UserEntity user = userService.getUserById(userId);
        PartnerEntity partner = partnerRepository.findByUserId(userId).orElse(null);
        return AdminUser.from(user, resolveRole(userId), partner);
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

    private void createTouristProfile(UUID userId) {
        touristService.createTouristProfile(userId, new CreateTouristRequest(userId, null, null, null, null, null));
    }

    private static String requireUserOrAdminRole(String role) {
        if (ROLE_TOURIST.equals(role) || ROLE_ADMIN.equals(role)) {
            return role;
        }
        if (ROLE_PARTNER.equals(role)) {
            throw new IllegalArgumentException("Los socios se crean desde el módulo de socios");
        }
        throw new IllegalArgumentException("Rol no válido: usa 'user' o 'admin'");
    }

    private static void requireValidPassword(String password) {
        if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres");
        }
    }

    private static PersonName requireName(String fullName) {
        PersonName name = PersonName.resolve(fullName, null, null);
        if (name.first() == null) {
            throw new IllegalArgumentException("El nombre es requerido");
        }
        return name;
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static String blankToNull(String value) {
        return isBlank(value) ? null : value.trim();
    }
}
