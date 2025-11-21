package com.touristchain.backend.kyoto.services;

import com.touristchain.backend.kyoto.models.Destination;
import com.touristchain.backend.kyoto.repository.DestinationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DestinationServices {

    @Autowired
    private DestinationRepository destinationRepository;

    // Obtener todos los destinos
    public List<Destination> findAll() {
        return destinationRepository.findAll();
    }

    // Obtener destinos con paginación
    public Page<Destination> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return destinationRepository.findAll(pageable);
    }

    // Buscar por ID
    public Optional<Destination> findById(Long id) {
        return destinationRepository.findById(id);
    }

    // Guardar destino (crear o actualizar)
    public Destination save(Destination destination) {
        return destinationRepository.save(destination);
    }

    // Eliminar destino
    public void delete(Long id) {
        destinationRepository.deleteById(id);
    }

    // Buscar por país
    public List<Destination> findByCountry(String country) {
        return destinationRepository.findByCountry(country);
    }

    // Buscar por categoría
    public List<Destination> findByCategory(String category) {
        return destinationRepository.findByCategory(category);
    }

    // Buscar por nombre
    public List<Destination> searchByName(String name) {
        return destinationRepository.findByNameContainingIgnoreCase(name);
    }

    // Buscar por rango de precio
    public List<Destination> findByPriceRange(Double minPrice, Double maxPrice) {
        return destinationRepository.findByPricePerDayBetween(minPrice, maxPrice);
    }

    // Obtener destinos populares
    public List<Destination> findPopular() {
        return destinationRepository.findByOrderByRatingDesc();
    }

    // Buscar con filtros combinados
    public List<Destination> findWithFilters(String country, String category, Double minPrice, Double maxPrice) {
        return destinationRepository.findWithFilters(country, category, minPrice, maxPrice);
    }
}