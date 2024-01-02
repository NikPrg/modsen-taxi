package com.example.ridesservice.util;

import com.example.ridesservice.amqp.message.DriverStatusMessage;
import com.example.ridesservice.amqp.message.RideInfoMessage;
import com.example.ridesservice.dto.request.CreateRideRequest;
import com.example.ridesservice.dto.response.DriverInfoResponse;
import com.example.ridesservice.dto.response.ride.*;
import com.example.ridesservice.feign.response.PaymentMethodResponse;
import com.example.ridesservice.model.DriverInfo;
import com.example.ridesservice.model.Ride;
import com.example.ridesservice.model.enums.DriverStatus;
import com.example.ridesservice.model.enums.PaymentMethod;
import com.example.ridesservice.model.enums.PaymentStatus;
import com.example.ridesservice.model.enums.RideStatus;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

public class DataUtil {
    public static final Long RIDE_ID = 1L;
    public static final UUID RIDE_EXTERNAL_ID = UUID.randomUUID();
    public static final UUID PASSENGER_EXTERNAL_ID = UUID.randomUUID();
    public static final String PICK_UP_ADDRESS = "30/46 Bogdana Khmelnytskogo st.";
    public static final String DESTINATION_ADDRESS = "Stefana Okrzei 1a/10";
    public static final Double RIDE_COST = 20.1;
    public static final PaymentMethod RIDE_PAYMENT_METHOD_CASH = PaymentMethod.CASH;
    public static final PaymentStatus RIDE_PAYMENT_STATUS_PAID = PaymentStatus.PAID;
    public static final RideStatus RIDE_STATUS_INITIATED = RideStatus.INITIATED;
    public static final RideStatus RIDE_STATUS_ACCEPTED = RideStatus.ACCEPTED;
    public static final RideStatus RIDE_STATUS_STARTED = RideStatus.STARTED;
    public static final RideStatus RIDE_STATUS_FINISHED = RideStatus.FINISHED;
    public static final LocalTime RIDE_STARTED_AT = LocalTime.now().plusMinutes(5);
    public static final long RIDE_DURATION = 20;
    public static final LocalDateTime RIDE_CREATED_AT = LocalDateTime.now();

    public static final Long DRIVER_INFO_ID = 1L;
    public static final UUID DRIVER_INFO_EXTERNAL_ID = UUID.randomUUID();
    public static final String DRIVER_INFO_FIRST_NAME = "Steven";
    public static final String DRIVER_INFO_LAST_NAME = "Haminkton";
    public static final DriverStatus DRIVER_INFO_STATUS_AVAILABLE = DriverStatus.AVAILABLE;
    public static final DriverStatus DRIVER_INFO_STATUS_UNAVAILABLE = DriverStatus.UNAVAILABLE;

    public static final UUID NOT_EXISTED_EXTERNAL_ID = UUID.randomUUID();

    public static Ride defaultFinishedRideCash() {
        return Ride.builder()
                .id(RIDE_ID)
                .externalId(RIDE_EXTERNAL_ID)
                .passengerExternalId(PASSENGER_EXTERNAL_ID)
                .pickUpAddress(PICK_UP_ADDRESS)
                .destinationAddress(DESTINATION_ADDRESS)
                .rideCost(RIDE_COST)
                .paymentMethod(RIDE_PAYMENT_METHOD_CASH)
                .paymentStatus(RIDE_PAYMENT_STATUS_PAID)
                .rideStatus(RIDE_STATUS_FINISHED)
                .rideStartedAt(RIDE_STARTED_AT)
                .rideDuration(RIDE_DURATION)
                .driver(initDriverInfo())
                .rideCreatedAt(RIDE_CREATED_AT)
                .build();
    }

    public static GetRideResponse defaultGetRideResponse() {
        return GetRideResponse.builder()
                .id(RIDE_ID)
                .externalId(RIDE_EXTERNAL_ID)
                .passengerExternalId(PASSENGER_EXTERNAL_ID)
                .pickUpAddress(PICK_UP_ADDRESS)
                .destinationAddress(DESTINATION_ADDRESS)
                .rideCost(RIDE_COST)
                .rideDuration(RIDE_DURATION)
                .paymentMethod(RIDE_PAYMENT_METHOD_CASH)
                .driver(defaultDriverInfoResponse())
                .rideStatus(RideStatus.FINISHED)
                .build();
    }

    public static DriverInfoResponse defaultDriverInfoResponse() {
        return new DriverInfoResponse(DRIVER_INFO_FIRST_NAME, DRIVER_INFO_LAST_NAME, null);
    }

    public static PaymentMethodResponse defaultPaymentMethodResponseCash() {
        return new PaymentMethodResponse(RIDE_PAYMENT_METHOD_CASH);
    }

    public static Ride defaultInitiatedRide() {
        return Ride.builder()
                .id(RIDE_ID)
                .externalId(RIDE_EXTERNAL_ID)
                .passengerExternalId(PASSENGER_EXTERNAL_ID)
                .pickUpAddress(PICK_UP_ADDRESS)
                .destinationAddress(DESTINATION_ADDRESS)
                .rideCost(RIDE_COST)
                .paymentMethod(null)
                .paymentStatus(null)
                .rideStatus(RIDE_STATUS_INITIATED)
                .rideStartedAt(null)
                .driver(null)
                .rideCreatedAt(RIDE_CREATED_AT)
                .build();
    }

