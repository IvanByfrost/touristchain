package com.touristchain.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service  ;

import com.touristchain.models.Tourist;
import com.touristchain.repositories.TouristRepository;

@Service

public class TouristService {
    private final TouristRepository touristRepository;

    public TouristService(TouristRepository touristRepository){
        this.touristRepository = touristRepository;
    }

    public Tourist save(Tourist tourist){
        return touristRepository.save(tourist);
    }

    public List<Tourist> findAll(){
        return touristRepository.findAll();
    }

    public optional<Tourist> findById(Long id){
        return touristRpeository.findById(id);
    }
    

}
