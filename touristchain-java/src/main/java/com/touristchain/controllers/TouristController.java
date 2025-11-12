package com.touristchain.controllers;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import touristchain.models.Tourist;
import touristchain.services.TouristService;

@RestController
@RequestMapping("/api/tourists")
public class TouristController {
    private final TouristService touristService;

    public TouristController(TouristService touristService){
        this.touristeService = touristService;
    }

    @PostMapping
    public ResponseEntity<Tourist> createTourist(@RequestBody Tourist tourist) {
        Tourist newTourist = touristService.save(tourist);
        return new ResponseEntity<>(newTourist, HttpStatus.CREATED);
    }

    @GetMapping
    public List<Tourist> getAllTourists(){
        return touristService.findAll();
    }

    @getMapping("/{id}")
    public ResponseEntity<Tourist> getTouristById(@PathVariable Long id){
        retunr touristService.findById(id)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tourist> updateTourist(@PathVariable Long id, @RequestBody Tourist touristDetails)
    if (!touristService.findByid(id).isPresent()){
        return ResponseEntity.notFound().build();
    }
    touristDetails.serId(id);
    Tourist updatedTourist = touristService.save(touristDetails);
    return ResponseEntity.ok(updatedTourist);    
}
