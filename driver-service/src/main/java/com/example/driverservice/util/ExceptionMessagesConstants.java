package com.example.driverservice.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ExceptionMessagesConstants {
    public final String DRIVER_NOT_FOUND_EXCEPTION_MESSAGE = "Driver was not found by given externalId = %s";
    public final String CAR_NOT_FOUND_EXCEPTION_MESSAGE = "Car was not found by given externalId = %s";
    public final String DRIVER_HAS_NO_CARS_EXCEPTION_MESSAGE = "Driver with externalId = %s, does not have a car";
    public final String DRIVER_ALREADY_HAS_CAR_EXCEPTION_MESSAGE = "Driver with externalId = %s, already have a car";
    public final String NO_DRIVERS_AVAILABLE_EXCEPTION_MESSAGE = "There is no available driver for your trip, please try a bit later ";
    public final String CAR_NOT_BELONG_EXCEPTION_MESSAGE =
            "Car with externalId = %s, does not belong to the driver with externalId = %s";

}