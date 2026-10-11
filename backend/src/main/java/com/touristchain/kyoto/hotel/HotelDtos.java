package com.touristchain.kyoto.hotel;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

/** Formatos de entrada y salida de hoteles, con los nombres de campo que usa el frontend. */
public final class HotelDtos {

    private HotelDtos() {
    }

    public record Coordinates(@NotNull Double lat, @NotNull Double lng) {
    }

    public record HotelRequest(
            @NotBlank(message = "El nombre es obligatorio") @Size(max = 150) String name,
            @NotBlank(message = "La ciudad es obligatoria") @Size(max = 150) String city,
            @NotNull(message = "Las estrellas son obligatorias")
            @Min(value = 1, message = "Las estrellas van de 1 a 5")
            @Max(value = 5, message = "Las estrellas van de 1 a 5") Integer stars,
            @NotNull(message = "El precio es obligatorio")
            @PositiveOrZero(message = "El precio no puede ser negativo") BigDecimal price,
            @Size(max = 500) String image,
            String description,
            List<@NotBlank String> amenities,
            @Valid Coordinates coordinates) {
    }

    public record HotelView(
            Long id,
            String name,
            String city,
            Integer stars,
            double price,
            String image,
            String description,
            List<String> amenities,
            Coordinates coordinates) {

        static HotelView from(HotelEntity hotel) {
            Coordinates coordinates = hotel.getLatitude() == null || hotel.getLongitude() == null ? null
                    : new Coordinates(hotel.getLatitude(), hotel.getLongitude());
            return new HotelView(
                    hotel.getId(),
                    hotel.getName(),
                    hotel.getCity(),
                    hotel.getStars(),
                    hotel.getPrice().doubleValue(),
                    hotel.getImage(),
                    hotel.getDescription(),
                    List.copyOf(hotel.getAmenities()),
                    coordinates);
        }
    }
}
