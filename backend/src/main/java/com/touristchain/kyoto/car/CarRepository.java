package com.touristchain.kyoto.car;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarRepository extends JpaRepository<CarEntity, Long> {

    List<CarEntity> findAllByOrderByIdAsc();

    List<CarEntity> findByCategoryIgnoreCaseOrderByIdAsc(String category);
}
