package com.example.passengerservice.component.step;

import com.example.passengerservice.amqp.handler.SendRequestHandler;
import com.example.passengerservice.amqp.message.NewPassengerInfoMessage;
import com.example.passengerservice.dto.request.PassengerRegistrationRequest;
import com.example.passengerservice.dto.response.CreatePassengerResponse;
import com.example.passengerservice.dto.response.PassengerResponse;
import com.example.passengerservice.dto.response.PaymentMethodResponse;
import com.example.passengerservice.mapper.PassengerMapper;
import com.example.passengerservice.model.Passenger;
import com.example.passengerservice.repository.PassengerRepository;
import com.example.passengerservice.service.PassengerService;
import com.example.passengerservice.service.impl.PassengerServiceImpl;
import com.example.passengerservice.util.DataUtil;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.persistence.EntityNotFoundException;

import java.util.Optional;
import java.util.UUID;

import static com.example.passengerservice.util.ExceptionMessagesConstants.PASSENGER_NOT_FOUND_EXCEPTION_MESSAGE;
import static com.example.passengerservice.util.ExceptionMessagesConstants.USER_WITH_THE_SAME_PHONE_IS_EXISTS_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.doNothing;

public class PassengerServiceStepDefinitions {
    private PassengerMapper passengerMapper;
    private PassengerRepository passengerRepo;
    private SendRequestHandler sendRequestHandler;
    private PassengerService passengerService;

    private PassengerResponse passengerResponse;
    private CreatePassengerResponse createPassengerResponse;
    private PaymentMethodResponse paymentMethodResponse;
    private Exception exception;

    @Before
    public void setUp() {
        this.passengerMapper = mock(PassengerMapper.class);
        this.passengerRepo = mock(PassengerRepository.class);
        this.sendRequestHandler = mock(SendRequestHandler.class);
        this.passengerService = new PassengerServiceImpl(passengerMapper, passengerRepo, sendRequestHandler);
    }

    @Given("An external passenger identifier: {uuid}, that exist")
    public void passengerWithExternalIdExist(UUID passengerExternalId) {
        PassengerResponse expected = DataUtil.defaultPassengerResponse();
        Passenger passenger = DataUtil.defaultPassenger();

        doReturn(Optional.of(passenger))
                .when(passengerRepo)
                .findByExternalId(passengerExternalId);
        doReturn(expected)
                .when(passengerMapper)
                .toDto(passenger);

        assertThat(passengerRepo.findByExternalId(passengerExternalId)).isPresent();
    }

    @When("An external passenger identifier: {uuid}, is passed to the findPassengerByExternalId method")
    public void externalIdPassedToFindPassengerByExternalIdMethod(UUID passengerExternalId) {
        try {
            passengerResponse = passengerService.findPassengerByExternalId(passengerExternalId);
        } catch (EntityNotFoundException e) {
            exception = e;
        }
    }

    @Then("The response should contain details of the passenger with externalId: {uuid}")
    public void responseContainsPassengerDetails(UUID passengerExternalId) {
        var passenger = passengerRepo.findByExternalId(passengerExternalId).get();
        var expected = passengerMapper.toDto(passenger);

        assertThat(passengerResponse).isEqualTo(expected);
    }

    @Given("An external passenger identifier: {uuid}, that doesn't exist")
    public void passengerWithExternalIdNotExist(UUID passengerExternalId) {
        var actual = passengerRepo.findByExternalId(passengerExternalId);
        assertThat(actual).isEmpty();
    }

    @Then("The EntityNotFoundException with the message containing passengerExternalId: {uuid}, should be thrown")
    public void passengerNotFoundExceptionThrown(UUID passengerExternalId) {
        var expected = String.format(PASSENGER_NOT_FOUND_EXCEPTION_MESSAGE, passengerExternalId);
        var actual = exception.getMessage();

        assertThat(actual).isEqualTo(expected);
    }

