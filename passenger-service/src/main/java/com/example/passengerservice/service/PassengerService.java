package com.example.passengerservice.service;

import com.example.passengerservice.dto.request.ChangePhoneRequest;
import com.example.passengerservice.dto.request.PassengerRegistrationDto;
import com.example.passengerservice.dto.request.PassengerRequestDto;
import com.example.passengerservice.dto.response.CreatePassengerResponse;
import com.example.passengerservice.dto.response.PassengerResponse;
import com.example.passengerservice.dto.response.PaymentInfoResponse;
import com.example.passengerservice.model.projections.PassengerView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PassengerService {

    CreatePassengerResponse signUp(PassengerRegistrationDto passengerDto);

    PassengerResponse findPassengerByExternalId(UUID externalId);

    PaymentInfoResponse findPassengerPaymentInfo(UUID externalId);

    Page<PassengerView> findAllPassengers(Pageable pageable);

    PassengerResponse update(UUID externalId, PassengerRequestDto passengerDto);

    void addCardAsDefaultPaymentMethod(UUID passengerExternalId, UUID cardExternalId);

    void addCashAsDefaultPaymentMethod(UUID externalId);

    void delete(UUID externalId);

    void updatePassengerPhone(UUID externalId, ChangePhoneRequest changePhoneRequest);

}