package com.example.passengerservice.service.impl;

import com.example.passengerservice.amqp.handler.SendRequestHandler;
import com.example.passengerservice.amqp.message.NewPassengerInfoMessage;
import com.example.passengerservice.dto.request.ChangePhoneRequest;
import com.example.passengerservice.dto.request.PassengerRequest;
import com.example.passengerservice.dto.response.AllPassengersResponse;
import com.example.passengerservice.dto.response.CreatePassengerResponse;
import com.example.passengerservice.dto.response.PassengerResponse;
import com.example.passengerservice.dto.response.PaymentMethodResponse;
import com.example.passengerservice.mapper.PassengerMapper;
import com.example.passengerservice.model.Passenger;
import com.example.passengerservice.model.projections.PassengerView;
import com.example.passengerservice.repository.PassengerRepository;
import com.example.passengerservice.util.DataUtil;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;

import java.util.List;
import java.util.Optional;

import static com.example.passengerservice.util.DataUtil.PAYMENT_METHOD_CARD;
import static com.example.passengerservice.util.DataUtil.PAYMENT_METHOD_CASH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
public class PassengerServiceImplTest {

    private ProjectionFactory projectionFactory = new SpelAwareProxyProjectionFactory();

    @Mock
    private PassengerMapper passengerMapper;

    @Mock
    private PassengerRepository passengerRepository;

    @Mock
    private SendRequestHandler sendRequestHandler;

    @InjectMocks
    private PassengerServiceImpl passengerService;

    @Test
    void createPassenger_shouldReturnCreatedObject() {
        NewPassengerInfoMessage message = DataUtil.defaultNewPassengerMessage();
        CreatePassengerResponse expectedResult = DataUtil.defaultCreatePassengerResponse();

        doReturn(Boolean.FALSE)
                .when(passengerRepository)
                .existsByPhone(DataUtil.PHONE);
        doReturn(DataUtil.defaultPassenger())
                .when(passengerMapper)
                .toPassenger(DataUtil.defaultPassengerRegistrationRequest());
        doNothing()
                .when(sendRequestHandler)
                .sendNewPassengerToKafka(message);
        doReturn(DataUtil.defaultCreatePassengerResponse())
                .when(passengerMapper)
                .toCreateDto(DataUtil.defaultPassenger());

        CreatePassengerResponse actual = passengerService.signUp(DataUtil.defaultPassengerRegistrationRequest());

        assertThat(actual).isEqualTo(expectedResult);

        verify(passengerMapper).toPassenger(eq(DataUtil.defaultPassengerRegistrationRequest()));
        verify(sendRequestHandler).sendNewPassengerToKafka(eq(DataUtil.defaultNewPassengerMessage()));
        verify(passengerRepository).save(DataUtil.defaultPassenger());
        verify(passengerMapper).toCreateDto(eq(DataUtil.defaultPassenger()));
    }

    @Test
    void createPassenger_shouldThrowIllegalArgumentException() {
        doReturn(Boolean.TRUE)
                .when(passengerRepository)
                .existsByPhone(DataUtil.PHONE);

        assertThrows(IllegalArgumentException.class,
                () -> passengerService.signUp(DataUtil.defaultPassengerRegistrationRequest()));

        verify(passengerRepository).existsByPhone(eq(DataUtil.PHONE));
    }

    @Test
    void findPassengerByExternalId_shouldReturnExpectedResponse() {
        PassengerResponse expected = DataUtil.defaultPassengerResponse();
        Passenger passenger = DataUtil.defaultPassenger();

        doReturn(Optional.of(passenger))
                .when(passengerRepository)
                .findByExternalId(DataUtil.PASSENGER_EXTERNAL_ID);
        doReturn(expected)
                .when(passengerMapper)
                .toDto(passenger);

        PassengerResponse actual = passengerService.findPassengerByExternalId(DataUtil.PASSENGER_EXTERNAL_ID);

        assertThat(actual).isEqualTo(expected);

        verify(passengerRepository).findByExternalId(DataUtil.PASSENGER_EXTERNAL_ID);
        verify(passengerMapper).toDto(eq(passenger));
    }

    @Test
    void findPassengerByExternalId_shouldThrowEntityNotFoundException() {
        doReturn(Optional.empty())
                .when(passengerRepository)
                .findByExternalId(DataUtil.PASSENGER_EXTERNAL_ID);

        assertThrows(EntityNotFoundException.class,
                () -> passengerService.findPassengerByExternalId(DataUtil.PASSENGER_EXTERNAL_ID));

        verify(passengerRepository).findByExternalId(DataUtil.PASSENGER_EXTERNAL_ID);
    }