    @Given("A passenger with phone: {string}, firstName: {string} and lastName: {string}, doesn't exist")
    public void passengerWithPhoneNotExist(String phone, String firstName, String lastName) {
        PassengerRegistrationRequest request = new PassengerRegistrationRequest(phone, firstName, lastName);
        Passenger passenger = DataUtil.defaultPassenger();
        NewPassengerInfoMessage message = DataUtil.defaultNewPassengerMessage();
        CreatePassengerResponse createPassengerResponse = DataUtil.defaultCreatePassengerResponse();

        doReturn(Boolean.FALSE)
                .when(passengerRepo)
                .existsByPhone(phone);
        doReturn(passenger)
                .when(passengerMapper)
                .toPassenger(request);
        doNothing()
                .when(sendRequestHandler)
                .sendNewPassengerToKafka(message);
        doReturn(createPassengerResponse)
                .when(passengerMapper)
                .toCreateDto(passenger);

        assertThat(passengerRepo.existsByPhone(phone)).isFalse();
    }

    @When("A create request with firstName: {string}, lastName: {string}, phone: {string} is passed to the signUp method")
    public void createRequestPassedToAddDriverMethod(String firstName, String lastName, String phone) {
        var createRequest = PassengerRegistrationRequest.builder()
                .firstName(firstName)
                .lastName(lastName)
                .phone(phone)
                .build();
        try {
            createPassengerResponse = passengerService.signUp(createRequest);
        } catch (IllegalArgumentException e) {
            exception = e;
        }
    }

    @Then("The response should contain details of the newly created passenger")
    public void responseContainsCreatedPassengerDetails() {
        var expected = DataUtil.defaultCreatePassengerResponse();

        assertThat(createPassengerResponse).isEqualTo(expected);
    }

    @Given("A passenger with phone: {string}, that already exist")
    public void passengerWithPhoneExist(String phone) {
        doReturn(Boolean.TRUE)
                .when(passengerRepo)
                .existsByPhone(phone);

        assertThat(passengerRepo.existsByPhone(phone)).isTrue();
    }

    @Then("The IllegalArgumentException with the message containing phone: {string}, should be thrown")
    public void illegalArgumentExceptionThrown(String phone) {
        var expected = String.format(USER_WITH_THE_SAME_PHONE_IS_EXISTS_MESSAGE, phone);
        var actual = exception.getMessage();

        assertThat(actual).isEqualTo(expected);
    }

    @When("An external passenger identifier: {uuid}, is passed to the findPassengerPaymentMethod method")
    public void externalIdPassedToFindPassengerPaymentMethod(UUID passengerExternalId) {
        try {
            paymentMethodResponse = passengerService.findPassengerPaymentMethod(passengerExternalId);
        } catch (EntityNotFoundException e) {
            exception = e;
        }
    }

    @Then("The response should contain details of the passenger payment method with externalId: {uuid}")
    public void responseContainsPassengerPaymentMethodDetails(UUID passengerExternalId) {
        var passenger = passengerRepo.findByExternalId(passengerExternalId).get();
        var expected = passengerMapper.toPaymentMethodDto(passenger);

        assertThat(paymentMethodResponse).isEqualTo(expected);
    }

    @Given("A passenger with externalId: {uuid}, that exist")
    public void passengerWithExternalIdExistDelete(UUID passengerExternalId) {
        Passenger passenger = DataUtil.defaultPassenger();

        doReturn(Optional.of(passenger))
                .when(passengerRepo)
                .findByExternalId(passengerExternalId);

        assertThat(passengerRepo.findByExternalId(passengerExternalId)).isPresent();
    }

    @Given("A passenger with externalId: {uuid}, that doesn't exist")
    public void passengerWithExternalIdNotExistDelete(UUID passengerExternalId) {
        doReturn(Optional.empty())
                .when(passengerRepo)
                .findByExternalId(passengerExternalId);

        assertThat(passengerRepo.findByExternalId(passengerExternalId)).isEmpty();
    }

    @When("The passengerExternalId: {uuid}, is passed to the delete method")
    public void externalIdPassedToDeleteMethod(UUID passengerExternalId) {
        try {
            passengerService.delete(passengerExternalId);
        } catch (EntityNotFoundException e) {
            exception = e;
        }
    }

    @Then("The passenger with passengerExternalId: {uuid}, should be deleted from the database")
    public void passengerDeletedFromDatabase(UUID passengerExternalId) {
        var passenger = passengerRepo.findByExternalId(passengerExternalId);
        verify(passengerRepo).delete(passenger.get());
    }
}