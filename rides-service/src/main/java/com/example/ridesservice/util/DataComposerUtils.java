package com.example.ridesservice.util;

import com.example.ridesservice.amqp.message.DriverStatusMessage;
import com.example.ridesservice.amqp.message.RideInfoMessage;
import com.example.ridesservice.amqp.message.RidePaymentMessage;
import com.example.ridesservice.dto.response.ride.AllRidesResponse;
import com.example.ridesservice.feign.response.DefaultCardResponse;
import com.example.ridesservice.model.DriverInfo;
import com.example.ridesservice.model.Ride;
import com.example.ridesservice.model.projection.RideView;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;

public class DataComposerUtils {
    public static AllRidesResponse buildAllRidesDto(Page<RideView> allPassengerRidesViews) {
        return AllRidesResponse.builder()
                .rideViewList(allPassengerRidesViews.getContent())
                .currentPageNumber(allPassengerRidesViews.getNumber())
                .totalPages(allPassengerRidesViews.getTotalPages())
                .totalElements(allPassengerRidesViews.getTotalElements())
                .build();
    }

    public static RideInfoMessage buildRideInfoMessage(Ride ride) {
        return RideInfoMessage.builder()
                .externalId(ride.getExternalId())
                .pickUpAddress(ride.getPickUpAddress())
                .destinationAddress(ride.getDestinationAddress())
                .cost(ride.getRideCost())
                .build();
    }

    public static DriverStatusMessage buildDriverStatusMessage(DriverInfo driver) {
        return DriverStatusMessage.builder()
                .driverExternalId(driver.getExternalId())
                .driverStatus(driver.getDriverStatus())
                .build();
    }

    public static RidePaymentMessage buildRidePaymentMessage(DefaultCardResponse defaultCard, Ride ride) {
        return RidePaymentMessage.builder()
                .cardExternalId(defaultCard.cardExternalId())
                .rideExternalId(ride.getExternalId())
                .rideCost(BigDecimal.valueOf(ride.getRideCost()))
                .build();
    }
}
