package com.touristchain.repositories;

import org.springframework.stereotype.Repository;
import jakarta.springframework.data.jpa.repository.JpaRepository;

import com.touristchain.models.Tourist;

@Reporsitory

public interface TouristRepository extends JpaRepository<Tourist, Long> {
    
}
