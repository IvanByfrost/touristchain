package com.touristchain.kyoto.hotel;

import com.touristchain.common.NotFoundException;
import com.touristchain.kyoto.hotel.HotelDtos.HotelRequest;
import com.touristchain.kyoto.hotel.HotelDtos.HotelView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HotelService {

    private final HotelRepository hotelRepository;

    @Transactional(readOnly = true)
    public List<HotelView> search(String city, String search, Integer stars, BigDecimal minPrice, BigDecimal maxPrice) {
        return hotelRepository.search(nullToEmpty(city), nullToEmpty(search), stars, minPrice, maxPrice)
                .stream().map(HotelView::from).toList();
    }

    @Transactional(readOnly = true)
    public HotelView get(Long id) {
        return HotelView.from(find(id));
    }

    @Transactional
    public HotelView create(HotelRequest request) {
        HotelEntity hotel = new HotelEntity();
        apply(hotel, request);
        return HotelView.from(hotelRepository.save(hotel));
    }

    @Transactional
    public HotelView update(Long id, HotelRequest request) {
        HotelEntity hotel = find(id);
        apply(hotel, request);
        return HotelView.from(hotelRepository.saveAndFlush(hotel));
    }

    @Transactional
    public void delete(Long id) {
        hotelRepository.delete(find(id));
    }

    private HotelEntity find(Long id) {
        return hotelRepository.findById(id).orElseThrow(() -> new NotFoundException("Hotel no encontrado"));
    }

    private static void apply(HotelEntity hotel, HotelRequest request) {
        hotel.setName(request.name().trim());
        hotel.setCity(request.city().trim());
        hotel.setStars(request.stars());
        hotel.setPrice(request.price());
        hotel.setImage(request.image());
        hotel.setDescription(request.description());
        hotel.setAmenities(request.amenities() == null ? new ArrayList<>() : new ArrayList<>(request.amenities()));
        hotel.setLatitude(request.coordinates() == null ? null : request.coordinates().lat());
        hotel.setLongitude(request.coordinates() == null ? null : request.coordinates().lng());
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value.trim();
    }
}
