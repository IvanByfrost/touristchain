package com.touristchain.geneva.partners;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/** Socio tal como aparece en la vitrina pública. */
public record PartnerShowcase(
        UUID id,
        String name,
        String type,
        String description,
        String website,
        String logo,
        List<String> benefits,
        boolean verified) {

    static PartnerShowcase from(PartnerEntity partner) {
        String seed = partner.getTaxId() != null ? partner.getTaxId() : partner.getCompanyName();
        return new PartnerShowcase(
                partner.getPartnerId(),
                partner.getCompanyName(),
                partner.getBusinessType(),
                partner.getCompanyDescription() != null
                        ? partner.getCompanyDescription()
                        : "Socio TouristChain - " + partner.getCompanyName(),
                partner.getWebsite() != null ? partner.getWebsite() : "#",
                partner.getLogoUrl() != null
                        ? partner.getLogoUrl()
                        : "https://api.dicebear.com/7.x/shapes/svg?seed="
                                + URLEncoder.encode(seed, StandardCharsets.UTF_8),
                partner.getBenefits() == null ? List.of()
                        : Arrays.stream(partner.getBenefits().split("\n"))
                                .map(String::trim)
                                .filter(b -> !b.isEmpty())
                                .toList(),
                Boolean.TRUE.equals(partner.getVerified()));
    }
}
