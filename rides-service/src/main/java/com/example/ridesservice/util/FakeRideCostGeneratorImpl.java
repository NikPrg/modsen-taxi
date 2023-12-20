package com.example.ridesservice.util;

import com.example.ridesservice.dto.request.CreateRideRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FakeRideCostGeneratorImpl implements FakeRideCostGenerator {

    @Value("${app.config.base-cost-per-km}")
    private int baseCostPerKm;

    @Override
    public double calculateRideCost(CreateRideRequest createRideRequestDto) {

        var pickUpAddress = createRideRequestDto.pickUpAddress();
        var destinationAddress = createRideRequestDto.destinationAddress();
        var totalDistance = getTotalDistance(pickUpAddress, destinationAddress);

        return Math.round(totalDistance * baseCostPerKm);
    }

    private double getTotalDistance(String pickUpAddress, String destinationAddress) {
        return pickUpAddress.concat(destinationAddress).length() * Math.random() * 3;
    }
}
