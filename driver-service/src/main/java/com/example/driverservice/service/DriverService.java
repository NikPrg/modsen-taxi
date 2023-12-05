package com.example.driverservice.service;

import com.example.driverservice.amqp.message.DriverStatusMessage;
import com.example.driverservice.amqp.message.RideInfoMessage;
import com.example.driverservice.dto.request.DriverRequest;
import com.example.driverservice.dto.request.UpdateDriverRequest;
import com.example.driverservice.dto.response.AllDriversResponse;
import com.example.driverservice.dto.response.CreateDriverResponse;
import com.example.driverservice.dto.response.DriverResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface DriverService {
    CreateDriverResponse createDriver(DriverRequest createDriverRequest);

    DriverResponse findDriverByExternalId(UUID externalId);

    AllDriversResponse findAllDrivers(Pageable pageable);

    DriverResponse updateDriver(UUID externalId, UpdateDriverRequest updateDriverRequest);

    void deleteDriver(UUID externalId);

    void notifyDrivers(RideInfoMessage rideInfoMessage);

    void updateDriverStatus(DriverStatusMessage driverStatusMessage);
}
