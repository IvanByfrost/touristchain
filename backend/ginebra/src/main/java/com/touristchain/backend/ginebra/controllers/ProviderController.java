package com.touristchain.backend.ginebra.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.touristchain.backend.ginebra.dto.ProviderResponse;
import com.touristchain.backend.ginebra.models.entities.Provider;
import com.touristchain.backend.ginebra.services.ProviderService;

@RestController
@RequestMapping("/api/v1/ginebra/providers")
public class ProviderController {

    @Autowired
    private ProviderService providerService;

    // ========== 📋 CONSULTAS ==========

    @GetMapping
    public ResponseEntity<List<ProviderResponse>> getAllProviders() {
        List<ProviderResponse> providers = providerService.getAllProviders();
        return ResponseEntity.ok(providers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProviderResponse> getProviderById(@PathVariable Long id) {
        return providerService.getProviderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ProviderResponse> getProviderByUserId(@PathVariable Long userId) {
        return providerService.getProviderByUserId(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/verified")
    public ResponseEntity<List<ProviderResponse>> getVerifiedProviders() {
        List<ProviderResponse> providers = providerService.getVerifiedProviders();
        return ResponseEntity.ok(providers);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProviderResponse>> searchProviders(
            @RequestParam String companyName) {
        List<ProviderResponse> providers = providerService.searchProvidersByCompanyName(companyName);
        return ResponseEntity.ok(providers);
    }

    // ========== 📝 MUTACIONES ==========

    @PostMapping
    public ResponseEntity<ProviderResponse> registerProvider(@RequestBody Provider provider) {
        try {
            ProviderResponse newProvider = providerService.registerProvider(provider);
            return ResponseEntity.ok(newProvider);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProviderResponse> updateProvider(
            @PathVariable Long id,
            @RequestBody Provider providerUpdates) {
        try {
            ProviderResponse updatedProvider = providerService.updateProvider(id, providerUpdates);
            return ResponseEntity.ok(updatedProvider);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{id}/verify")
    public ResponseEntity<Void> verifyProvider(@PathVariable Long id) {
        try {
            providerService.verifyProvider(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/reviews")
    public ResponseEntity<Void> addReview(
            @PathVariable Long id,
            @RequestParam Double rating) {
        try {
            providerService.addReview(id, rating);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProvider(@PathVariable Long id) {
        try {
            providerService.deleteProvider(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ========== 📊 ESTADÍSTICAS ==========

    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        long totalProviders = providerService.countProviders();
        long verifiedProviders = providerService.countVerifiedProviders();

        return ResponseEntity.ok(new Object() {
            public final long total = totalProviders;
            public final long verified = verifiedProviders;
            public final long pending = totalProviders - verifiedProviders;
        });
    }
}