package com.touristchain.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.touristchain.models.MainUser;

@Repository

public interface MainUserRepository extends JpaRepository<MainUser, Long> {
    Optional<MainUser> findByEmail(String email);
}
