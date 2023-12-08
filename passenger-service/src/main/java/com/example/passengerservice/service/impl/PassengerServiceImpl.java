package com.example.passengerservice.service.impl;

import com.example.passengerservice.dto.request.ChangePhoneRequest;
import com.example.passengerservice.dto.request.PassengerRegistrationDto;
import com.example.passengerservice.dto.request.PassengerRequestDto;
import com.example.passengerservice.dto.response.CreatePassengerResponse;
import com.example.passengerservice.dto.response.PassengerResponse;
import com.example.passengerservice.dto.response.PaymentInfoResponse;
import com.example.passengerservice.exception.CardNotBelongPassengerException;
import com.example.passengerservice.mapper.PassengerMapper;
import com.example.passengerservice.model.Card;
import com.example.passengerservice.model.Passenger;
import com.example.passengerservice.model.PassengerCard;
import com.example.passengerservice.model.PaymentMethod;
import com.example.passengerservice.model.projections.PassengerView;
import com.example.passengerservice.repository.CardRepository;
import com.example.passengerservice.repository.PassengerRepository;
import com.example.passengerservice.service.PassengerService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.val;
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
        checkPhoneForUniqueness(passengerDto.phone());
        val passenger = passengerMapper.toPassenger(passengerDto);
        passengerRepo.save(passenger);
        return passengerMapper.toCreateDto(passenger);
    }

    @Override
    public PassengerResponse findPassengerByExternalId(UUID externalId) {
        val passenger = passengerRepo.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException(PASSENGER_NOT_FOUND_EXCEPTION_MESSAGE.formatted(externalId)));
        return passengerMapper.toDto(passenger);
    }

    @Override
    public PaymentInfoResponse findPassengerPaymentInfo(UUID externalId) {
        val passenger = passengerRepo.findByExternalIdFetch(externalId)
                .orElseThrow(() -> new EntityNotFoundException(PASSENGER_NOT_FOUND_EXCEPTION_MESSAGE.formatted(externalId)));
        return passengerMapper.toPaymentInfoDto(passenger);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<PassengerView> findAllPassengers(Pageable pageable) {
        return passengerRepo.findAllPassengersView(pageable);
    }

    @Transactional
    @Override
    public PassengerResponse update(UUID externalId, PassengerRequestDto passengerDto) {
        var passenger = passengerRepo.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException(PASSENGER_NOT_FOUND_EXCEPTION_MESSAGE.formatted(externalId)));
        passengerMapper.updatePassenger(passengerDto, passenger);
        return passengerMapper.toDto(passenger);
    }

    @Transactional
    @Override
    public void updatePassengerPhone(UUID externalId, ChangePhoneRequest changePhoneRequest) {
        var updatedPhone = changePhoneRequest.phone();

        checkPhoneForUniqueness(updatedPhone);
        var passenger = passengerRepo.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException(PASSENGER_NOT_FOUND_EXCEPTION_MESSAGE.formatted(externalId)));

        passenger.setPhone(updatedPhone);
    }

    @Transactional
    @Override
    public void addCardAsDefaultPaymentMethod(UUID passengerExternalId, UUID cardExternalId) {
        var passenger = passengerRepo.findByExternalIdFetch(passengerExternalId)
                .orElseThrow(() -> new EntityNotFoundException(PASSENGER_NOT_FOUND_EXCEPTION_MESSAGE.formatted(passengerExternalId)));

        var card = cardRepo.findByExternalId(cardExternalId)
                .orElseThrow(() -> new EntityNotFoundException(CARD_NOT_FOUND_EXCEPTION_MESSAGE.formatted(cardExternalId)));

        addCardIfPassengerBelong(passenger, card);

        passengerRepo.save(passenger);
    }

    @Transactional
    @Override
    public void addCashAsDefaultPaymentMethod(UUID externalId) {
        var passenger = passengerRepo.findByExternalIdFetch(externalId)
                .orElseThrow(() -> new EntityNotFoundException(PASSENGER_NOT_FOUND_EXCEPTION_MESSAGE.formatted(externalId)));

        passenger.getCards().forEach(passCard -> passCard.setUsedAsDefault(false));

        passenger.setDefaultPaymentMethod(PaymentMethod.CASH);

        passengerRepo.save(passenger);
    }

    @Transactional
    @Override
    public void delete(UUID externalId) {
        var passenger = passengerRepo.findByExternalIdFetch(externalId)
                .orElseThrow(() -> new EntityNotFoundException(PASSENGER_NOT_FOUND_EXCEPTION_MESSAGE.formatted(externalId)));

        passengerRepo.delete(passenger);
    }

    private void addCardIfPassengerBelong(Passenger passenger, Card card) {
        PassengerCard passengerCard = card.getPassengers().stream()
                .filter(passCard -> passCard.getPassenger().equals(passenger))
                .findFirst()
                .orElseThrow(() -> new CardNotBelongPassengerException(CARD_NOT_BELONG_PASSENGER_EXCEPTION_MESSAGE.formatted(card.getExternalId(), passenger.getExternalId())));

        if (PaymentMethod.CASH.equals(passenger.getDefaultPaymentMethod())) {
            passenger.setDefaultPaymentMethod(PaymentMethod.CARD);
            passengerCard.setUsedAsDefault(true);
        } else {
            passenger.getCards().forEach(passCard -> passCard.setUsedAsDefault(false));
            passengerCard.setUsedAsDefault(true);
        }
    }

    private void checkPhoneForUniqueness(String phone) {
        if (passengerRepo.existsByPhone(phone)) {
            throw new IllegalArgumentException(USER_WITH_THE_SAME_PHONE_IS_EXISTS_MESSAGE.formatted(phone));
        }
    }
}