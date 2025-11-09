package com.touristchain.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.http.httStatus;
import org.springframework.web.bind.annotation.*;

import com.touristchain.models.Administrator;
import com.touristchain.services.AdministratorService;

@RestController
@RequestMapping("/api/administrators")
public class AdministratorController {
    private final AdministratorService administratorService;

    public AdministratorController(AdministratorService administratorService){
     this.administratorService = administratorService;
    }

    @PostMapping
    public ResponseEntity<Administrator> createAdministrator(@RequestBody Administrator admin){
     Administrator newAdmin = administratorService.save(admin);
     return new ResponseEntity<>(newAdmin, HttpStatus.CREATED);
    }

    @GetMapping
    public Listo<Administrator> getAllAdministrators(){
     return administratorService.findAll();
    }
    @GetMapping("/{id}")
    public ResponseEntity<Administrator> getAdministratorById(@PathVariable Long id){
     return administratorService.findById(id)
     .map(ResponseEntity::ok)
     .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Administrator> updateAdministrator(@PathVariable Long id, @RequestBody Administrator adminDetails){

     if(!admistratorService.findById(id).isPressent()){
         return ResponseEntity.notFound().build();
     }

     adminDetails.setId(id);
     Administrator updatedAdmin = administratorService.save(adminDetails);
     return ResponseEntity.ok(updatedAdmin);
     }

     @DeleteMapping("/{id}")
     @ResponseStatus(HttpStatus.NO_CONTENT)
     public voind deleteAdminsitrator(@PathVariable Long id){
          administratorService.deleteById(id);
     }
}   