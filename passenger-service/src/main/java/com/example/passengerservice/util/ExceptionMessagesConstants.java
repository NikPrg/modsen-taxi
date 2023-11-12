package com.example.passengerservice.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ExceptionMessagesConstants {

    public static String PASSENGER_WITH_THE_SAME_PHONE_IS_EXISTS_MESSAGE =
            "Passenger with similar phone = %s is already exists";

    public static String PASSENGER_NOT_FOUND_ERROR_MESSAGE = "Passenger was not found by given externalId = %s";

    public static String CARD_NOT_FOUND_ERROR_MESSAGE = "Card was not found by given externalId = %s";

    public static String CARD_ALREADY_EXIST_ERROR_MESSAGE = "Card already exist for this passenger = %s";

}
