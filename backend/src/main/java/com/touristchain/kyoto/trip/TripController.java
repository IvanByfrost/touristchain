package com.touristchain.kyoto.trip;

import com.touristchain.kyoto.trip.TripDtos.TripRequest;
import com.touristchain.kyoto.trip.TripDtos.TripView;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/** Viajes (paquetes): lectura pública, escritura solo administradores (ver SecurityConfig). */
@RestController
@RequestMapping("/api/v1/kyoto/trips")
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;

    @GetMapping
    public List<TripView> list(@RequestParam(required = false) String destination,
                               @RequestParam(required = false) String status,
                               @RequestParam(required = false) String tag,
                               @RequestParam(required = false) BigDecimal minPrice,
                               @RequestParam(required = false) BigDecimal maxPrice) {
        return tripService.search(destination, status, tag, minPrice, maxPrice);
    }

    @GetMapping("/{id}")
    public TripView get(@PathVariable Long id) {
        return tripService.get(id);
    }

    @PostMapping
    public ResponseEntity<TripView> create(@Valid @RequestBody TripRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tripService.create(request));
    }

    @PutMapping("/{id}")
    public TripView update(@PathVariable Long id, @Valid @RequestBody TripRequest request) {
        return tripService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public Map<String, String> delete(@PathVariable Long id) {
        tripService.delete(id);
        return Map.of("message", "Viaje eliminado");
    }
}
