package com.touristchain.kyoto.company;

import com.touristchain.common.NotFoundException;
import com.touristchain.kyoto.company.CompanyDtos.CompanyRequest;
import com.touristchain.kyoto.company.CompanyDtos.CompanyView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;

    @Transactional(readOnly = true)
    public List<CompanyView> list() {
        return companyRepository.findAllByOrderByIdAsc().stream().map(CompanyView::from).toList();
    }

    @Transactional(readOnly = true)
    public CompanyView get(Long id) {
        return CompanyView.from(find(id));
    }

    @Transactional
    public CompanyView create(CompanyRequest request) {
        String ruc = request.ruc().trim();
        if (companyRepository.existsByRuc(ruc)) {
            throw new IllegalStateException("Ya existe una empresa con ese RUC");
        }
        CompanyEntity company = new CompanyEntity();
        apply(company, request);
        return CompanyView.from(companyRepository.save(company));
    }

    @Transactional
    public CompanyView update(Long id, CompanyRequest request) {
        CompanyEntity company = find(id);
        if (companyRepository.existsByRucAndIdNot(request.ruc().trim(), id)) {
            throw new IllegalStateException("Ya existe una empresa con ese RUC");
        }
        apply(company, request);
        return CompanyView.from(companyRepository.saveAndFlush(company));
    }

    @Transactional
    public void delete(Long id) {
        companyRepository.delete(find(id));
    }

    private CompanyEntity find(Long id) {
        return companyRepository.findById(id).orElseThrow(() -> new NotFoundException("Empresa no encontrada"));
    }

    private static void apply(CompanyEntity company, CompanyRequest request) {
        company.setRuc(request.ruc().trim());
        company.setName(request.name().trim());
        company.setContact(request.contact().trim());
        company.setEmail(request.email().trim().toLowerCase());
        company.setPhone(request.phone().trim());
        company.setAddress(request.address());
        if (request.status() != null) {
            company.setStatus(request.status());
        }
        if (request.workers() != null) {
            company.setWorkers(request.workers());
        }
    }
}
