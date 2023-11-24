package com.example.ridesservice.service;

import com.example.ridesservice.dto.request.CreateRideRequest;
import com.example.ridesservice.dto.response.ride.*;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface RideService {
    GetRideResponse findRideByPassengerExternalId(UUID passengerExternalId, UUID rideExternalId);

    AllRidesResponse findAllPassengerRides(UUID passengerExternalId, Pageable pageable);

    CreateRideResponse bookRide(UUID passengerExternalId, CreateRideRequest createRideDto);

    AcceptRideResponse acceptRide(UUID driverExternalId, UUID rideExternalId);

    StartRideResponse startRide(UUID driverExternalId, UUID rideExternalId);

    FinishRideResponse finishRide(UUID driverExternalId, UUID rideExternalId);
}
