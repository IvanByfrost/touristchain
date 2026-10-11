package com.touristchain.kyoto.car;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

/** Formatos de entrada y salida de vehículos, con los nombres de campo que usa el frontend. */
public final class CarDtos {

    private CarDtos() {
    }

    public record CarRequest(
            @NotBlank(message = "El nombre es obligatorio") @Size(max = 150) String name,
            @NotBlank(message = "El tipo es obligatorio") @Size(max = 50) String type,
            @NotBlank(message = "La categoría es obligatoria")
            @Pattern(regexp = "basico|medio|black", message = "La categoría debe ser basico, medio o black") String category,
            @NotNull(message = "El precio es obligatorio")
            @PositiveOrZero(message = "El precio no puede ser negativo") BigDecimal price,
            @Size(max = 500) String image,
            List<@NotBlank String> features) {
    }

    /** {@code reviews} es el número de reseñas, como lo nombra el frontend. */
    public record CarView(
            Long id,
            String name,
            String type,
            String category,
            double price,
            String image,
            List<String> features,
            double rating,
            Integer reviews) {

        static CarView from(CarEntity car) {
            return new CarView(
                    car.getId(),
                    car.getName(),
                    car.getType(),
                    car.getCategory(),
                    car.getPrice().doubleValue(),
                    car.getImage(),
                    List.copyOf(car.getFeatures()),
                    car.getRating().doubleValue(),
                    car.getTotalReviews());
        }
    }
}
