package com.touristchain.rome.booking;

import com.touristchain.geneva.security.UserPrincipal;
import com.touristchain.rome.booking.BookingDtos.BookingRequest;
import com.touristchain.rome.booking.BookingDtos.BookingView;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Reservas del usuario autenticado (el administrador ve y cancela todas). */
@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @GetMapping
    public List<BookingView> list(@AuthenticationPrincipal UserPrincipal principal) {
        return bookingService.list(principal.getUserId(), isAdmin(principal));
    }

    @PostMapping
    public ResponseEntity<BookingView> create(@AuthenticationPrincipal UserPrincipal principal,
                                              @Valid @RequestBody BookingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.create(principal.getUserId(), request));
    }

    @DeleteMapping("/{bookingId}")
    public Map<String, String> cancel(@AuthenticationPrincipal UserPrincipal principal,
                                      @PathVariable UUID bookingId) {
        bookingService.cancel(principal.getUserId(), isAdmin(principal), bookingId);
        return Map.of("message", "Reserva cancelada");
    }

    static boolean isAdmin(UserPrincipal principal) {
        return principal.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
    }
}
