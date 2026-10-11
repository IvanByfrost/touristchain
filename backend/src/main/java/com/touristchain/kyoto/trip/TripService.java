package com.touristchain.kyoto.trip;

import com.touristchain.common.NotFoundException;
import com.touristchain.kyoto.destination.DestinationService;
import com.touristchain.kyoto.trip.TripDtos.TripRequest;
import com.touristchain.kyoto.trip.TripDtos.TripView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;
    private final DestinationService destinationService;

    @Transactional(readOnly = true)
    public List<TripView> search(String destination, String status, String tag, BigDecimal minPrice, BigDecimal maxPrice) {
        String wantedTag = tag == null || tag.isBlank() ? null : tag.trim().toLowerCase(Locale.ROOT);
        return tripRepository.search(destination == null ? "" : destination.trim(),
                        status == null || status.isBlank() ? null : status.trim(), minPrice, maxPrice)
                .stream()
                .filter(trip -> wantedTag == null || trip.getTags().contains(wantedTag))
                .map(TripView::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public TripView get(Long id) {
        return TripView.from(find(id));
    }

    @Transactional
    public TripView create(TripRequest request) {
        TripEntity trip = new TripEntity();
        trip.setAvailable(request.capacity());
        apply(trip, request);
        return TripView.from(tripRepository.save(trip));
    }

    @Transactional
    public TripView update(Long id, TripRequest request) {
        TripEntity trip = find(id);
        apply(trip, request);
        return TripView.from(tripRepository.saveAndFlush(trip));
    }

    @Transactional
    public void delete(Long id) {
        tripRepository.delete(find(id));
    }

    /** Para el módulo de reservas: ocupa cupos con la fila bloqueada. */
    @Transactional
    public void reserveSeats(Long id, int quantity) {
        lockedTrip(id).reserveSeats(quantity);
    }

    /** Para el módulo de reservas: devuelve cupos al cancelar. */
    @Transactional
    public void releaseSeats(Long id, int quantity) {
        lockedTrip(id).releaseSeats(quantity);
    }

    private TripEntity find(Long id) {
        return tripRepository.findById(id).orElseThrow(() -> new NotFoundException("Viaje no encontrado"));
    }

    private TripEntity lockedTrip(Long id) {
        return tripRepository.findForUpdate(id).orElseThrow(() -> new NotFoundException("Viaje no encontrado"));
    }

    private void apply(TripEntity trip, TripRequest request) {
        if (request.endDate().isBefore(request.startDate())) {
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la de inicio");
        }
        if (request.destinationId() != null && !request.destinationId().equals(trip.getDestinationId())) {
            destinationService.requireActive(request.destinationId());
        }
        trip.setDestinationId(request.destinationId());
        int available = request.available() != null ? request.available()
                : Math.min(trip.getAvailable(), request.capacity());
        if (available > request.capacity()) {
            throw new IllegalArgumentException("Los cupos disponibles no pueden superar la capacidad");
        }
        trip.setDestination(request.destination().trim());
        trip.setDescription(request.description().trim());
        trip.setStartDate(request.startDate());
        trip.setEndDate(request.endDate());
        trip.setPrice(request.price());
        trip.setCapacity(request.capacity());
        trip.setAvailable(available);
        if (request.status() != null) {
            trip.setStatus(request.status());
        }
        trip.setImage(request.image());
        if (request.tags() != null) {
            trip.setTags(new ArrayList<>(request.tags().stream()
                    .map(tag -> tag.trim().toLowerCase(Locale.ROOT)).distinct().toList()));
        }
        if (request.coordinates() != null) {
            trip.setLatitude(request.coordinates().lat());
            trip.setLongitude(request.coordinates().lng());
        }
    }
}
