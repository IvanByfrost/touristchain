package com.touristchain.kyoto.company;

import com.touristchain.kyoto.company.CompanyDtos.CompanyRequest;
import com.touristchain.kyoto.company.CompanyDtos.CompanyView;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/** Empresas asociadas: todo el recurso es solo para administradores (ver SecurityConfig). */
@RestController
@RequestMapping("/api/v1/kyoto/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping
    public List<CompanyView> list() {
        return companyService.list();
    }

    @GetMapping("/{id}")
    public CompanyView get(@PathVariable Long id) {
        return companyService.get(id);
    }

    @PostMapping
    public ResponseEntity<CompanyView> create(@Valid @RequestBody CompanyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(companyService.create(request));
    }

    @PutMapping("/{id}")
    public CompanyView update(@PathVariable Long id, @Valid @RequestBody CompanyRequest request) {
        return companyService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public Map<String, String> delete(@PathVariable Long id) {
        companyService.delete(id);
        return Map.of("message", "Empresa eliminada");
    }
}
