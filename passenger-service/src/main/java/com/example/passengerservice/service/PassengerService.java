package com.example.passengerservice.service;

import com.example.passengerservice.amqp.message.ChangeDefaultPaymentMethodMessage;
import com.example.passengerservice.amqp.message.ErrorInfoMessage;
import com.example.passengerservice.dto.request.ChangePhoneRequest;
import com.example.passengerservice.dto.request.PassengerRegistrationRequest;
import com.example.passengerservice.dto.request.PassengerRequest;
import com.example.passengerservice.dto.response.AllPassengersResponse;
import com.example.passengerservice.dto.response.CreatePassengerResponse;
import com.example.passengerservice.dto.response.PassengerResponse;
import com.example.passengerservice.dto.response.PaymentInfoResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PassengerService {

    CreatePassengerResponse signUp(PassengerRegistrationRequest passengerDto);

    PassengerResponse findPassengerByExternalId(UUID externalId);

    PaymentInfoResponse findPassengerPaymentInfo(UUID externalId);

    AllPassengersResponse findAllPassengers(Pageable pageable);

    PassengerResponse update(UUID externalId, PassengerRequest passengerDto);

    void addCardAsDefaultPaymentMethod(UUID passengerExternalId, UUID cardExternalId);

    void addCashAsDefaultPaymentMethod(UUID externalId);

    void delete(UUID externalId);

    void updatePassengerPhone(UUID externalId, ChangePhoneRequest changePhoneRequest);

    void updateDefaultPaymentMethod(ChangeDefaultPaymentMethodMessage message);

    void resetDefaultPaymentMethod(ErrorInfoMessage message);

}