    @Test
    void findPassengerPaymentInfo_shouldReturnExpectedResponse() {
        PaymentMethodResponse expected = DataUtil.defaultPaymentMethodResponseCash();
        Passenger passenger = DataUtil.defaultPassenger();

        doReturn(Optional.of(passenger))
                .when(passengerRepository)
                .findByExternalId(DataUtil.PASSENGER_EXTERNAL_ID);
        doReturn(expected)
                .when(passengerMapper)
                .toPaymentMethodDto(passenger);

        PaymentMethodResponse actual = passengerService.findPassengerPaymentMethod(DataUtil.PASSENGER_EXTERNAL_ID);

        assertThat(actual).isEqualTo(expected);

        verify(passengerRepository).findByExternalId(DataUtil.PASSENGER_EXTERNAL_ID);
        verify(passengerMapper).toPaymentMethodDto(passenger);
    }

    @Test
    void findPassengerPaymentInfo_shouldThrowEntityNotFoundException() {
        doReturn(Optional.empty())
                .when(passengerRepository)
                .findByExternalId(DataUtil.PASSENGER_EXTERNAL_ID);

        assertThrows(EntityNotFoundException.class,
                () -> passengerService.findPassengerByExternalId(DataUtil.PASSENGER_EXTERNAL_ID));

        verify(passengerRepository).findByExternalId(DataUtil.PASSENGER_EXTERNAL_ID);
    }

    @Test
    void findAllPassengers_shouldReturnExpectedResponse() {
        List<PassengerView> passengerViews = List.of(
                projectionFactory.createProjection(PassengerView.class),
                projectionFactory.createProjection(PassengerView.class),
                projectionFactory.createProjection(PassengerView.class)
        );
        Pageable pageable = Pageable.ofSize(3);
        AllPassengersResponse expected = new AllPassengersResponse(passengerViews, 0, 1, pageable.getPageSize());

        doReturn(new PageImpl<>(passengerViews))
                .when(passengerRepository)
                .findAllPassengersView(pageable);

        AllPassengersResponse actual = passengerService.findAllPassengers(pageable);

        assertThat(actual.totalElements()).isEqualTo(3);
        assertThat(actual).isEqualTo(expected);

        verify(passengerRepository).findAllPassengersView(pageable);
    }

    @Test
    void updatePassenger_shouldUpdatePassengerAndReturnExpectedResponse() {
        Passenger passenger = DataUtil.defaultPassenger();
        PassengerRequest request = DataUtil.defaultPassengerRequest();

        doReturn(Optional.of(passenger))
                .when(passengerRepository)
                .findByExternalId(DataUtil.PASSENGER_EXTERNAL_ID);

        passengerService.update(DataUtil.PASSENGER_EXTERNAL_ID, request);

        verify(passengerRepository).findByExternalId(eq(DataUtil.PASSENGER_EXTERNAL_ID));
        verify(passengerMapper).updatePassenger(eq(request), eq(passenger));
        verify(passengerMapper).toDto(eq(passenger));
    }

    @Test
    void updatePassenger_shouldThrowEntityNotFoundException() {
        PassengerRequest request = DataUtil.defaultPassengerRequest();

        doReturn(Optional.empty())
                .when(passengerRepository)
                .findByExternalId(DataUtil.PASSENGER_EXTERNAL_ID);

        assertThrows(EntityNotFoundException.class,
                () -> passengerService.update(DataUtil.PASSENGER_EXTERNAL_ID, request));

        verify(passengerRepository).findByExternalId(eq(DataUtil.PASSENGER_EXTERNAL_ID));
    }

    @Test
    void updatePassengerPhone_shouldUpdatePassengerPhone() {
        ChangePhoneRequest phoneRequest = DataUtil.defaultChangePhoneRequest();
        Passenger passenger = DataUtil.defaultPassenger();

        doReturn(Boolean.FALSE)
                .when(passengerRepository)
                .existsByPhone(DataUtil.NEW_PHONE);
        doReturn(Optional.of(passenger))
                .when(passengerRepository)
                .findByExternalId(DataUtil.PASSENGER_EXTERNAL_ID);

        passengerService.updatePassengerPhone(DataUtil.PASSENGER_EXTERNAL_ID, phoneRequest);

        verify(passengerRepository).existsByPhone(DataUtil.NEW_PHONE);
        verify(passengerRepository).findByExternalId(eq(DataUtil.PASSENGER_EXTERNAL_ID));
        verify(passengerRepository).save(passenger);
    }

    @Test
    void updatePassengerPhone_shouldThrowIllegalArgumentException() {
        doReturn(Boolean.TRUE)
                .when(passengerRepository)
                .existsByPhone(DataUtil.NEW_PHONE);

        assertThrows(IllegalArgumentException.class,
                () -> passengerService.updatePassengerPhone(DataUtil.PASSENGER_EXTERNAL_ID, DataUtil.defaultChangePhoneRequest()));

        verify(passengerRepository).existsByPhone(DataUtil.NEW_PHONE);
    }

