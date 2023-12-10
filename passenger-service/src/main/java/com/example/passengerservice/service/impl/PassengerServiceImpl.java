package com.example.passengerservice.service.impl;

import com.example.passengerservice.amqp.handler.SendRequestHandler;
import com.example.passengerservice.amqp.message.ChangeCardUsedAsDefaultMessage;
import com.example.passengerservice.amqp.message.ChangeDefaultPaymentMethodMessage;
import com.example.passengerservice.amqp.message.NewPassengerInfoMessage;
import com.example.passengerservice.amqp.message.RemovePassengerInfoMessage;
import com.example.passengerservice.dto.request.ChangePhoneRequest;
import com.example.passengerservice.dto.request.PassengerRegistrationDto;
import com.example.passengerservice.dto.request.PassengerRequestDto;
import com.example.passengerservice.dto.response.AllPassengersResponse;
import com.example.passengerservice.dto.response.CreatePassengerResponse;
import com.example.passengerservice.dto.response.PassengerResponse;
import com.example.passengerservice.dto.response.PaymentInfoResponse;
import com.example.passengerservice.mapper.PassengerMapper;
import com.example.passengerservice.model.Passenger;
import com.example.passengerservice.model.enums.PaymentMethod;
import com.example.passengerservice.model.projections.PassengerView;
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
    private final SendRequestHandler sendRequestHandler;

    @Transactional
    @Override
    public CreatePassengerResponse signUp(PassengerRegistrationDto passengerDto) {
        checkPhoneForUniqueness(passengerDto.phone());
        val passenger = passengerMapper.toPassenger(passengerDto);
        passengerRepo.save(passenger);
        sendRequestHandler.sendNewPassengerToKafka(buildNewPassengerInfoMessage(passenger));
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
        val passenger = passengerRepo.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException(PASSENGER_NOT_FOUND_EXCEPTION_MESSAGE.formatted(externalId)));
        return passengerMapper.toPaymentInfoDto(passenger);
    }

    @Transactional(readOnly = true)
    @Override
    public AllPassengersResponse findAllPassengers(Pageable pageable) {
        Page<PassengerView> allPassengersViews = passengerRepo.findAllPassengersView(pageable);
        return buildAllPassengersResponse(allPassengersViews);
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
    public void updateDefaultPaymentMethod(ChangeDefaultPaymentMethodMessage message) {
        UUID passengerExternalId = message.passengerExternalId();
        var passenger = passengerRepo.findByExternalId(passengerExternalId)
                .orElseThrow(() -> new EntityNotFoundException(PASSENGER_NOT_FOUND_EXCEPTION_MESSAGE.formatted(passengerExternalId)));
        passenger.setDefaultPaymentMethod(PaymentMethod.CASH);
    }

    @Transactional
    @Override
    public void addCardAsDefaultPaymentMethod(UUID passengerExternalId, UUID cardExternalId) {
        var passenger = passengerRepo.findByExternalId(passengerExternalId)
                .orElseThrow(() -> new EntityNotFoundException(PASSENGER_NOT_FOUND_EXCEPTION_MESSAGE.formatted(passengerExternalId)));

        var message = ChangeCardUsedAsDefaultMessage.builder()
                .passengerExternalId(passengerExternalId)
                .cardExternalId(cardExternalId)
                .paymentMethod(PaymentMethod.CARD)
                .build();

        sendRequestHandler.sendCardUsedAsDefaultChangeRequestToKafka(message);

        passenger.setDefaultPaymentMethod(PaymentMethod.CARD);
    }

    @Transactional
    @Override
    public void addCashAsDefaultPaymentMethod(UUID externalId) {
        var passenger = passengerRepo.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException(PASSENGER_NOT_FOUND_EXCEPTION_MESSAGE.formatted(externalId)));

        var message = ChangeCardUsedAsDefaultMessage.builder()
                .passengerExternalId(externalId)
                .paymentMethod(PaymentMethod.CASH)
                .build();

        sendRequestHandler.sendCardUsedAsDefaultChangeRequestToKafka(message);

        passenger.setDefaultPaymentMethod(PaymentMethod.CASH);
    }

    @Transactional
    @Override
    public void delete(UUID externalId) {
        var passenger = passengerRepo.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException(PASSENGER_NOT_FOUND_EXCEPTION_MESSAGE.formatted(externalId)));

        sendRequestHandler.sendPassengerRemovalToKafka(buildRemovePassengerInfoMessage(passenger));

        passengerRepo.delete(passenger);
    }

    private void checkPhoneForUniqueness(String phone) {
        if (passengerRepo.existsByPhone(phone)) {
            throw new IllegalArgumentException(USER_WITH_THE_SAME_PHONE_IS_EXISTS_MESSAGE.formatted(phone));
        }
    }

    private AllPassengersResponse buildAllPassengersResponse(Page<PassengerView> allPassengersViews) {
        return AllPassengersResponse.builder()
                .passengerViewList(allPassengersViews.getContent())
                .currentPageNumber(allPassengersViews.getNumber())
                .totalPages(allPassengersViews.getTotalPages())
                .totalElements(allPassengersViews.getTotalElements())
                .build();
    }

    private NewPassengerInfoMessage buildNewPassengerInfoMessage(Passenger passenger) {
        return NewPassengerInfoMessage.builder()
                .passengerExternalId(passenger.getExternalId())
                .build();
    }

    private RemovePassengerInfoMessage buildRemovePassengerInfoMessage(Passenger passenger) {
        return RemovePassengerInfoMessage.builder()
                .passengerExternalId(passenger.getExternalId())
                .build();
    }
}