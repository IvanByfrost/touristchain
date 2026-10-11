package com.touristchain.geneva.admin;

import com.touristchain.geneva.partners.PartnerEntity;
import com.touristchain.geneva.users.UserEntity;

import java.util.UUID;

/** Usuario tal como lo muestra el panel de administración. */
record AdminUser(
        UUID id,
        String name,
        String firstName,
        String lastName,
        String email,
        String role,
        String status,
        String phone,
        String address,
        String createdAt,
        String lastLogin,
        PartnerProfile partnerProfile) {

    static AdminUser from(UserEntity user, String role, PartnerEntity partner) {
        String fullName = user.getLastName() == null
                ? user.getFirstName()
                : user.getFirstName() + " " + user.getLastName();
        return new AdminUser(
                user.getUserId(),
                partner != null ? partner.getCompanyName() : fullName,
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                role,
                user.getStatusAccount().label(),
                partner != null && partner.getContactPhone() != null ? partner.getContactPhone() : user.getPhone(),
                user.getAddress(),
                user.getCreatedAt() == null ? null : user.getCreatedAt().toLocalDate().toString(),
                null,
                partner == null ? null : PartnerProfile.from(partner));
    }
}

record PartnerProfile(
        UUID partnerId,
        String companyName,
        String nit,
        String contactName,
        String phone,
        String address,
        String city,
        String type) {

    static PartnerProfile from(PartnerEntity partner) {
        return new PartnerProfile(
                partner.getPartnerId(),
                partner.getCompanyName(),
                partner.getTaxId(),
                partner.getContactName(),
                partner.getContactPhone(),
                partner.getAddress(),
                partner.getCity(),
                partner.getBusinessType());
    }
}

/** Alta o edición de un usuario desde el panel (rol "user" o "admin"). */
record AdminUserRequest(
        String name,
        String email,
        String password,
        String role,
        String status,
        String phone,
        String address) {
}

/** Alta de un socio por el administrador: usuario de ingreso + perfil de empresa. */
record CreatePartnerAccountRequest(
        String companyName,
        String nit,
        String contactName,
        String email,
        String password,
        String phone,
        String address,
        String city,
        String type,
        String description,
        String website) {
}

record UpdatePartnerAccountRequest(
        String companyName,
        String nit,
        String contactName,
        String phone,
        String address,
        String city,
        String type,
        String status) {
}