    @Test
    void updatePassengerPhone_shouldThrowEntityNotFoundException() {
        doReturn(Optional.empty())
                .when(passengerRepository)
                .findByExternalId(DataUtil.PASSENGER_EXTERNAL_ID);

        assertThrows(EntityNotFoundException.class,
                () -> passengerService.updatePassengerPhone(DataUtil.PASSENGER_EXTERNAL_ID, DataUtil.defaultChangePhoneRequest()));

        verify(passengerRepository).findByExternalId(DataUtil.PASSENGER_EXTERNAL_ID);
    }

    @Test
    void addCardAsDefaultPaymentMethod_shouldUpdateDefaultPaymentMethod() {
        Passenger passenger = DataUtil.defaultPassenger();
        Passenger updatedPassenger = DataUtil.defaultPassengerWithCardPaymentMethod();

        doReturn(Optional.of(passenger))
                .when(passengerRepository)
                .findByExternalId(DataUtil.PASSENGER_EXTERNAL_ID);
        doReturn(updatedPassenger)
                .when(passengerRepository)
                .save(passenger);

        passengerService.addCardAsDefaultPaymentMethod(DataUtil.PASSENGER_EXTERNAL_ID, null);

        assertThat(PAYMENT_METHOD_CARD).isEqualTo(updatedPassenger.getDefaultPaymentMethod());

        verify(passengerRepository).findByExternalId(DataUtil.PASSENGER_EXTERNAL_ID);
        verify(passengerRepository).save(passenger);
    }

    @Test
    void addCardAsDefaultPaymentMethod_shouldThrowEntityNotFoundException() {
        doReturn(Optional.empty())
                .when(passengerRepository)
                .findByExternalId(DataUtil.PASSENGER_EXTERNAL_ID);

        assertThrows(EntityNotFoundException.class,
                () -> passengerService.addCardAsDefaultPaymentMethod(DataUtil.PASSENGER_EXTERNAL_ID, null));

        verify(passengerRepository).findByExternalId(DataUtil.PASSENGER_EXTERNAL_ID);
    }

    @Test
    void addCashAsDefaultPaymentMethod_shouldUpdateDefaultPaymentMethod() {
        Passenger passenger = DataUtil.defaultPassengerWithCardPaymentMethod();
        Passenger updatedPassenger = DataUtil.defaultPassenger();

        doReturn(Optional.of(passenger))
                .when(passengerRepository)
                .findByExternalId(DataUtil.PASSENGER_EXTERNAL_ID);
        doReturn(updatedPassenger)
                .when(passengerRepository)
                .save(passenger);

        passengerService.addCashAsDefaultPaymentMethod(DataUtil.PASSENGER_EXTERNAL_ID);

        assertThat(PAYMENT_METHOD_CASH).isEqualTo(updatedPassenger.getDefaultPaymentMethod());

        verify(passengerRepository).findByExternalId(DataUtil.PASSENGER_EXTERNAL_ID);
        verify(passengerRepository).save(passenger);
    }

    @Test
    void addCashAsDefaultPaymentMethod_shouldThrowEntityNotFoundException() {
        doReturn(Optional.empty())
                .when(passengerRepository)
                .findByExternalId(DataUtil.PASSENGER_EXTERNAL_ID);

        assertThrows(EntityNotFoundException.class,
                () -> passengerService.addCashAsDefaultPaymentMethod(DataUtil.PASSENGER_EXTERNAL_ID));

        verify(passengerRepository).findByExternalId(DataUtil.PASSENGER_EXTERNAL_ID);
    }

    @Test
    void deletePassenger_shouldDeletePassenger() {
        Passenger passenger = DataUtil.defaultPassenger();

        doReturn(Optional.of(passenger))
                .when(passengerRepository)
                .findByExternalId(DataUtil.PASSENGER_EXTERNAL_ID);
        doNothing()
                .when(passengerRepository)
                .delete(passenger);

        passengerService.delete(DataUtil.PASSENGER_EXTERNAL_ID);

        verify(passengerRepository).findByExternalId(DataUtil.PASSENGER_EXTERNAL_ID);
        verify(passengerRepository).delete(eq(passenger));
    }

    @Test
    void deletePassenger_shouldThrowEntityNotFoundException() {
        doReturn(Optional.empty())
                .when(passengerRepository)
                .findByExternalId(DataUtil.PASSENGER_EXTERNAL_ID);

        assertThrows(EntityNotFoundException.class,
                () -> passengerService.delete(DataUtil.PASSENGER_EXTERNAL_ID));

        verify(passengerRepository).findByExternalId(DataUtil.PASSENGER_EXTERNAL_ID);
    }
}