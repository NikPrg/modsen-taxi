package com.example.driverservice.service.impl;

import com.example.driverservice.dto.request.CarRequestDto;
import com.example.driverservice.dto.response.AllCarsResponseDto;
import com.example.driverservice.dto.response.CarResponseDto;
import com.example.driverservice.exception.CarNotBelongDriverException;
import com.example.driverservice.exception.DriverAlreadyHasCarException;
import com.example.driverservice.exception.DriverCarNotFoundException;
import com.example.driverservice.mapper.CarMapper;
import com.example.driverservice.model.projections.CarView;
import com.example.driverservice.repository.CarRepository;
import com.example.driverservice.repository.DriverRepository;
import com.example.driverservice.service.CarService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.example.driverservice.util.ExceptionMessagesConstants.*;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {
    private final CarRepository carRepo;
    private final DriverRepository driverRepo;
    private final CarMapper carMapper;

    @Transactional
    @Override
    public CarResponseDto createCar(UUID driverExternalId, CarRequestDto carRequest) {
        var driver = driverRepo.findByExternalId(driverExternalId)
                .orElseThrow(() -> new EntityNotFoundException(DRIVER_NOT_FOUND_EXCEPTION_MESSAGE.formatted(driverExternalId)));

        if (ObjectUtils.isNotEmpty(driver.getCar())) {
            throw new DriverAlreadyHasCarException(DRIVER_ALREADY_HAS_CAR_EXCEPTION_MESSAGE.formatted(driverExternalId));
        }

        var car = carMapper.toCar(carRequest);
        car.setDriver(driver);
        carRepo.save(car);

        return carMapper.toDto(car);
    }

    @Override
    public CarResponseDto findByExternalId(UUID externalId) {
        var car = carRepo.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException(CAR_NOT_FOUND_EXCEPTION_MESSAGE.formatted(externalId)));
        return carMapper.toDto(car);
    }

    @Transactional(readOnly = true)
    @Override
    public AllCarsResponseDto findAllCars(Pageable pageable) {
        Page<CarView> allCarsViews = carRepo.findAllCarsViews(pageable);
        return buildAllCarsDto(allCarsViews);
    }

    @Transactional
    @Override
    public CarResponseDto updateDriverCar(UUID driverExternalId, CarRequestDto carRequestDto) {
        var driver = driverRepo.findByExternalId(driverExternalId)
                .orElseThrow(() -> new EntityNotFoundException(DRIVER_NOT_FOUND_EXCEPTION_MESSAGE.formatted(driverExternalId)));

        var car = driver.getCar();

        if (ObjectUtils.isEmpty(car)) {
            throw new DriverCarNotFoundException(DRIVER_HAS_NO_CARS_EXCEPTION_MESSAGE.formatted(driverExternalId));
        }

        carMapper.updateCar(carRequestDto, car);
        driverRepo.save(driver);

        return carMapper.toDto(car);
    }

    @Transactional
    @Override
    public void deleteDriverCar(UUID driverExternalId, UUID carExternalId) {
        var driver = driverRepo.findByExternalId(driverExternalId)
                .orElseThrow(() -> new EntityNotFoundException(DRIVER_NOT_FOUND_EXCEPTION_MESSAGE.formatted(driverExternalId)));

        var car = carRepo.findByExternalId(carExternalId)
                .orElseThrow(() -> new EntityNotFoundException(CAR_NOT_FOUND_EXCEPTION_MESSAGE.formatted(carExternalId)));

        if (ObjectUtils.notEqual(car, driver.getCar())) {
            throw new CarNotBelongDriverException(CAR_NOT_BELONG_EXCEPTION_MESSAGE.formatted(carExternalId, driverExternalId));
        }

        driver.setCar(null);
        carRepo.delete(car);
    }

    private AllCarsResponseDto buildAllCarsDto(Page<CarView> allCarsViews) {
        return AllCarsResponseDto.builder()
                .carViewList(allCarsViews.getContent())
                .currentPageNumber(allCarsViews.getNumber())
                .totalPages(allCarsViews.getTotalPages())
                .totalElements(allCarsViews.getTotalElements())
                .build();
    }
}