package com.touristchain.kyoto.hotel;

import com.touristchain.kyoto.hotel.HotelDtos.HotelRequest;
import com.touristchain.kyoto.hotel.HotelDtos.HotelView;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/** Catálogo de hoteles: lectura pública, escritura solo administradores (ver SecurityConfig). */
@RestController
@RequestMapping("/api/v1/kyoto/hotels")
@RequiredArgsConstructor
public class HotelController {

    private final HotelService hotelService;

    @GetMapping
    public List<HotelView> list(@RequestParam(required = false) String city,
                                @RequestParam(required = false) String search,
                                @RequestParam(required = false) Integer stars,
                                @RequestParam(required = false) BigDecimal minPrice,
                                @RequestParam(required = false) BigDecimal maxPrice) {
        return hotelService.search(city, search, stars, minPrice, maxPrice);
    }

    @GetMapping("/{id}")
    public HotelView get(@PathVariable Long id) {
        return hotelService.get(id);
    }

    @PostMapping
    public ResponseEntity<HotelView> create(@Valid @RequestBody HotelRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(hotelService.create(request));
    }

    @PutMapping("/{id}")
    public HotelView update(@PathVariable Long id, @Valid @RequestBody HotelRequest request) {
        return hotelService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public Map<String, String> delete(@PathVariable Long id) {
        hotelService.delete(id);
        return Map.of("message", "Hotel eliminado");
    }
}
