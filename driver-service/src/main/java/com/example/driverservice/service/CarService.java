package com.example.driverservice.service;

import com.example.driverservice.dto.request.CarRequestDto;
import com.example.driverservice.dto.response.AllCarsResponseDto;
import com.example.driverservice.dto.response.CarResponseDto;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CarService {
    CarResponseDto createCar(UUID driverExternalId, CarRequestDto carRequest);

    CarResponseDto findByExternalId(UUID externalId);

    AllCarsResponseDto findAllCars(Pageable pageable);

    CarResponseDto updateDriverCar(UUID driverExternalId, CarRequestDto carRequestDto);

    void deleteDriverCar(UUID driverExternalId, UUID carExternalId);
}