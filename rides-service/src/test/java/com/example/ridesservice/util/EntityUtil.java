package com.example.ridesservice.util;

import com.example.ridesservice.model.CarInfo;
import com.example.ridesservice.model.DriverInfo;
import com.example.ridesservice.model.Ride;
import com.example.ridesservice.model.enums.DriverStatus;
import com.example.ridesservice.model.enums.PaymentMethod;
import com.example.ridesservice.model.enums.PaymentStatus;
import com.example.ridesservice.model.enums.RideStatus;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

public class EntityUtil {
    public static final Long FINISHED_RIDE_ID = 1L;
    public static final UUID FINISHED_RIDE_EXTERNAL_ID = UUID.fromString("2f2cae82-a434-11ee-a506-0242ac120002");
    public static final UUID FINISHED_RIDE_PASSENGER_EXTERNAL_ID = UUID.fromString("55bb3530-96b7-4adb-a9a6-c9062439fed8");
    public static final String FINISHED_RIDE_PICK_UP_ADDRESS = "St.Kupeq";
    public static final String FINISHED_RIDE_DESTINATION_ADDRESS = "St.Borisoqw";
    public static final Double FINISHED_RIDE_COST = 2.1;
    public static final PaymentMethod FINISHED_RIDE_PAYMENT_METHOD = PaymentMethod.CASH;
    public static final RideStatus FINISHED_RIDE_STATUS = RideStatus.FINISHED;
    public static final LocalTime FINISHED_RIDE_STARTED_AT = LocalTime.of(3, 45, 35);
    public static final long FINISHED_RIDE_DURATION = 11;
    public static final LocalDateTime FINISHED_RIDE_CREATED_AT = LocalDateTime.of(2023, 11, 12, 3, 43, 35);
    public static final PaymentStatus FINISHED_RIDE_PAYMENT_STATUS = PaymentStatus.PAID;

    public static final Long INITIATED_RIDE_ID = 4L;
    public static final UUID INITIATED_RIDE_EXTERNAL_ID = UUID.fromString("6df6a606-a478-11ee-a506-0242ac120002");
    public static final UUID INITIATED_RIDE_PASSENGER_EXTERNAL_ID = UUID.fromString("8e5ef207-e829-4bb5-818f-0c7c9ad0f9ed");

    public static final Long ACCEPTED_RIDE_ID = 3L;
    public static final UUID ACCEPTED_RIDE_EXTERNAL_ID = UUID.fromString("3ada7336-a434-11ee-a506-0242ac120002");
    public static final UUID ACCEPTED_RIDE_PASSENGER_EXTERNAL_ID = UUID.fromString("8e5ef207-e829-4bb5-818f-0c7c9ad0f9ed");

    public static final Long STARTED_RIDE_ID = 2L;
    public static final UUID STARTED_RIDE_EXTERNAL_ID = UUID.fromString("36922cce-a434-11ee-a506-0242ac120002");
    public static final UUID STARTED_RIDE_PASSENGER_EXTERNAL_ID = UUID.fromString("eea59cd6-0c9a-48de-8f17-263b496d1a5f");


    public static Long VLAD_ID = 11L;
    public static UUID VLAD_EXTERNAL_ID = UUID.fromString("54bb2de9-c518-488a-9898-015faa6dee3c");
    public static String VLAD_FIRST_NAME = "Vlad";
    public static String VLAD_LAST_NAME = "Vladovich";
    public static DriverStatus VLAD_STATUS = DriverStatus.AVAILABLE;
    public static String VLAD_CAR_LICENSE_PLATE = "8628AX-3";
    public static String VLAD_CAR_MODEL = "Honda Civic";
    public static String VLAD_CAR_COLOR = "white";

    public static Long GLEB_ID = 4L;
    public static UUID GLEB_EXTERNAL_ID = UUID.fromString("73247152-2e5a-473c-9dcc-2d630c2116ef");

    public static Long ANTON_ID = 5L;
    public static UUID ANTON_EXTERNAL_ID = UUID.fromString("63de8825-f2f8-4c55-b8d1-6318f797e7a1");

    public static Ride finishedRide(){
        return Ride.builder()
                .id(FINISHED_RIDE_ID)
                .externalId(FINISHED_RIDE_EXTERNAL_ID)
                .passengerExternalId(FINISHED_RIDE_PASSENGER_EXTERNAL_ID)
                .pickUpAddress(FINISHED_RIDE_PICK_UP_ADDRESS)
                .destinationAddress(FINISHED_RIDE_DESTINATION_ADDRESS)
                .rideCost(FINISHED_RIDE_COST)
                .paymentMethod(FINISHED_RIDE_PAYMENT_METHOD)
                .rideStatus(FINISHED_RIDE_STATUS)
                .rideStartedAt(FINISHED_RIDE_STARTED_AT)
                .rideDuration(FINISHED_RIDE_DURATION)
                .rideCreatedAt(FINISHED_RIDE_CREATED_AT)
                .paymentStatus(FINISHED_RIDE_PAYMENT_STATUS)
                .driver(initDriverVlad())
                .build();
    }

    public static DriverInfo initDriverVlad(){
        return new DriverInfo(VLAD_ID, VLAD_EXTERNAL_ID, VLAD_FIRST_NAME, VLAD_LAST_NAME, VLAD_STATUS, initCarVlad(), null);
    }

    public static CarInfo initCarVlad(){
        return new CarInfo(VLAD_CAR_LICENSE_PLATE, VLAD_CAR_MODEL, VLAD_CAR_COLOR);
    }

}
