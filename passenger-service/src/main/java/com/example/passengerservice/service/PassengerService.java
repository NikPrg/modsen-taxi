package com.example.passengerservice.service;

import com.example.passengerservice.dto.response.PaymentInfoResponse;
import com.example.passengerservice.model.projections.PassengerView;
import com.example.passengerservice.dto.request.PassengerRegistrationDto;
import com.example.passengerservice.dto.request.PassengerRequestDto;
import com.example.passengerservice.dto.response.CreatePassengerResponse;
import com.example.passengerservice.dto.response.PassengerResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PassengerService {

    CreatePassengerResponse signUp(PassengerRegistrationDto passengerDto);

    PassengerResponseDto findPassengerByExternalId(UUID externalId);

    PaymentInfoResponse findPassengerPaymentInfo(UUID passengerExternalId);

    Page<PassengerView> findAllPassengers(Pageable pageable);

    PassengerResponseDto update(UUID externalId, PassengerRequestDto passengerDto);

    void addCardAsDefaultPaymentMethod(UUID passengerExternalId, UUID cardExternalId);

    void addCashAsDefaultPaymentMethod(UUID passengerExternalId);

    void delete(UUID externalId);
}