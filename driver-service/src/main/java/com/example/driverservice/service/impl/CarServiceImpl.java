package com.example.driverservice.service.impl;

import com.example.driverservice.amqp.handler.SendRequestHandler;
import com.example.driverservice.dto.request.CarRequest;
import com.example.driverservice.dto.request.UpdateCarRequest;
import com.example.driverservice.dto.response.AllCarsResponse;
import com.example.driverservice.dto.response.CarResponse;
import com.example.driverservice.exception.CarNotBelongDriverException;
import com.example.driverservice.exception.DriverAlreadyHasCarException;
import com.example.driverservice.exception.DriverCarNotFoundException;
import com.example.driverservice.mapper.CarMapper;
import com.example.driverservice.model.enums.DriverStatus;
import com.example.driverservice.model.projections.CarView;
import com.example.driverservice.repository.CarRepository;
import com.example.driverservice.repository.DriverRepository;
import com.example.driverservice.service.CarService;
import com.example.driverservice.util.DataComposerUtils;
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
    private final SendRequestHandler sendRequestHandler;
    private final DataComposerUtils dataComposerUtils;

    @Transactional
    @Override
    public CarResponse createCar(UUID driverExternalId, CarRequest carRequest) {
        var driver = driverRepo.findByExternalId(driverExternalId)
                .orElseThrow(() -> new EntityNotFoundException(DRIVER_NOT_FOUND_EXCEPTION_MESSAGE.formatted(driverExternalId)));

        if (ObjectUtils.isNotEmpty(driver.getCar())) {
            throw new DriverAlreadyHasCarException(DRIVER_ALREADY_HAS_CAR_EXCEPTION_MESSAGE.formatted(driverExternalId));
        }

        var car = carMapper.toCar(carRequest);
        car.addDriver(driver);
        carRepo.save(car);
        driver.setDriverStatus(DriverStatus.AVAILABLE);

        sendRequestHandler.sendDriverInfoRequestToKafka(dataComposerUtils.buildDriverInfoMessage(driver));

        return carMapper.toDto(car);
    }

    @Override
    public CarResponse findByExternalId(UUID externalId) {
        var car = carRepo.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException(CAR_NOT_FOUND_EXCEPTION_MESSAGE.formatted(externalId)));
        return carMapper.toDto(car);
    }

    @Transactional(readOnly = true)
    @Override
    public AllCarsResponse findAllCars(Pageable pageable) {
        Page<CarView> allCarsViews = carRepo.findAllCarsViews(pageable);
        return dataComposerUtils.buildAllCarsDto(allCarsViews);
    }

    @Transactional
    @Override
    public CarResponse updateDriverCar(UUID driverExternalId, UpdateCarRequest updateCarRequest) {
        var driver = driverRepo.findByExternalId(driverExternalId)
                .orElseThrow(() -> new EntityNotFoundException(DRIVER_NOT_FOUND_EXCEPTION_MESSAGE.formatted(driverExternalId)));

        var car = driver.getCar();

        if (ObjectUtils.isEmpty(car)) {
            throw new DriverCarNotFoundException(DRIVER_HAS_NO_CARS_EXCEPTION_MESSAGE.formatted(driverExternalId));
        }

        carMapper.updateCar(updateCarRequest, car);
        driverRepo.save(driver);

        sendRequestHandler.sendDriverInfoRequestToKafka(dataComposerUtils.buildDriverInfoMessage(driver));

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
        driver.setDriverStatus(DriverStatus.NO_CAR);

        sendRequestHandler.sendDriverInfoRequestToKafka(dataComposerUtils.buildDriverInfoMessage(driver));
    }

}