package com.example.ridesservice.component.step;

import com.example.ridesservice.amqp.handler.SendRequestHandler;
import com.example.ridesservice.amqp.message.RideInfoMessage;
import com.example.ridesservice.dto.request.CreateRideRequest;
import com.example.ridesservice.dto.response.ride.AcceptRideResponse;
import com.example.ridesservice.dto.response.ride.CreateRideResponse;
import com.example.ridesservice.dto.response.ride.GetRideResponse;
import com.example.ridesservice.exception.PassengerRideNotFoundException;
import com.example.ridesservice.feign.client.CardClient;
import com.example.ridesservice.feign.client.PassengerClient;
import com.example.ridesservice.mapper.RideMapper;
import com.example.ridesservice.model.DriverInfo;
import com.example.ridesservice.model.Ride;
import com.example.ridesservice.model.enums.RideStatus;
import com.example.ridesservice.repository.DriverInfoRepository;
import com.example.ridesservice.repository.RideRepository;
import com.example.ridesservice.service.RideService;
import com.example.ridesservice.service.impl.RideServiceImpl;
import com.example.ridesservice.util.DataUtil;
import com.example.ridesservice.util.FakeRideCostGenerator;
import com.example.ridesservice.util.RideVerifier;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.Optional;
import java.util.UUID;

import static com.example.ridesservice.util.ExceptionMessagesConstants.PASSENGER_RIDE_NOT_FOUND_EXCEPTION_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.doNothing;

public class RidesServiceStepDefinitions {

    private RideRepository rideRepo;
    private DriverInfoRepository driverInfoRepo;
    private RideMapper rideMapper;
    private SendRequestHandler sendRequestHandler;
    private PassengerClient passengerClient;
    private CardClient cardClient;
    private FakeRideCostGenerator rideCostGenerator;
    private RideVerifier rideVerifier;
    private RideService rideService;

    private GetRideResponse getRideResponse;
    private CreateRideResponse createRideResponse;
    private AcceptRideResponse acceptRideResponse;
    private Exception exception;

    @Before
    public void setUp() {
        this.rideRepo = mock(RideRepository.class);
        this.driverInfoRepo = mock(DriverInfoRepository.class);
        this.rideMapper = mock(RideMapper.class);
        this.sendRequestHandler = mock(SendRequestHandler.class);
        this.passengerClient = mock(PassengerClient.class);
        this.cardClient = mock(CardClient.class);
        this.rideCostGenerator = mock(FakeRideCostGenerator.class);
        this.rideVerifier = mock(RideVerifier.class);
        this.rideService = new RideServiceImpl(rideRepo, driverInfoRepo, rideMapper, sendRequestHandler, passengerClient, cardClient, rideCostGenerator, rideVerifier);
    }

    @Given("An external ride identifier: {uuid}, that exist and passengerExternalId: {uuid}, that exist")
    public void rideWithExternalIdExistsAndPassengerExternalIdExist(UUID rideExternalId, UUID passengerExternalId) {
        Ride ride = DataUtil.defaultFinishedRideCash();
        GetRideResponse expected = DataUtil.defaultGetRideResponse();

        doReturn(Optional.of(ride))
                .when(rideRepo)
                .findByExternalIdAndPassengerExternalId(rideExternalId, passengerExternalId);
        doReturn(expected)
                .when(rideMapper)
                .toGetRideDto(ride);

        assertThat(rideRepo.findByExternalIdAndPassengerExternalId(rideExternalId, passengerExternalId)).isPresent();
    }

    @Given("An external ride identifier: {uuid}, that doesn't exist and passengerExternalId: {uuid}, that exist")
    public void rideWithExternalIdNotExistsAndPassengerExternalIdExist(UUID rideExternalId, UUID passengerExternalId) {
        doReturn(Optional.empty())
                .when(rideRepo)
                .findByExternalIdAndPassengerExternalId(rideExternalId, passengerExternalId);

        assertThat(rideRepo.findByExternalIdAndPassengerExternalId(rideExternalId, passengerExternalId)).isEmpty();
    }

