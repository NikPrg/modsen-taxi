package com.example.passengerservice.service;

import com.example.passengerservice.amqp.message.ChangeDefaultPaymentMethodMessage;
import com.example.passengerservice.amqp.message.ErrorInfoMessage;
import com.example.passengerservice.dto.request.ChangePhoneRequest;
import com.example.passengerservice.dto.request.PassengerRegistrationRequest;
import com.example.passengerservice.dto.request.PassengerRequest;
import com.example.passengerservice.dto.response.AllPassengersResponse;
import com.example.passengerservice.dto.response.CreatePassengerResponse;
import com.example.passengerservice.dto.response.PassengerResponse;
import com.example.passengerservice.dto.response.PaymentMethodResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PassengerService {

    CreatePassengerResponse signUp(PassengerRegistrationRequest passengerDto);

    PassengerResponse findPassengerByExternalId(UUID passengerExternalId);

    PaymentMethodResponse findPassengerPaymentMethod(UUID passengerExternalId);

    AllPassengersResponse findAllPassengers(Pageable pageable);

    PassengerResponse update(UUID passengerExternalId, PassengerRequest passengerDto);

    void addCardAsDefaultPaymentMethod(UUID passengerExternalId, UUID cardExternalId);

    void addCashAsDefaultPaymentMethod(UUID passengerExternalId);

    void delete(UUID passengerExternalId);

    void updatePassengerPhone(UUID passengerExternalId, ChangePhoneRequest changePhoneRequest);

    void updateDefaultPaymentMethod(ChangeDefaultPaymentMethodMessage message);

    void resetDefaultPaymentMethod(ErrorInfoMessage message);

}