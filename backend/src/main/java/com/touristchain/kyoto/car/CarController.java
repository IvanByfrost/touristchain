package com.touristchain.kyoto.car;

import com.touristchain.kyoto.car.CarDtos.CarRequest;
import com.touristchain.kyoto.car.CarDtos.CarView;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/** Vehículos de alquiler: lectura pública, escritura solo administradores (ver SecurityConfig). */
@RestController
@RequestMapping("/api/v1/kyoto/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;

    @GetMapping
    public List<CarView> list(@RequestParam(required = false) String category) {
        return carService.list(category);
    }

    @GetMapping("/{id}")
    public CarView get(@PathVariable Long id) {
        return carService.get(id);
    }

    @PostMapping
    public ResponseEntity<CarView> create(@Valid @RequestBody CarRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(carService.create(request));
    }

    @PutMapping("/{id}")
    public CarView update(@PathVariable Long id, @Valid @RequestBody CarRequest request) {
        return carService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public Map<String, String> delete(@PathVariable Long id) {
        carService.delete(id);
        return Map.of("message", "Vehículo eliminado");
    }
}
