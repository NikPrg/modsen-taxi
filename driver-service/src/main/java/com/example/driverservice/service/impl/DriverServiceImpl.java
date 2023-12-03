package com.example.driverservice.service.impl;

import com.example.driverservice.amqp.channelGateway.KafkaChannelGateway;
import com.example.driverservice.amqp.message.DriverStatusMessage;
import com.example.driverservice.amqp.message.RideInfoMessage;
import com.example.driverservice.dto.request.DriverRequest;
import com.example.driverservice.dto.request.UpdateDriverRequest;
import com.example.driverservice.dto.response.AllDriversResponse;
import com.example.driverservice.dto.response.CreateDriverResponse;
import com.example.driverservice.dto.response.DriverResponse;
import com.example.driverservice.mapper.DriverMapper;
import com.example.driverservice.model.entity.Driver;
import com.example.driverservice.model.enums.DriverStatus;
import com.example.driverservice.model.projections.DriverView;
import com.example.driverservice.repository.DriverRepository;
import com.example.driverservice.service.DriverService;
import com.example.driverservice.util.BuildFactory;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

import static com.example.driverservice.util.ExceptionMessagesConstants.*;

@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepo;
    private final DriverMapper driverMapper;
    private final KafkaChannelGateway kafkaChannelGateway;
    private final WebClient webClient;
    private final BuildFactory buildFactory;
    @Value("${app.routes.rides.accept-ride-method}")
    private String ridesAcceptRideMethodUri;

    @Transactional
    @Override
    public CreateDriverResponse createDriver(DriverRequest createDriverRequest) {
        var driver = driverMapper.toDriver(createDriverRequest);

        driver.setDriverStatus(DriverStatus.CREATED);
        driverRepo.save(driver);

        kafkaChannelGateway.sendDriverInfoRequestToKafka(
                new GenericMessage<>(buildFactory.buildDriverInfoMessage(driver)));

        return driverMapper.toCreateDto(driver);
    }

    @Override
    public DriverResponse findDriverByExternalId(UUID externalId) {
        var driver = driverRepo.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException(DRIVER_NOT_FOUND_EXCEPTION_MESSAGE.formatted(externalId)));
        return driverMapper.toDto(driver);
    }

    @Transactional(readOnly = true)
    @Override
    public AllDriversResponse findAllDrivers(Pageable pageable) {
        Page<DriverView> allDriversViews = driverRepo.findAllDriversViews(pageable);
        return buildFactory.buildAllDriversDto(allDriversViews);
    }

    @Transactional
    @Override
    public DriverResponse updateDriver(UUID externalId, UpdateDriverRequest updateDriverRequest) {
        var storedDriver = driverRepo.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException(DRIVER_NOT_FOUND_EXCEPTION_MESSAGE.formatted(externalId)));
        driverMapper.updateDriver(updateDriverRequest, storedDriver);

        kafkaChannelGateway.sendDriverInfoRequestToKafka(
                new GenericMessage<>(buildFactory.buildDriverInfoMessage(storedDriver)));

        return driverMapper.toDto(storedDriver);
    }

    @Transactional
    @Override
    public void deleteDriver(UUID externalId) {
        var driver = driverRepo.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException(DRIVER_NOT_FOUND_EXCEPTION_MESSAGE.formatted(externalId)));

        driver.setDriverStatus(DriverStatus.DELETED);
        driverRepo.delete(driver);

        kafkaChannelGateway.sendDriverInfoRequestToKafka(
                new GenericMessage<>(buildFactory.buildDriverInfoMessage(driver)));
    }

    @Transactional
    @Override
    public void notificationDrivers(RideInfoMessage rideInfoMessage) {
        driverRepo.findAll().stream()
                .filter(driver -> DriverStatus.AVAILABLE.equals(driver.getDriverStatus()))
                .findFirst()
                .ifPresentOrElse(driver -> {
                            driver.setDriverStatus(DriverStatus.TOWARDS_PASSENGER);
                            sendRideAcceptConfirmation(rideInfoMessage, driver);
                        },
                        () -> {
                            throw new EntityNotFoundException(NO_DRIVERS_AVAILABLE_EXCEPTION_MESSAGE);
                        });
    }

    @Transactional
    @Override
    public void updateDriverStatus(DriverStatusMessage driverStatusMessage) {
        UUID driverExternalId = driverStatusMessage.driverExternalId();

        var driver = driverRepo.findByExternalId(driverExternalId)
                .orElseThrow(() -> new EntityNotFoundException(DRIVER_NOT_FOUND_EXCEPTION_MESSAGE.formatted(driverExternalId)));

        driver.setDriverStatus(driverStatusMessage.driverStatus());

    }

    private void sendRideAcceptConfirmation(RideInfoMessage rideInfoMessage, Driver driver) {
        UUID rideExternalId = rideInfoMessage.externalId();
        UUID driverExternalId = driver.getExternalId();

        webClient.post()
                .uri(ridesAcceptRideMethodUri, rideExternalId, driverExternalId)
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Void.class)
                .subscribe();
    }

}