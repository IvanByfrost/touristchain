package com.touristchain.backend.roma.controllers;

import com.touristchain.backend.roma.models.BookingModel;
import com.touristchain.backend.roma.services.BookingServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/roma/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    @Autowired
    private BookingServices bookingServices;

    // Crear nueva reserva
    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody BookingModel booking) {
        try {
            BookingModel newBooking = bookingServices.createBooking(booking);
            return ResponseEntity.ok(newBooking);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Obtener todas las reservas
    @GetMapping
    public ResponseEntity<List<BookingModel>> getAllBookings() {
        List<BookingModel> bookings = bookingServices.findByStatus("PENDING");
        return ResponseEntity.ok(bookings);
    }

    // Obtener reserva por ID
    @GetMapping("/{id}")
    public ResponseEntity<BookingModel> getBookingById(@PathVariable Long id) {
        Optional<BookingModel> booking = bookingServices.findById(id);
        return booking.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    // Obtener reservas de un turista
    @GetMapping("/tourist/{touristId}")
    public ResponseEntity<List<BookingModel>> getBookingsByTourist(@PathVariable Long touristId) {
        List<BookingModel> bookings = bookingServices.findByTouristId(touristId);
        return ResponseEntity.ok(bookings);
    }

    // Obtener reservas de un destino
    @GetMapping("/destination/{destinationId}")
    public ResponseEntity<List<BookingModel>> getBookingsByDestination(@PathVariable Long destinationId) {
        List<BookingModel> bookings = bookingServices.findByDestinationId(destinationId);
        return ResponseEntity.ok(bookings);
    }

    // Obtener reservas por estado
    @GetMapping("/status/{status}")
    public ResponseEntity<List<BookingModel>> getBookingsByStatus(@PathVariable String status) {
        List<BookingModel> bookings = bookingServices.findByStatus(status);
        return ResponseEntity.ok(bookings);
    }

    // Obtener reservas activas de un turista
    @GetMapping("/tourist/{touristId}/active")
    public ResponseEntity<List<BookingModel>> getActiveBookingsByTourist(@PathVariable Long touristId) {
        List<BookingModel> bookings = bookingServices.findActiveBookingsByTourist(touristId);
        return ResponseEntity.ok(bookings);
    }

    // Obtener historial de reservas de un turista
    @GetMapping("/tourist/{touristId}/history")
    public ResponseEntity<List<BookingModel>> getBookingHistoryByTourist(@PathVariable Long touristId) {
        List<BookingModel> bookings = bookingServices.findBookingHistoryByTourist(touristId);
        return ResponseEntity.ok(bookings);
    }

    // Confirmar reserva
    @PatchMapping("/{id}/confirm")
    public ResponseEntity<?> confirmBooking(@PathVariable Long id, 
                                           @RequestBody ConfirmBookingRequest request) {
        try {
            Optional<BookingModel> booking = bookingServices.confirmBooking(id, 
                request.getContractAddress(), request.getTransactionHash());
            return booking.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Cancelar reserva
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<?> cancelBooking(@PathVariable Long id) {
        try {
            Optional<BookingModel> booking = bookingServices.cancelBooking(id);
            return booking.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Completar reserva
    @PatchMapping("/{id}/complete")
    public ResponseEntity<?> completeBooking(@PathVariable Long id) {
        try {
            Optional<BookingModel> booking = bookingServices.completeBooking(id);
            return booking.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Verificar disponibilidad
    @GetMapping("/availability")
    public ResponseEntity<Boolean> checkAvailability(
            @RequestParam Long destinationId,
            @RequestParam String checkIn,
            @RequestParam String checkOut) {
        
        boolean available = bookingServices.isDestinationAvailable(
            destinationId, 
            java.time.LocalDate.parse(checkIn), 
            java.time.LocalDate.parse(checkOut)
        );
        return ResponseEntity.ok(available);
    }

    // Obtener estadisticas
    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        Object stats = bookingServices.getBookingStatistics();
        return ResponseEntity.ok(stats);
    }

    // Obtener ingresos totales
    @GetMapping("/revenue")
    public ResponseEntity<Double> getTotalRevenue() {
        Double revenue = bookingServices.calculateTotalRevenue();
        return ResponseEntity.ok(revenue);
    }

    // Clase interna para el request de confirmacion
    public static class ConfirmBookingRequest {
        private String contractAddress;
        private String transactionHash;

        public String getContractAddress() {
            return contractAddress;
        }

        public void setContractAddress(String contractAddress) {
            this.contractAddress = contractAddress;
        }

        public String getTransactionHash() {
            return transactionHash;
        }

        public void setTransactionHash(String transactionHash) {
            this.transactionHash = transactionHash;
        }
    }
}