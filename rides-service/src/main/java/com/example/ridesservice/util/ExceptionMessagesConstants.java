package com.example.ridesservice.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ExceptionMessagesConstants {
    public final String PASSENGER_RIDE_NOT_FOUND_EXCEPTION_MESSAGE =
            "Ride with externalId = %s not found for the passenger with externalId = %s";
    public final String PASSENGER_NOT_FOUND_EXCEPTION_MESSAGE = "Passenger was not found";
    public final String DRIVER_NOT_FOUND_EXCEPTION_MESSAGE = "Driver was not found by given externalId = %s";
    public final String DRIVER_ALREADY_IN_USE_EXCEPTION_MESSAGE = "Driver with externalId = %s should finished another ride";
    public final String RIDE_NOT_FOUND_EXCEPTION_MESSAGE = "Ride was not found by given externalId = %s";
    public final String RIDE_BELONG_ANOTHER_DRIVER_EXCEPTION_MESSAGE =
            "The ride with externalId = %s has already been taken by another driver";
    public final String RIDE_NOT_ACCEPTED_EXCEPTION_MESSAGE = "Ride was not accepted by given externalId = %s";
    public final String RIDE_ALREADY_STARTED_EXCEPTION_MESSAGE = "Ride already started by given externalId = %s";
    public final String RIDE_ALREADY_FINISHED_EXCEPTION_MESSAGE = "Ride already finished by given externalId = %s";
    public final String RIDE_NOT_STARTED_EXCEPTION_MESSAGE = "Ride was not started by given externalId = %s";

}
