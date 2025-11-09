package com.touristchain.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.touristchain.models.Administrator;

@Repository

public interface AdministratorRepository extends JpaRepository<Administrator, Long> {

    
}
