package com.example.passengerservice.service.impl;

import com.example.passengerservice.model.Card;
import com.example.passengerservice.model.Passenger;
import com.example.passengerservice.model.projections.PassengerView;
import com.example.passengerservice.dto.request.PassengerRegistrationDto;
import com.example.passengerservice.dto.request.PassengerRequestDto;
import com.example.passengerservice.dto.response.CreatePassengerResponse;
import com.example.passengerservice.dto.response.PassengerResponseDto;
import com.example.passengerservice.mapper.PassengerMapper;
import com.example.passengerservice.model.PaymentMethod;
import com.example.passengerservice.repository.CardRepository;
import com.example.passengerservice.repository.PassengerRepository;
import com.example.passengerservice.service.PassengerService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.example.passengerservice.util.ExceptionMessagesConstants.*;

@Service
@RequiredArgsConstructor
public class PassengerServiceImpl implements PassengerService {

    private final PassengerMapper passengerMapper;
    private final PassengerRepository passengerRepo;
    private final CardRepository cardRepo;

    @Transactional
    @Override
    public CreatePassengerResponse signUp(PassengerRegistrationDto passengerDto) {
        val passenger = passengerMapper.toPassenger(passengerDto);
        passengerRepo.save(passenger);
        return passengerMapper.toCreateDto(passenger);
    }

    @Override
    public PassengerResponseDto findPassengerByExternalId(UUID externalId) {
        val passenger = passengerRepo.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException(PASSENGER_NOT_FOUND_EXCEPTION_MESSAGE.formatted(externalId)));
        return passengerMapper.toDto(passenger);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<PassengerView> findAllPassengers(Pageable pageable) {
        return passengerRepo.findAllPassengersView(pageable);
    }

    @Transactional
    @Override
    public PassengerResponseDto update(UUID externalId, PassengerRequestDto passengerDto) {
        var passenger = passengerRepo.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException(PASSENGER_NOT_FOUND_EXCEPTION_MESSAGE.formatted(externalId)));
        passengerMapper.updatePassenger(passengerDto, passenger);
        return passengerMapper.toDto(passenger);
    }

    @Transactional
    @Override
    public void addCardAsDefaultPaymentMethod(UUID passengerExternalId, UUID cardExternalId) {
        var passenger = passengerRepo.findByExternalId(passengerExternalId)
                .orElseThrow(() -> new EntityNotFoundException(PASSENGER_NOT_FOUND_EXCEPTION_MESSAGE.formatted(passengerExternalId)));

        cardRepo.findByExternalId(cardExternalId)
                .map(card -> addCardIfPassengerContains(passenger, card))
                .orElseThrow(() ->
                        new EntityNotFoundException(CARD_NOT_FOUND_EXCEPTION_MESSAGE.formatted(cardExternalId)));

        passengerRepo.save(passenger);
    }

    @Transactional
    @Override
    public void addCashAsDefaultPaymentMethod(UUID passengerExternalId) {
        var passenger = passengerRepo.findByExternalId(passengerExternalId)
                .orElseThrow(() -> new EntityNotFoundException(PASSENGER_NOT_FOUND_EXCEPTION_MESSAGE.formatted(passengerExternalId)));

        passenger.getCards().forEach(card -> card.setUsedAsDefault(false));
        passenger.setDefaultPaymentMethod(PaymentMethod.CASH);

        passengerRepo.save(passenger);
    }

    @Transactional
    @Override
    public void delete(UUID id) {
        passengerRepo.deleteByExternalId(id);
    }

    private String addCardIfPassengerContains(Passenger passenger, Card card) {
        if (card.getPassengers().contains(passenger)) {
            if (PaymentMethod.CASH.equals(passenger.getDefaultPaymentMethod())) {
                passenger.setDefaultPaymentMethod(PaymentMethod.CARD);
                card.setUsedAsDefault(true);
            } else {
                passenger.getCards().forEach(pCard -> pCard.setUsedAsDefault(false));
                card.setUsedAsDefault(true);
            }
        }
        return Strings.EMPTY;
    }

}