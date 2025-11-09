package com.touristchain.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.touristchain.models.Country;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {
}
