package com.touristchain.backend.roma.services;

import com.touristchain.backend.roma.models.BookingModel;
import com.touristchain.backend.roma.repositories.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BookingServices {

    @Autowired
    private BookingRepository bookingRepository;

    // Crear nueva reserva
    public BookingModel createBooking(BookingModel booking) {
        // Validar fechas
        if (booking.getCheckInDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de check-in no puede ser en el pasado");
        }
        
        if (booking.getCheckOutDate().isBefore(booking.getCheckInDate())) {
            throw new IllegalArgumentException("La fecha de check-out debe ser después del check-in");
        }

        // Validar numero de huespedes
        if (booking.getNumberOfGuests() == null || booking.getNumberOfGuests() <= 0) {
            throw new IllegalArgumentException("El numero de huespedes debe ser mayor a cero");
        }

        // Validar precio
        if (booking.getTotalPrice() == null || booking.getTotalPrice() <= 0) {
            throw new IllegalArgumentException("El precio total debe ser mayor a cero");
        }

        // Verificar disponibilidad
        if (!isDestinationAvailable(booking.getDestinationId(), booking.getCheckInDate(), booking.getCheckOutDate())) {
            throw new IllegalArgumentException("El destino no esta disponible para las fechas seleccionadas");
        }

        booking.setStatus("PENDING");
        booking.setCreatedAt(LocalDateTime.now());
        booking.setUpdatedAt(LocalDateTime.now());

        return bookingRepository.save(booking);
    }

    // Obtener reserva por ID
    public Optional<BookingModel> findById(Long id) {
        return bookingRepository.findById(id);
    }

    // Obtener reservas de un turista
    public List<BookingModel> findByTouristId(Long touristId) {
        return bookingRepository.findByTouristId(touristId);
    }

    // Obtener reservas de un destino
    public List<BookingModel> findByDestinationId(Long destinationId) {
        return bookingRepository.findByDestinationId(destinationId);
    }

    // Obtener reservas por estado
    public List<BookingModel> findByStatus(String status) {
        return bookingRepository.findByStatus(status);
    }

    // Confirmar reserva (cuando se ejecuta el smart contract)
    public Optional<BookingModel> confirmBooking(Long id, String contractAddress, String transactionHash) {
        Optional<BookingModel> bookingOpt = bookingRepository.findById(id);
        
        if (bookingOpt.isPresent()) {
            BookingModel booking = bookingOpt.get();
            
            if (!booking.isPending()) {
                throw new IllegalStateException("Solo se pueden confirmar reservas pendientes");
            }

            booking.setStatus("CONFIRMED");
            booking.setContractAddress(contractAddress);
            booking.setTransactionHash(transactionHash);
            booking.setConfirmedAt(LocalDateTime.now());
            booking.setUpdatedAt(LocalDateTime.now());

            return Optional.of(bookingRepository.save(booking));
        }
        
        return Optional.empty();
    }

    // Cancelar reserva
    public Optional<BookingModel> cancelBooking(Long id) {
        Optional<BookingModel> bookingOpt = bookingRepository.findById(id);
        
        if (bookingOpt.isPresent()) {
            BookingModel booking = bookingOpt.get();
            
            if (!booking.isPending()) {
                throw new IllegalStateException("Solo se pueden cancelar reservas pendientes");
            }

            booking.setStatus("CANCELLED");
            booking.setCancelledAt(LocalDateTime.now());
            booking.setUpdatedAt(LocalDateTime.now());

            return Optional.of(bookingRepository.save(booking));
        }
        
        return Optional.empty();
    }

    // Completar reserva (despues del viaje)
    public Optional<BookingModel> completeBooking(Long id) {
        Optional<BookingModel> bookingOpt = bookingRepository.findById(id);
        
        if (bookingOpt.isPresent()) {
            BookingModel booking = bookingOpt.get();
            
            if (!booking.isConfirmed()) {
                throw new IllegalStateException("Solo se pueden completar reservas confirmadas");
            }

            booking.setStatus("COMPLETED");
            booking.setUpdatedAt(LocalDateTime.now());

            return Optional.of(bookingRepository.save(booking));
        }
        
        return Optional.empty();
    }

    // Verificar disponibilidad de un destino
    public boolean isDestinationAvailable(Long destinationId, LocalDate checkIn, LocalDate checkOut) {
        List<BookingModel> conflictingBookings = bookingRepository.findConflictingBookings(
            destinationId, checkIn, checkOut
        );
        return conflictingBookings.isEmpty();
    }

    // Obtener reservas activas de un turista
    public List<BookingModel> findActiveBookingsByTourist(Long touristId) {
        return bookingRepository.findByTouristIdAndStatus(touristId, "CONFIRMED");
    }

    // Obtener historial de reservas de un turista
    public List<BookingModel> findBookingHistoryByTourist(Long touristId) {
        return bookingRepository.findByTouristIdOrderByCreatedAtDesc(touristId);
    }

    // Obtener estadisticas de reservas
    public Object getBookingStatistics() {
        long totalBookings = bookingRepository.count();
        long pendingBookings = bookingRepository.findByStatus("PENDING").size();
        long confirmedBookings = bookingRepository.findByStatus("CONFIRMED").size();
        long completedBookings = bookingRepository.findByStatus("COMPLETED").size();
        long cancelledBookings = bookingRepository.findByStatus("CANCELLED").size();
        
        return new Object() {
            public final long total = totalBookings;
            public final long pending = pendingBookings;
            public final long confirmed = confirmedBookings;
            public final long completed = completedBookings;
            public final long cancelled = cancelledBookings;
        };
    }

    // Calcular ingresos totales
    public Double calculateTotalRevenue() {
        List<BookingModel> completedBookings = bookingRepository.findByStatus("COMPLETED");
        return completedBookings.stream()
                .mapToDouble(BookingModel::getTotalPrice)
                .sum();
    }
}