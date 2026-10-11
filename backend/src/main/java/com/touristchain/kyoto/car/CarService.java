package com.touristchain.kyoto.car;

import com.touristchain.common.NotFoundException;
import com.touristchain.kyoto.car.CarDtos.CarRequest;
import com.touristchain.kyoto.car.CarDtos.CarView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;

    @Transactional(readOnly = true)
    public List<CarView> list(String category) {
        List<CarEntity> cars = category == null || category.isBlank()
                ? carRepository.findAllByOrderByIdAsc()
                : carRepository.findByCategoryIgnoreCaseOrderByIdAsc(category.trim());
        return cars.stream().map(CarView::from).toList();
    }

    @Transactional(readOnly = true)
    public CarView get(Long id) {
        return CarView.from(find(id));
    }

    @Transactional
    public CarView create(CarRequest request) {
        CarEntity car = new CarEntity();
        apply(car, request);
        return CarView.from(carRepository.save(car));
    }

    @Transactional
    public CarView update(Long id, CarRequest request) {
        CarEntity car = find(id);
        apply(car, request);
        return CarView.from(carRepository.saveAndFlush(car));
    }

    @Transactional
    public void delete(Long id) {
        carRepository.delete(find(id));
    }

    private CarEntity find(Long id) {
        return carRepository.findById(id).orElseThrow(() -> new NotFoundException("Vehículo no encontrado"));
    }

    private static void apply(CarEntity car, CarRequest request) {
        car.setName(request.name().trim());
        car.setType(request.type().trim());
        car.setCategory(request.category());
        car.setPrice(request.price());
        car.setImage(request.image());
        car.setFeatures(request.features() == null ? new ArrayList<>() : new ArrayList<>(request.features()));
    }
}
