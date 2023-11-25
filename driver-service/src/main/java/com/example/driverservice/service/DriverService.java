package com.example.driverservice.service;

import com.example.driverservice.dto.request.DriverRequestDto;
import com.example.driverservice.dto.response.AllDriversResponseDto;
import com.example.driverservice.dto.response.CreateDriverResponseDto;
import com.example.driverservice.dto.response.DriverResponseDto;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface DriverService {
    CreateDriverResponseDto createDriver(DriverRequestDto createDriverRequestDto);

    DriverResponseDto findDriverByExternalId(UUID externalId);

    AllDriversResponseDto findAllDrivers(Pageable pageable);

    DriverResponseDto updateDriver(UUID externalId, DriverRequestDto driverRequestDto);

    void deleteDriver(UUID externalId);
}
