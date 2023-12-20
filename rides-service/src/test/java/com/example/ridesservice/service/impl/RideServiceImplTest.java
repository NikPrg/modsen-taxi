package com.example.ridesservice.service.impl;

import com.example.ridesservice.amqp.handler.SendRequestHandler;
import com.example.ridesservice.amqp.message.DriverStatusMessage;
import com.example.ridesservice.amqp.message.RideInfoMessage;
import com.example.ridesservice.dto.request.CreateRideRequest;
import com.example.ridesservice.dto.response.ride.*;
import com.example.ridesservice.exception.*;
import com.example.ridesservice.feign.client.CardClient;
import com.example.ridesservice.feign.client.PassengerClient;
import com.example.ridesservice.feign.response.PaymentMethodResponse;
import com.example.ridesservice.mapper.RideMapper;
import com.example.ridesservice.model.DriverInfo;
import com.example.ridesservice.model.Ride;
import com.example.ridesservice.model.projection.RideView;
import com.example.ridesservice.repository.DriverInfoRepository;
import com.example.ridesservice.repository.RideRepository;
import com.example.ridesservice.util.DataUtil;
import com.example.ridesservice.util.FakeRideCostGenerator;
import com.example.ridesservice.util.RideVerifier;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class RideServiceImplTest {
    private final ProjectionFactory projectionFactory = new SpelAwareProxyProjectionFactory();
    @Mock
    private RideRepository rideRepository;
    @Mock
    private DriverInfoRepository driverInfoRepository;
    @Mock
    private RideMapper rideMapper;
    @Mock
    private SendRequestHandler sendRequestHandler;
    @Mock
    private PassengerClient passengerClient;
    @Mock
    private CardClient cardClient;
    @Mock
    private RideVerifier rideVerifier;
    @Mock
    private FakeRideCostGenerator rideCostGenerator;
    @InjectMocks
    private RideServiceImpl rideService;

    @Test
    void findRideByPassengerExternalId_shouldReturnExpectedResponse() {
        UUID rideExternalId = DataUtil.RIDE_EXTERNAL_ID;
        UUID passengerExternalId = DataUtil.PASSENGER_EXTERNAL_ID;
        Ride ride = DataUtil.defaultFinishedRideCash();
        GetRideResponse expected = DataUtil.defaultGetRideResponse();

        doReturn(Optional.of(ride))
                .when(rideRepository)
                .findByExternalIdAndPassengerExternalId(rideExternalId, passengerExternalId);
        doReturn(expected)
                .when(rideMapper)
                .toGetRideDto(ride);

        GetRideResponse actual = rideService.findRideByPassengerExternalId(passengerExternalId, rideExternalId);

        assertThat(actual).isEqualTo(expected);

        verify(rideRepository).findByExternalIdAndPassengerExternalId(eq(rideExternalId), eq(passengerExternalId));
        verify(rideMapper).toGetRideDto(eq(ride));
    }

    @Test
    void findRideByPassengerExternalId_shouldThrowPassengerRideNotFoundException() {
        UUID rideExternalId = DataUtil.RIDE_EXTERNAL_ID;
        UUID passengerExternalId = DataUtil.PASSENGER_EXTERNAL_ID;

        doReturn(Optional.empty())
                .when(rideRepository)
                .findByExternalIdAndPassengerExternalId(rideExternalId, passengerExternalId);

        assertThrows(PassengerRideNotFoundException.class,
                () -> rideService.findRideByPassengerExternalId(passengerExternalId, rideExternalId));

        verify(rideRepository).findByExternalIdAndPassengerExternalId(eq(rideExternalId), eq(passengerExternalId));
    }

    @Test
    void findAllPassengerRides_shouldReturnExpectedResponse() {
        List<RideView> rideProjections = List.of(
                projectionFactory.createProjection(RideView.class),
                projectionFactory.createProjection(RideView.class),
                projectionFactory.createProjection(RideView.class)
        );
        UUID passengerExternalId = DataUtil.PASSENGER_EXTERNAL_ID;
        Pageable pageable = Pageable.ofSize(3);
        AllRidesResponse expected = new AllRidesResponse(rideProjections, 0, 1, pageable.getPageSize());
        PageImpl<RideView> rideViews = new PageImpl<>(rideProjections);

        doReturn(rideViews)
                .when(rideRepository)
                .findAllPassengerRideViews(passengerExternalId, pageable);

        AllRidesResponse actual = rideService.findAllPassengerRides(passengerExternalId, pageable);

        assertThat(actual.totalElements()).isEqualTo(3);
        assertThat(actual).isEqualTo(expected);

        verify(rideRepository).findAllPassengerRideViews(eq(passengerExternalId), eq(pageable));
    }

    @Test
    void findAllPassengerRides_shouldReturnEmptyResponse() {
        List<RideView> rideProjections = Collections.emptyList();
        UUID passengerExternalId = DataUtil.PASSENGER_EXTERNAL_ID;
        PageImpl<RideView> rideViews = new PageImpl<>(rideProjections);
        AllRidesResponse emptyResponse = new AllRidesResponse(rideProjections, 0, 1, 0);
        Pageable pageable = Pageable.unpaged();

        doReturn(rideViews)
                .when(rideRepository)
                .findAllPassengerRideViews(passengerExternalId, pageable);

        AllRidesResponse actual = rideService.findAllPassengerRides(passengerExternalId, pageable);

        assertThat(actual.rideViewList()).isEmpty();
        assertThat(actual.totalElements()).isEqualTo(0);
        assertThat(actual).isEqualTo(emptyResponse);

        verify(rideRepository).findAllPassengerRideViews(eq(passengerExternalId), eq(pageable));
    }

    @Test
    void bookRide_shouldReturnExpectedResponse() {
        UUID passengerExternalId = DataUtil.PASSENGER_EXTERNAL_ID;
        CreateRideRequest request = DataUtil.defaultCreateRideRequest();
        Ride ride = DataUtil.defaultInitiatedRide();
        RideInfoMessage message = DataUtil.defaultRideInfoMessage();
        CreateRideResponse expected = DataUtil.defaultCreateRideResponse();

        doReturn(DataUtil.RIDE_COST)
                .when(rideCostGenerator)
                .calculateRideCost(request);
        doReturn(ride)
                .when(rideMapper)
                .toRide(request, passengerExternalId, DataUtil.RIDE_COST);
        doNothing()
                .when(sendRequestHandler)
                .sendRideInfoRequestToKafka(message);
        doReturn(expected)
                .when(rideMapper)
                .toCreateRideDto(ride);

        CreateRideResponse actual = rideService.bookRide(passengerExternalId, request);

        assertThat(actual).isEqualTo(expected);
        assertThat(actual.rideStatus()).isEqualTo(DataUtil.RIDE_STATUS_INITIATED);

        verify(rideCostGenerator).calculateRideCost(eq(request));
        verify(rideMapper).toRide(eq(request), eq(passengerExternalId), eq(DataUtil.RIDE_COST));
        verify(sendRequestHandler).sendRideInfoRequestToKafka(eq(message));
        verify(rideRepository).save(eq(ride));
        verify(rideMapper).toCreateRideDto(eq(ride));
    }

    @Test
    void acceptRide_shouldReturnExpectedResponse() {
        UUID rideExternalId = DataUtil.RIDE_EXTERNAL_ID;
        UUID driverInfoExternalId = DataUtil.DRIVER_INFO_EXTERNAL_ID;
        Ride ride = DataUtil.defaultInitiatedRide();
        DriverInfo driverInfo = DataUtil.defaultAvailableDriver();
        AcceptRideResponse expected = DataUtil.defaultAcceptRideResponse();

        doReturn(Optional.of(ride))
                .when(rideRepository)
                .findByExternalId(rideExternalId);
        doNothing()
                .when(rideVerifier)
                .verifyAcceptPossibility(ride);
        doReturn(Optional.of(driverInfo))
                .when(driverInfoRepository)
                .findByExternalId(driverInfoExternalId);
        doNothing()
                .when(rideMapper)
                .updateRideOnAcceptance(driverInfo, ride);
        doReturn(expected)
                .when(rideMapper)
                .toAcceptRideDto(ride);

        AcceptRideResponse actual = rideService.acceptRide(driverInfoExternalId, rideExternalId);

        assertThat(actual).isEqualTo(expected);
        assertThat(actual.rideStatus()).isEqualTo(DataUtil.RIDE_STATUS_ACCEPTED);

        verify(rideRepository).findByExternalId(eq(rideExternalId));
        verify(rideVerifier).verifyAcceptPossibility(eq(ride));
        verify(driverInfoRepository).findByExternalId(eq(driverInfoExternalId));
        verify(rideMapper).updateRideOnAcceptance(eq(driverInfo), eq(ride));
        verify(rideMapper).toAcceptRideDto(eq(ride));
    }

    @Test
    void acceptRide_shouldThrowEntityNotFoundException() {
        UUID rideExternalId = DataUtil.RIDE_EXTERNAL_ID;
        UUID driverInfoExternalId = DataUtil.DRIVER_INFO_EXTERNAL_ID;

        doReturn(Optional.empty())
                .when(rideRepository)
                .findByExternalId(rideExternalId);

        assertThrows(EntityNotFoundException.class,
                () -> rideService.acceptRide(driverInfoExternalId, rideExternalId));

        verify(rideRepository).findByExternalId(eq(rideExternalId));
    }

    @Test
    void acceptRide_shouldThrowDriverNotBelongRideException() {
        UUID rideExternalId = DataUtil.RIDE_EXTERNAL_ID;
        UUID driverInfoExternalId = DataUtil.DRIVER_INFO_EXTERNAL_ID;
        Ride ride = DataUtil.defaultAcceptedRide();

        doReturn(Optional.of(ride))
                .when(rideRepository)
                .findByExternalId(rideExternalId);
        doThrow(DriverNotBelongRideException.class)
                .when(rideVerifier)
                .verifyAcceptPossibility(ride);

        assertThrows(DriverNotBelongRideException.class,
                () -> rideService.acceptRide(driverInfoExternalId, rideExternalId));

        verify(rideRepository).findByExternalId(eq(rideExternalId));
        verify(rideVerifier).verifyAcceptPossibility(eq(ride));
    }

    @Test
    void acceptRide_shouldThrowDriverAlreadyInUseException() {
        UUID rideExternalId = DataUtil.RIDE_EXTERNAL_ID;
        UUID driverInfoExternalId = DataUtil.DRIVER_INFO_EXTERNAL_ID;
        Ride ride = DataUtil.defaultInitiatedRide();
        DriverInfo driverInfo = DataUtil.defaultUnavailableDriver();

        doReturn(Optional.of(ride))
                .when(rideRepository)
                .findByExternalId(rideExternalId);
        doNothing()
                .when(rideVerifier)
                .verifyAcceptPossibility(ride);
        doReturn(Optional.of(driverInfo))
                .when(driverInfoRepository)
                .findByExternalId(driverInfoExternalId);

        assertThrows(DriverAlreadyInUseException.class,
                () -> rideService.acceptRide(driverInfoExternalId, rideExternalId));

        verify(rideRepository).findByExternalId(eq(rideExternalId));
        verify(rideVerifier).verifyAcceptPossibility(eq(ride));
        verify(driverInfoRepository).findByExternalId(eq(driverInfoExternalId));
    }

    @Test
    void startRide_shouldReturnExpectedResponse() {
        UUID rideExternalId = DataUtil.RIDE_EXTERNAL_ID;
        UUID driverInfoExternalId = DataUtil.DRIVER_INFO_EXTERNAL_ID;
        Ride ride = DataUtil.defaultAcceptedRide();
        DriverStatusMessage message = DataUtil.defaultDriverStatusMessageUnavailable();
        StartRideResponse expected = DataUtil.defaultStartRideResponse();

        doReturn(Optional.of(ride))
                .when(rideRepository)
                .findByExternalId(rideExternalId);
        doNothing()
                .when(rideVerifier)
                .verifyStartPossibility(ride, driverInfoExternalId);
        doNothing()
                .when(sendRequestHandler)
                .sendDriverStatusRequestToKafka(message);
        doReturn(expected)
                .when(rideMapper)
                .toStartRideDto(ride);

        StartRideResponse actual = rideService.startRide(driverInfoExternalId, rideExternalId);

        assertThat(actual).isEqualTo(expected);

        verify(rideRepository).findByExternalId(eq(rideExternalId));
        verify(rideVerifier).verifyStartPossibility(eq(ride), eq(driverInfoExternalId));
        verify(sendRequestHandler).sendDriverStatusRequestToKafka(eq(message));
        verify(rideMapper).toStartRideDto(eq(ride));
    }

    @Test
    void startRide_shouldThrowEntityNotFoundException() {
        UUID rideExternalId = DataUtil.RIDE_EXTERNAL_ID;
        UUID driverInfoExternalId = DataUtil.DRIVER_INFO_EXTERNAL_ID;

        doReturn(Optional.empty())
                .when(rideRepository)
                .findByExternalId(rideExternalId);

        assertThrows(EntityNotFoundException.class,
                () -> rideService.startRide(driverInfoExternalId, rideExternalId));

        verify(rideRepository).findByExternalId(eq(rideExternalId));
    }

    @Test
    void startRide_shouldThrowRideNotAcceptedException() {
        UUID rideExternalId = DataUtil.RIDE_EXTERNAL_ID;
        UUID driverInfoExternalId = DataUtil.DRIVER_INFO_EXTERNAL_ID;
        Ride ride = DataUtil.defaultInitiatedRide();

        doReturn(Optional.of(ride))
                .when(rideRepository)
                .findByExternalId(rideExternalId);
        doThrow(RideNotAcceptedException.class)
                .when(rideVerifier)
                .verifyStartPossibility(ride, driverInfoExternalId);

        assertThrows(RideNotAcceptedException.class,
                () -> rideService.startRide(driverInfoExternalId, rideExternalId));

        verify(rideRepository).findByExternalId(eq(rideExternalId));
        verify(rideVerifier).verifyStartPossibility(eq(ride), eq(driverInfoExternalId));
    }

    @Test
    void startRide_shouldThrowDriverNotBelongRideException() {
        UUID rideExternalId = DataUtil.RIDE_EXTERNAL_ID;
        UUID newDriverInfoExternalId = UUID.randomUUID();
        Ride ride = DataUtil.defaultAcceptedRide();

        doReturn(Optional.of(ride))
                .when(rideRepository)
                .findByExternalId(rideExternalId);
        doThrow(DriverNotBelongRideException.class)
                .when(rideVerifier)
                .verifyStartPossibility(ride, newDriverInfoExternalId);

        assertThrows(DriverNotBelongRideException.class,
                () -> rideService.startRide(newDriverInfoExternalId, rideExternalId));

        verify(rideRepository).findByExternalId(eq(rideExternalId));
        verify(rideVerifier).verifyStartPossibility(eq(ride), eq(newDriverInfoExternalId));
    }

    @Test
    void startRide_shouldThrowRideAlreadyStartedException() {
        UUID rideExternalId = DataUtil.RIDE_EXTERNAL_ID;
        UUID driverInfoExternalId = DataUtil.DRIVER_INFO_EXTERNAL_ID;
        Ride ride = DataUtil.defaultStartedRide();

        doReturn(Optional.of(ride))
                .when(rideRepository)
                .findByExternalId(rideExternalId);
        doThrow(RideAlreadyStartedException.class)
                .when(rideVerifier)
                .verifyStartPossibility(ride, driverInfoExternalId);

        assertThrows(RideAlreadyStartedException.class,
                () -> rideService.startRide(driverInfoExternalId, rideExternalId));

        verify(rideRepository).findByExternalId(eq(rideExternalId));
        verify(rideVerifier).verifyStartPossibility(eq(ride), eq(driverInfoExternalId));
    }

    @Test
    void startRide_shouldThrowRideAlreadyFinishedException() {
        UUID rideExternalId = DataUtil.RIDE_EXTERNAL_ID;
        UUID driverInfoExternalId = DataUtil.DRIVER_INFO_EXTERNAL_ID;
        Ride ride = DataUtil.defaultFinishedRideCash();

        doReturn(Optional.of(ride))
                .when(rideRepository)
                .findByExternalId(rideExternalId);
        doThrow(RideAlreadyFinishedException.class)
                .when(rideVerifier)
                .verifyStartPossibility(ride, driverInfoExternalId);

        assertThrows(RideAlreadyFinishedException.class,
                () -> rideService.startRide(driverInfoExternalId, rideExternalId));

        verify(rideRepository).findByExternalId(eq(rideExternalId));
        verify(rideVerifier).verifyStartPossibility(eq(ride), eq(driverInfoExternalId));
    }

    @Test
    void finishRide_shouldReturnExpectedResponse() {
        UUID rideExternalId = DataUtil.RIDE_EXTERNAL_ID;
        UUID driverInfoExternalId = DataUtil.DRIVER_INFO_EXTERNAL_ID;
        UUID passengerExternalId = DataUtil.PASSENGER_EXTERNAL_ID;
        PaymentMethodResponse ridePaymentMethodResponse = DataUtil.defaultPaymentMethodResponseCash();
        DriverStatusMessage message = DataUtil.defaultDriverStatusMessageAvailable();
        Ride ride = DataUtil.defaultFinishedRideCash();
        FinishRideResponse expected = DataUtil.defaultFinishRideResponseCash();

        doReturn(Optional.of(ride))
                .when(rideRepository)
                .findByExternalId(rideExternalId);
        doNothing()
                .when(rideVerifier)
                .verifyFinishPossibility(ride, driverInfoExternalId);
        doReturn(ridePaymentMethodResponse)
                .when(passengerClient)
                .findPassengerPaymentMethod(passengerExternalId);
        doNothing()
                .when(sendRequestHandler)
                .sendDriverStatusRequestToKafka(message);
        doReturn(expected)
                .when(rideMapper)
                .toFinishRideResponse(ride);

        FinishRideResponse actual = rideService.finishRide(driverInfoExternalId, rideExternalId);

        assertThat(actual).isEqualTo(expected);
        assertThat(actual.rideStatus()).isEqualTo(DataUtil.RIDE_STATUS_FINISHED);

        verify(rideRepository).findByExternalId(eq(rideExternalId));
        verify(rideVerifier).verifyFinishPossibility(eq(ride), eq(driverInfoExternalId));
        verify(passengerClient).findPassengerPaymentMethod(eq(passengerExternalId));
        verify(sendRequestHandler).sendDriverStatusRequestToKafka(eq(message));
        verify(rideMapper).toFinishRideResponse(eq(ride));
    }

    @Test
    void finishRide_shouldThrowRideNotAcceptedException() {
        UUID rideExternalId = DataUtil.RIDE_EXTERNAL_ID;
        UUID driverInfoExternalId = DataUtil.DRIVER_INFO_EXTERNAL_ID;
        Ride ride = DataUtil.defaultInitiatedRide();

        doReturn(Optional.of(ride))
                .when(rideRepository)
                .findByExternalId(rideExternalId);
        doThrow(RideNotAcceptedException.class)
                .when(rideVerifier)
                .verifyFinishPossibility(ride, driverInfoExternalId);

        assertThrows(RideNotAcceptedException.class,
                () -> rideService.finishRide(driverInfoExternalId, rideExternalId));

        verify(rideRepository).findByExternalId(eq(rideExternalId));
        verify(rideVerifier).verifyFinishPossibility(eq(ride), eq(driverInfoExternalId));
    }

    @Test
    void finishRide_shouldThrowDriverNotBelongRideException() {
        UUID rideExternalId = DataUtil.RIDE_EXTERNAL_ID;
        UUID newDriverInfoExternalId = UUID.randomUUID();
        Ride ride = DataUtil.defaultAcceptedRide();

        doReturn(Optional.of(ride))
                .when(rideRepository)
                .findByExternalId(rideExternalId);
        doThrow(DriverNotBelongRideException.class)
                .when(rideVerifier)
                .verifyFinishPossibility(ride, newDriverInfoExternalId);

        assertThrows(DriverNotBelongRideException.class,
                () -> rideService.finishRide(newDriverInfoExternalId, rideExternalId));

        verify(rideRepository).findByExternalId(eq(rideExternalId));
        verify(rideVerifier).verifyFinishPossibility(eq(ride), eq(newDriverInfoExternalId));
    }

    @Test
    void finishRide_shouldThrowRideNotStartedException() {
        UUID rideExternalId = DataUtil.RIDE_EXTERNAL_ID;
        UUID driverInfoExternalId = DataUtil.DRIVER_INFO_EXTERNAL_ID;
        Ride ride = DataUtil.defaultAcceptedRide();

        doReturn(Optional.of(ride))
                .when(rideRepository)
                .findByExternalId(rideExternalId);
        doThrow(RideNotStartedException.class)
                .when(rideVerifier)
                .verifyFinishPossibility(ride, driverInfoExternalId);

        assertThrows(RideNotStartedException.class,
                () -> rideService.finishRide(driverInfoExternalId, rideExternalId));

        verify(rideRepository).findByExternalId(eq(rideExternalId));
        verify(rideVerifier).verifyFinishPossibility(eq(ride), eq(driverInfoExternalId));
    }

    @Test
    void finishRide_shouldThrowRideAlreadyFinishedException() {
        UUID rideExternalId = DataUtil.RIDE_EXTERNAL_ID;
        UUID driverInfoExternalId = DataUtil.DRIVER_INFO_EXTERNAL_ID;
        Ride ride = DataUtil.defaultFinishedRideCash();

        doReturn(Optional.of(ride))
                .when(rideRepository)
                .findByExternalId(rideExternalId);
        doThrow(RideAlreadyFinishedException.class)
                .when(rideVerifier)
                .verifyFinishPossibility(ride, driverInfoExternalId);

        assertThrows(RideAlreadyFinishedException.class,
                () -> rideService.finishRide(driverInfoExternalId, rideExternalId));

        verify(rideRepository).findByExternalId(eq(rideExternalId));
        verify(rideVerifier).verifyFinishPossibility(eq(ride), eq(driverInfoExternalId));
    }

}
