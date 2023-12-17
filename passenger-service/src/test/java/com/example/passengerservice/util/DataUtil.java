package com.example.passengerservice.util;

import com.example.passengerservice.amqp.message.NewPassengerInfoMessage;
import com.example.passengerservice.dto.request.ChangePhoneRequest;
import com.example.passengerservice.dto.request.PassengerRegistrationRequest;
import com.example.passengerservice.dto.request.PassengerRequest;
import com.example.passengerservice.dto.response.CreatePassengerResponse;
import com.example.passengerservice.dto.response.PassengerResponse;
import com.example.passengerservice.dto.response.PaymentMethodResponse;
import com.example.passengerservice.model.Discount;
import com.example.passengerservice.model.Passenger;
import com.example.passengerservice.model.enums.PaymentMethod;

import java.time.LocalDateTime;
import java.util.UUID;

public class DataUtil {
    public static final Long PASSENGER_ID = 1L;
    public static final UUID PASSENGER_EXTERNAL_ID = UUID.randomUUID();
    public static final String FIRST_NAME = "Nikita";
    public static final String LAST_NAME = "Przhevalsky";
    public static final String PHONE = "+375259998881";
    public static final Double RATE = 5.0;
    public static final PaymentMethod PAYMENT_METHOD_CASH = PaymentMethod.CASH;
    public static final PaymentMethod PAYMENT_METHOD_CARD = PaymentMethod.CASH;
    public static final Discount DISCOUNT = null;
    public static final LocalDateTime CREATED_AT = LocalDateTime.now();

    public static final String NEW_FIRST_NAME = "Vlad";
    public static final String NEW_LAST_NAME = "Veremeter";
    public static final String NEW_PHONE = "+375257778881";

    public static Passenger defaultPassenger() {
        return Passenger.builder()
                .id(PASSENGER_ID)
                .externalId(PASSENGER_EXTERNAL_ID)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .phone(PHONE)
                .rate(RATE)
                .defaultPaymentMethod(PAYMENT_METHOD_CARD)
                .discount(DISCOUNT)
                .createdAt(CREATED_AT)
                .build();
    }

    public static Passenger defaultPassengerWithCardPaymentMethod() {
        return Passenger.builder()
                .id(PASSENGER_ID)
                .externalId(PASSENGER_EXTERNAL_ID)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .phone(PHONE)
                .rate(RATE)
                .defaultPaymentMethod(PAYMENT_METHOD_CASH)
                .discount(DISCOUNT)
                .createdAt(CREATED_AT)
                .build();
    }

    public static Passenger defaultUpdatedPassenger() {
        return Passenger.builder()
                .id(PASSENGER_ID)
                .externalId(PASSENGER_EXTERNAL_ID)
                .firstName(NEW_FIRST_NAME)
                .lastName(NEW_LAST_NAME)
                .phone(PHONE)
                .rate(RATE)
                .defaultPaymentMethod(PAYMENT_METHOD_CASH)
                .discount(DISCOUNT)
                .createdAt(CREATED_AT)
                .build();
    }

    public static PassengerRegistrationRequest defaultPassengerRegistrationRequest() {
        return PassengerRegistrationRequest.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .phone(PHONE)
                .build();
    }

    public static NewPassengerInfoMessage defaultNewPassengerMessage() {
        return NewPassengerInfoMessage.builder()
                .passengerExternalId(PASSENGER_EXTERNAL_ID)
                .build();
    }

    public static CreatePassengerResponse defaultCreatePassengerResponse() {
        return CreatePassengerResponse.builder()
                .id(PASSENGER_ID)
                .externalId(PASSENGER_EXTERNAL_ID)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .phone(PHONE)
                .rate(RATE)
                .paymentMethod(PAYMENT_METHOD_CASH)
                .discount(DISCOUNT)
                .build();
    }

    public static PassengerResponse defaultPassengerResponse() {
        return PassengerResponse.builder()
                .id(PASSENGER_ID)
                .externalId(PASSENGER_EXTERNAL_ID)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .phone(PHONE)
                .rate(RATE)
                .build();
    }

    public static PaymentMethodResponse defaultPaymentMethodResponseCash() {
        return PaymentMethodResponse.builder()
                .paymentMethod(PAYMENT_METHOD_CASH)
                .build();
    }

    public static PassengerRequest defaultPassengerRequest() {
        return PassengerRequest.builder()
                .firstName(NEW_FIRST_NAME)
                .lastName(NEW_LAST_NAME)
                .build();
    }

    public static ChangePhoneRequest defaultChangePhoneRequest() {
        return ChangePhoneRequest.builder()
                .phone(NEW_PHONE)
                .build();
    }
}
