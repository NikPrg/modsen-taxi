package com.example.passengerservice.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ExceptionMessagesConstants {
    public final String PASSENGER_NOT_FOUND_EXCEPTION_MESSAGE = "Passenger was not found by given externalId = %s";
    public final String USER_WITH_THE_SAME_PHONE_IS_EXISTS_MESSAGE = "User with similar phone = %s is already exists";
}
