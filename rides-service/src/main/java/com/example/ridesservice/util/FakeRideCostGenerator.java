package com.example.ridesservice.util;

import com.example.ridesservice.dto.request.CreateRideRequest;

public interface FakeRideCostGenerator {
    double calculateRideCost(CreateRideRequest createRideRequestDto);
}
