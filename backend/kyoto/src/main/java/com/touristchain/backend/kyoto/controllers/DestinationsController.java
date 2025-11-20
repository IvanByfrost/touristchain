package com.touristchain.backend.kyoto.controllers;

import com.touristchain.backend.kyoto.services.DestinationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/kyoto/destinations")
@CrossOrigin(origins = "*")
public class DestinationsController {

    @Autowired
    private DestinationService destinationService;

    @GetMapping
    public Page<?> getDestinations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String category) {
        return destinationService.findWithFilters(country, category, page, size);
    }

    @GetMapping("/{id}")
    public Object getDestination(@PathVariable Long id) {
        return destinationService.findById(id);
    }

    @GetMapping("/search")
    public List<?> search(@RequestParam String query) {
        return destinationService.search(query);
    }

    @GetMapping("/popular")
    public List<?> getPopular() {
        return destinationService.findPopular();
    }

    @PostMapping
    public Object createDestination(@RequestBody Map<String, Object> destination) {
        return destinationService.create(destination);
    }

    @PutMapping("/{id}")
    public Object updateDestination(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        return destinationService.update(id, updates);
    }

    @DeleteMapping("/{id}")
    public void deleteDestination(@PathVariable Long id) {
        destinationService.delete(id);
    }
}