    public static CreateRideRequest defaultCreateRideRequest() {
        return new CreateRideRequest(PICK_UP_ADDRESS, DESTINATION_ADDRESS);
    }

    public static CreateRideResponse defaultCreateRideResponse() {
        return CreateRideResponse.builder()
                .id(RIDE_ID)
                .externalId(RIDE_EXTERNAL_ID)
                .passengerExternalId(PASSENGER_EXTERNAL_ID)
                .pickUpAddress(PICK_UP_ADDRESS)
                .destinationAddress(DESTINATION_ADDRESS)
                .rideCost(RIDE_COST)
                .rideStatus(RIDE_STATUS_INITIATED)
                .build();
    }

    public static RideInfoMessage defaultRideInfoMessage() {
        return RideInfoMessage.builder()
                .externalId(RIDE_EXTERNAL_ID)
                .pickUpAddress(PICK_UP_ADDRESS)
                .destinationAddress(DESTINATION_ADDRESS)
                .cost(RIDE_COST)
                .build();
    }

    public static DriverInfo defaultAvailableDriver() {
        return DriverInfo.builder()
                .id(DRIVER_INFO_ID)
                .externalId(DRIVER_INFO_EXTERNAL_ID)
                .firstName(DRIVER_INFO_FIRST_NAME)
                .lastName(DRIVER_INFO_LAST_NAME)
                .carInfo(null)
                .driverStatus(DRIVER_INFO_STATUS_AVAILABLE)
                .build();
    }

    public static DriverInfo defaultUnavailableDriver() {
        return DriverInfo.builder()
                .id(DRIVER_INFO_ID)
                .externalId(DRIVER_INFO_EXTERNAL_ID)
                .firstName(DRIVER_INFO_FIRST_NAME)
                .lastName(DRIVER_INFO_LAST_NAME)
                .carInfo(null)
                .driverStatus(DRIVER_INFO_STATUS_UNAVAILABLE)
                .build();
    }

    public static AcceptRideResponse defaultAcceptRideResponse() {
        return AcceptRideResponse.builder()
                .id(RIDE_ID)
                .externalId(RIDE_EXTERNAL_ID)
                .rideCost(RIDE_COST)
                .rideStatus(RIDE_STATUS_ACCEPTED)
                .build();
    }

    public static FinishRideResponse defaultFinishRideResponseCash() {
        return FinishRideResponse.builder()
                .id(RIDE_ID)
                .externalId(RIDE_EXTERNAL_ID)
                .passengerExternalId(PASSENGER_EXTERNAL_ID)
                .pickUpAddress(PICK_UP_ADDRESS)
                .destinationAddress(DESTINATION_ADDRESS)
                .rideCost(RIDE_COST)
                .rideDuration(RIDE_DURATION)
                .paymentMethod(RIDE_PAYMENT_METHOD_CASH)
                .rideStatus(RIDE_STATUS_FINISHED)
                .build();
    }

    public static Ride defaultAcceptedRide() {
        return Ride.builder()
                .id(RIDE_ID)
                .externalId(RIDE_EXTERNAL_ID)
                .passengerExternalId(PASSENGER_EXTERNAL_ID)
                .pickUpAddress(PICK_UP_ADDRESS)
                .destinationAddress(DESTINATION_ADDRESS)
                .rideCost(RIDE_COST)
                .paymentMethod(null)
                .paymentStatus(null)
                .rideStatus(RIDE_STATUS_ACCEPTED)
                .rideStartedAt(null)
                .driver(initDriverInfo())
                .rideCreatedAt(RIDE_CREATED_AT)
                .build();
    }

    public static Ride defaultStartedRide() {
        return Ride.builder()
                .id(RIDE_ID)
                .externalId(RIDE_EXTERNAL_ID)
                .passengerExternalId(PASSENGER_EXTERNAL_ID)
                .pickUpAddress(PICK_UP_ADDRESS)
                .destinationAddress(DESTINATION_ADDRESS)
                .rideCost(RIDE_COST)
                .paymentMethod(null)
                .paymentStatus(null)
                .rideStatus(RIDE_STATUS_STARTED)
                .rideStartedAt(null)
                .driver(initDriverInfo())
                .rideCreatedAt(RIDE_CREATED_AT)
                .build();
    }

    public static DriverInfo initDriverInfo() {
        return DriverInfo.builder()
                .id(DRIVER_INFO_ID)
                .externalId(DRIVER_INFO_EXTERNAL_ID)
                .build();
    }

    public static DriverStatusMessage defaultDriverStatusMessageUnavailable() {
        return DriverStatusMessage.builder()
                .driverExternalId(DRIVER_INFO_EXTERNAL_ID)
                .driverStatus(DRIVER_INFO_STATUS_UNAVAILABLE)
                .build();
    }

    public static DriverStatusMessage defaultDriverStatusMessageAvailable() {
        return DriverStatusMessage.builder()
                .driverExternalId(DRIVER_INFO_EXTERNAL_ID)
                .driverStatus(DRIVER_INFO_STATUS_AVAILABLE)
                .build();
    }

    public static StartRideResponse defaultStartRideResponse() {
        return new StartRideResponse(RIDE_ID, RIDE_EXTERNAL_ID, RIDE_STATUS_STARTED);
    }
}
