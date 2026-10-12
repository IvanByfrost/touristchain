package com.touristchain.rome.booking;

import com.touristchain.common.NotFoundException;
import com.touristchain.geneva.partners.PartnerRepository;
import com.touristchain.geneva.tourists.TouristEntity;
import com.touristchain.geneva.tourists.TouristRepository;
import com.touristchain.geneva.users.UserEntity;
import com.touristchain.geneva.users.UserRepository;
import com.touristchain.kyoto.car.CarDtos.CarView;
import com.touristchain.kyoto.car.CarService;
import com.touristchain.kyoto.hotel.HotelDtos.HotelView;
import com.touristchain.kyoto.hotel.HotelService;
import com.touristchain.kyoto.trip.TripDtos.TripView;
import com.touristchain.kyoto.trip.TripService;
import com.touristchain.rome.booking.BookingDtos.BookingRequest;
import com.touristchain.rome.booking.BookingDtos.BookingView;
import com.touristchain.rome.booking.BookingEntity.BookingStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final TouristRepository touristRepository;
    private final PartnerRepository partnerRepository;
    private final UserRepository userRepository;
    private final TripService tripService;
    private final HotelService hotelService;
    private final CarService carService;

    /** Admin: todas. Turista: las suyas. Socio: las de su empresa. */
    @Transactional(readOnly = true)
    public List<BookingView> list(UUID userId, boolean isAdmin) {
        if (isAdmin) {
            return toViews(bookingRepository.findAllByOrderByCreatedAtAsc());
        }
        var tourist = touristRepository.findByUserId(userId);
        if (tourist.isPresent()) {
            return toViews(bookingRepository.findByTouristIdOrderByCreatedAtAsc(tourist.get().getTouristId()));
        }
        return partnerRepository.findByUserId(userId)
                .map(partner -> toViews(bookingRepository.findByPartnerIdOrderByCreatedAtAsc(partner.getPartnerId())))
                .orElse(List.of());
    }

    /** Fija nombre y precio desde el catálogo; en viajes además ocupa los cupos (fila bloqueada). */
    @Transactional
    public BookingView create(UUID userId, BookingRequest request) {
        TouristEntity tourist = requireTourist(userId);
        int quantity = request.quantity();
        String itemId = request.itemId().trim();

        BookingEntity booking = new BookingEntity();
        booking.setTouristId(tourist.getTouristId());
        booking.setItemType(request.itemType());
        booking.setQuantity(quantity);

        switch (request.itemType()) {
            case "viaje" -> {
                long tripId = parseId(itemId);
                // Primero bloquear y ocupar cupos; leer antes dejaría un estado viejo en memoria
                tripService.reserveSeats(tripId, quantity);
                TripView trip = tripService.get(tripId);
                booking.setTripId(tripId);
                booking.setItemRef(String.valueOf(tripId));
                booking.setItemName(trip.destination());
                booking.setUnitPrice(BigDecimal.valueOf(trip.price()));
                booking.setStartDate(trip.startDate());
                booking.setEndDate(trip.endDate());
            }
            case "hotel" -> {
                HotelView hotel = hotelService.get(parseId(itemId));
                booking.setItemRef(String.valueOf(hotel.id()));
                booking.setItemName("Hotel: " + hotel.name());
                booking.setUnitPrice(BigDecimal.valueOf(hotel.price()));
            }
            case "renta" -> {
                CarView car = carService.get(parseId(itemId));
                booking.setItemRef(String.valueOf(car.id()));
                booking.setItemName("Renta: " + car.name());
                booking.setUnitPrice(BigDecimal.valueOf(car.price()));
            }
            case "paquete" -> {
                PackagePlan plan = PackagePlan.fromCode(itemId)
                        .orElseThrow(() -> new IllegalArgumentException("Paquete no válido"));
                if (quantity != 1) {
                    throw new IllegalArgumentException("Los paquetes se reservan de uno en uno");
                }
                booking.setItemRef(plan.code());
                booking.setItemName(plan.displayName());
                booking.setUnitPrice(plan.price());
            }
            default -> throw new IllegalArgumentException("Tipo de ítem inválido");
        }
        booking.setTotalPrice(booking.getUnitPrice().multiply(BigDecimal.valueOf(quantity)));

        BookingEntity saved = bookingRepository.save(booking);
        return toViews(List.of(saved)).get(0);
    }

    /**
     * El turista puede cancelar sus reservas pendientes; el administrador, cualquiera.
     * Si es un viaje, devuelve los cupos.
     */
    @Transactional
    public void cancel(UUID userId, boolean isAdmin, UUID bookingId) {
        BookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Reserva no encontrada"));
        if (!isAdmin) {
            boolean isOwner = touristRepository.findByUserId(userId)
                    .map(tourist -> tourist.getTouristId().equals(booking.getTouristId()))
                    .orElse(false);
            if (!isOwner) {
                throw new SecurityException("No tienes permiso para cancelar esta reserva");
            }
            if (booking.getStatus() != BookingStatus.PENDING) {
                throw new IllegalStateException("Solo se pueden cancelar reservas pendientes de pago");
            }
        }
        if (booking.getTripId() != null && booking.getStatus() != BookingStatus.CANCELLED) {
            tripService.releaseSeats(booking.getTripId(), booking.getQuantity());
        }
        bookingRepository.delete(booking);
    }

    // ========== Para el módulo de pagos ==========

    /** Reservas pendientes de pago del usuario (vacío si no es turista). */
    @Transactional(readOnly = true)
    public List<BookingEntity> pendingOf(UUID userId) {
        return touristRepository.findByUserId(userId)
                .map(tourist -> bookingRepository.findByTouristIdAndStatusOrderByCreatedAtAsc(
                        tourist.getTouristId(), BookingStatus.PENDING))
                .orElse(List.of());
    }

    /** Asocia las reservas a un pago todavía sin confirmar (QR). */
    @Transactional
    public void linkToPayment(List<BookingEntity> bookings, UUID paymentId) {
        bookings.forEach(booking -> booking.setPaymentId(paymentId));
        bookingRepository.saveAll(bookings);
    }

    /** Marca como pagadas las reservas pendientes asociadas al pago. */
    @Transactional
    public void confirmPayment(List<BookingEntity> bookings, UUID paymentId, LocalDateTime paidAt) {
        bookings.forEach(booking -> booking.markPaid(paymentId, paidAt));
        bookingRepository.saveAll(bookings);
    }

    @Transactional
    public void confirmPayment(UUID paymentId, LocalDateTime paidAt) {
        confirmPayment(bookingRepository.findByPaymentIdAndStatus(paymentId, BookingStatus.PENDING), paymentId, paidAt);
    }

    private TouristEntity requireTourist(UUID userId) {
        return touristRepository.findByUserId(userId)
                .orElseThrow(() -> new SecurityException("Solo los turistas pueden reservar"));
    }

    private static long parseId(String itemId) {
        try {
            return Long.parseLong(itemId);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Identificador de ítem inválido");
        }
    }

    private List<BookingView> toViews(List<BookingEntity> bookings) {
        Set<UUID> touristIds = bookings.stream().map(BookingEntity::getTouristId).collect(Collectors.toSet());
        Map<UUID, UUID> userIdByTourist = touristRepository.findAllById(touristIds).stream()
                .collect(Collectors.toMap(TouristEntity::getTouristId, TouristEntity::getUserId));
        Map<UUID, UserEntity> users = userRepository.findAllById(userIdByTourist.values()).stream()
                .collect(Collectors.toMap(UserEntity::getUserId, Function.identity()));
        return bookings.stream().map(booking -> {
            UUID ownerId = userIdByTourist.get(booking.getTouristId());
            UserEntity owner = ownerId == null ? null : users.get(ownerId);
            return BookingView.from(booking, ownerId, displayName(owner));
        }).toList();
    }

    static String displayName(UserEntity user) {
        if (user == null) {
            return "Turista";
        }
        return user.getLastName() == null ? user.getFirstName() : user.getFirstName() + " " + user.getLastName();
    }
}
