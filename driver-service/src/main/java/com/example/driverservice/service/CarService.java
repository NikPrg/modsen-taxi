package com.example.driverservice.service;

import com.example.driverservice.dto.request.CarRequest;
import com.example.driverservice.dto.request.UpdateCarRequest;
import com.example.driverservice.dto.response.AllCarsResponse;
import com.example.driverservice.dto.response.CarResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CarService {
    CarResponse createCar(UUID driverExternalId, CarRequest carRequest);

    CarResponse findByExternalId(UUID externalId);

    AllCarsResponse findAllCars(Pageable pageable);

    CarResponse updateDriverCar(UUID driverExternalId, UpdateCarRequest updateCarRequest);

    void deleteDriverCar(UUID driverExternalId, UUID carExternalId);
}