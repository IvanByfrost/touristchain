package com.touristchain.rome.booking;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

/** Paquetes fijos de la sección "Paquetes de Viaje" del sitio público. */
public enum PackagePlan {
    BASICO("basico", "Paquete Básico", new BigDecimal("799")),
    MEDIO("medio", "Paquete Medio", new BigDecimal("1299")),
    BLACK("black", "Paquete Black", new BigDecimal("2499"));

    private final String code;
    private final String displayName;
    private final BigDecimal price;

    PackagePlan(String code, String displayName, BigDecimal price) {
        this.code = code;
        this.displayName = displayName;
        this.price = price;
    }

    public String code() {
        return code;
    }

    public String displayName() {
        return displayName;
    }

    public BigDecimal price() {
        return price;
    }

    public static Optional<PackagePlan> fromCode(String code) {
        return Arrays.stream(values()).filter(plan -> plan.code.equalsIgnoreCase(code)).findFirst();
    }
}
