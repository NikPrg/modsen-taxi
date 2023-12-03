package com.example.ridesservice.util;

import com.example.ridesservice.amqp.message.DriverStatusMessage;
import com.example.ridesservice.amqp.message.RideInfoMessage;
import com.example.ridesservice.dto.response.ride.AllRidesResponse;
import com.example.ridesservice.model.DriverInfo;
import com.example.ridesservice.model.Ride;
import com.example.ridesservice.model.projection.RideView;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BuildFactory {
    public AllRidesResponse buildAllRidesDto(Page<RideView> allPassengerRidesViews) {
        return AllRidesResponse.builder()
                .rideViewList(allPassengerRidesViews.getContent())
                .currentPageNumber(allPassengerRidesViews.getNumber())
                .totalPages(allPassengerRidesViews.getTotalPages())
                .totalElements(allPassengerRidesViews.getTotalElements())
                .build();
    }

    public RideInfoMessage buildRideInfoMessage(Ride ride) {
        return RideInfoMessage.builder()
                .externalId(ride.getExternalId())
                .pickUpAddress(ride.getPickUpAddress())
                .destinationAddress(ride.getDestinationAddress())
                .cost(ride.getRideCost())
                .build();
    }

    public DriverStatusMessage buildDriverStatusMessage(DriverInfo driver) {
        return DriverStatusMessage.builder()
                .driverExternalId(driver.getExternalId())
                .driverStatus(driver.getDriverStatus())
                .build();
    }
}
