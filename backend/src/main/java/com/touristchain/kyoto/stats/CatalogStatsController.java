package com.touristchain.kyoto.stats;

import com.touristchain.kyoto.car.CarRepository;
import com.touristchain.kyoto.company.CompanyRepository;
import com.touristchain.kyoto.destination.DestinationService;
import com.touristchain.kyoto.hotel.HotelRepository;
import com.touristchain.kyoto.trip.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.TreeMap;

/** Resumen del catálogo para el panel de administración (solo administradores, ver SecurityConfig). */
@RestController
@RequiredArgsConstructor
public class CatalogStatsController {

    private final DestinationService destinationService;
    private final HotelRepository hotelRepository;
    private final CarRepository carRepository;
    private final TripRepository tripRepository;
    private final CompanyRepository companyRepository;

    @GetMapping("/api/v1/kyoto/stats")
    @Transactional(readOnly = true)
    public CatalogStats stats() {
        Map<String, Long> tripsByStatus = new TreeMap<>();
        long trips = 0;
        long seatsAvailable = 0;
        long seatsTotal = 0;
        for (Object[] row : tripRepository.summarizeByStatus()) {
            tripsByStatus.put((String) row[0], (Long) row[1]);
            trips += (Long) row[1];
            seatsAvailable += (Long) row[2];
            seatsTotal += (Long) row[3];
        }
        return new CatalogStats(
                destinationService.countAll(),
                destinationService.countActive(),
                hotelRepository.count(),
                carRepository.count(),
                companyRepository.count(),
                trips,
                tripsByStatus,
                seatsAvailable,
                seatsTotal);
    }

    public record CatalogStats(
            long destinations,
            long activeDestinations,
            long hotels,
            long cars,
            long companies,
            long trips,
            Map<String, Long> tripsByStatus,
            long seatsAvailable,
            long seatsTotal) {
    }
}