    @When("A rideExternalId: {uuid} and passengerExternalId: {uuid}, is passed to the findRideMethod")
    public void rideAndPassengerExternalIdsPassedToFindRideMethod(UUID rideExternalId, UUID passengerExternalId) {
        try {
            getRideResponse = rideService.findRideByPassengerExternalId(passengerExternalId, rideExternalId);
        } catch (PassengerRideNotFoundException e) {
            exception = e;
        }
    }

    @Then("The response should contain details of the ride with rideExternalId: {uuid}, and passengerExternalId: {uuid}")
    public void responseContainsRideDetails(UUID rideExternalId, UUID passengerExternalId) {
        var ride = rideRepo.findByExternalIdAndPassengerExternalId(rideExternalId, passengerExternalId).get();
        var expected = rideMapper.toGetRideDto(ride);

        assertThat(getRideResponse).isEqualTo(expected);
    }

    @Then("The PassengerRideNotFoundException with the message containing rideExternalId: {uuid}, and passengerExternalId: {uuid}, should be thrown")
    public void rideNotFoundExceptionThrown(UUID rideExternalId, UUID passengerExternalId) {
        var expected = String.format(PASSENGER_RIDE_NOT_FOUND_EXCEPTION_MESSAGE, rideExternalId, passengerExternalId);
        var actual = exception.getMessage();

        assertThat(actual).isEqualTo(expected);
    }

    @Given("An external passenger identifier: {uuid}, that exist, pickUpAddress: {string}, destinationAddress: {string}")
    public void passengerWithExternalIdExist(UUID passengerExternalId, String pickUpAddress, String destinationAddress) {
        CreateRideRequest request = new CreateRideRequest(pickUpAddress, destinationAddress);
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
    }

    @When("A create request with pickUpAddress: {string}, destinationAddress: {string}, and passengerExternalId: {uuid}, passed to bookRide method")
    public void createRequestAndPassengerExtIdPassedToBookRideMethod(String pickUpAddress, String destinationAddress, UUID passengerExternalId) {
        CreateRideRequest request = new CreateRideRequest(pickUpAddress, destinationAddress);

        createRideResponse = rideService.bookRide(passengerExternalId, request);
    }

    @Then("The response should contain details of the created ride with pickUpAddress: {string}, destinationAddress: {string}, passengerExternalId: {uuid}")
    public void responseContainsCreatedRideDetails(String pickUpAddress, String destinationAddress, UUID passengerExternalId) {
        CreateRideRequest request = new CreateRideRequest(pickUpAddress, destinationAddress);
        var ride = rideMapper.toRide(request, passengerExternalId, DataUtil.RIDE_COST);
        var expected = rideMapper.toCreateRideDto(ride);

        assertThat(createRideResponse).isEqualTo(expected);
    }

    @Given("Driver with externalId: {uuid} assigned to a ride with externalId: {uuid}")
    public void driverAssignedToRide(UUID driverExternalId, UUID rideExternalId) {
        Ride ride = DataUtil.defaultInitiatedRide();
        DriverInfo driverInfo = DataUtil.defaultAvailableDriver();
        AcceptRideResponse expected = DataUtil.defaultAcceptRideResponse();

        doReturn(Optional.of(ride))
                .when(rideRepo)
                .findByExternalId(rideExternalId);
        doNothing()
                .when(rideVerifier)
                .verifyAcceptPossibility(ride);
        doReturn(Optional.of(driverInfo))
                .when(driverInfoRepo)
                .findByExternalId(driverExternalId);
        doNothing()
                .when(rideMapper)
                .updateRideOnAcceptance(driverInfo, ride);
        doReturn(expected)
                .when(rideMapper)
                .toAcceptRideDto(ride);
    }

    @When("An external driver identifier: {uuid} and rideExternalId: {uuid} is passed to acceptRideMethod")
    public void driverExtIdAndRideExtIdPassedToAcceptRideMethod(UUID driverExternalId, UUID rideExternalId) {
        acceptRideResponse = rideService.acceptRide(driverExternalId, rideExternalId);
    }

    @Then("Ride status should be changed to {string}")
    public void rideStatusChangeTo(String status) {
        assertThat(acceptRideResponse.rideStatus()).isEqualTo(RideStatus.valueOf(status));
    }
}