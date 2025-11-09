package com.touristchain.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.touristchain.models.Administrator;
import com.touristchain.repositories.AdministratorRepository;

@Service
public class AdministratorService {
    private final AdministratorRepository administratorRepository;

    public AdministratorService(AdministratorRepository adminRepository){
        this.administratorRepository = adminRepository;
    }

    public Administrator save(Administrator admin){
        return administratorRepository.save(admin);
    }
    public list<Administrator> findAll(){
        return administratorRepository.findAll();
    }
    public Optional<Administrator> findById(Long id){
        return administratorRepository.findById(id);
    }
    public void deleteById(Long id){
        administratorRepository.deleteById(id);
    }
}