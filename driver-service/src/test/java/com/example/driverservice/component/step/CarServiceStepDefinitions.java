package com.example.driverservice.component.step;

import com.example.driverservice.amqp.handler.SendRequestHandler;
import com.example.driverservice.amqp.message.DriverInfoMessage;
import com.example.driverservice.dto.request.CarRequest;
import com.example.driverservice.dto.response.CarResponse;
import com.example.driverservice.exception.DriverAlreadyHasCarException;
import com.example.driverservice.mapper.CarMapper;
import com.example.driverservice.model.entity.Car;
import com.example.driverservice.model.entity.Driver;
import com.example.driverservice.repository.CarRepository;
import com.example.driverservice.repository.DriverRepository;
import com.example.driverservice.service.CarService;
import com.example.driverservice.service.impl.CarServiceImpl;
import com.example.driverservice.util.DataComposerUtils;
import com.example.driverservice.util.DataUtil;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.persistence.EntityNotFoundException;

import java.util.Optional;
import java.util.UUID;

import static com.example.driverservice.util.ExceptionMessagesConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.doNothing;

public class CarServiceStepDefinitions {
    private CarRepository carRepo;
    private DriverRepository driverRepo;
    private CarMapper carMapper;
    private SendRequestHandler sendRequestHandler;
    private DataComposerUtils dataComposerUtils;
    private CarService carService;

    private CarResponse carResponse;
    private Exception exception;

    @Before
    public void setUp() {
        this.driverRepo = mock(DriverRepository.class);
        this.carRepo = mock(CarRepository.class);
        this.sendRequestHandler = mock(SendRequestHandler.class);
        this.carMapper = mock(CarMapper.class);
        this.dataComposerUtils = mock(DataComposerUtils.class);
        this.carService = new CarServiceImpl(carRepo, driverRepo, carMapper, sendRequestHandler, dataComposerUtils);
    }

    @Given("An external car identifier: {uuid}, that exist")
    public void carWithExternalIdExit(UUID carExternalId) {
        Car car = DataUtil.defaultCar();
        CarResponse expected = DataUtil.defaultCarResponse();

        doReturn(Optional.of(car))
                .when(carRepo)
                .findByExternalId(carExternalId);
        doReturn(expected)
                .when(carMapper)
                .toDto(car);

        var actual = carRepo.findByExternalId(carExternalId);
        assertThat(actual).isPresent();
    }

    @Given("An external car identifier: {uuid}, that doesn't exist")
    public void carWithExternalIdNotExist(UUID carExternalId) {
        var actual = carRepo.findByExternalId(carExternalId);
        assertThat(actual).isEmpty();
    }

    @When("An external car identifier: {uuid}, is passed to the findCarByExternalId method")
    public void externalIdPassedToFindCarByExternalIdMethod(UUID carExternalId) {
        try {
            carResponse = carService.findByExternalId(carExternalId);
        } catch (EntityNotFoundException e) {
            exception = e;
        }
    }

    @Then("The response should contain details of the car with carExternalId: {uuid}")
    public void responseContainsCarDetails(UUID carExternalId) {
        var driver = carRepo.findByExternalId(carExternalId).get();
        var expected = carMapper.toDto(driver);

        assertThat(carResponse).isEqualTo(expected);
    }

    @Then("The EntityNotFoundException with the message containing carExternalId: {uuid}, should be thrown during car creation")
    public void carNotFoundExceptionThrown(UUID carExternalId) {
        var expected = String.format(CAR_NOT_FOUND_EXCEPTION_MESSAGE, carExternalId);
        var actual = exception.getMessage();

        assertThat(actual).isEqualTo(expected);
    }

    @Given("A car creation request with licensePlate: {string}, model: {string}, color: {string}, and driverExternalId: {uuid}, that exist")
    public void carWithDetailsAndDriverExternalIdExist(String licensePlate, String model, String color, UUID driverExternalId) {
        Car car = DataUtil.defaultCar();
        CarRequest request = new CarRequest(licensePlate, model, color);
        DriverInfoMessage message = DataUtil.defaultDriverInfoMessageWithCar();
        Driver driver = DataUtil.defaultDriverWithNoCarStatus();
        CarResponse expected = DataUtil.defaultCarResponse();

        doReturn(Optional.of(driver))
                .when(driverRepo)
                .findByExternalId(driverExternalId);
        doReturn(car)
                .when(carMapper)
                .toCar(request);
        doReturn(message)
                .when(dataComposerUtils)
                .buildDriverInfoMessage(driver);
        doNothing()
                .when(sendRequestHandler)
                .sendDriverInfoRequestToKafka(message);
        doReturn(expected)
                .when(carMapper)
                .toDto(car);

        assertThat(driverRepo.findByExternalId(driverExternalId)).isPresent();
    }

    @When("A creation request with licensePlate: {string}, model: {string}, color: {string}, and driverExternalId: {uuid}, is passed to the createCar method")
    public void createRequestAndDriverExternalIdIsPassedToCreateMethod(String licensePlate, String model, String color, UUID driverExternalId) {
        CarRequest request = new CarRequest(licensePlate, model, color);

        try {
            carResponse = carService.createCar(driverExternalId, request);
        } catch (EntityNotFoundException | DriverAlreadyHasCarException e) {
            exception = e;
        }
    }

    @Then("The response should contain details of the created car")
    public void responseContainsCreatedCarDetails() {
        CarResponse expected = DataUtil.defaultCarResponse();
        assertThat(carResponse).isEqualTo(expected);
    }

    @Given("An external driver identifier for car creation: {uuid}, that doesn't exist")
    public void driverExternalIdNotExist(UUID driverExternalId) {
        doReturn(Optional.empty())
                .when(driverRepo)
                .findByExternalId(driverExternalId);

        assertThat(driverRepo.findByExternalId(driverExternalId)).isEmpty();
    }

    @Then("The EntityNotFoundException with the message containing driverExternalId: {uuid}, should be thrown during car creation")
    public void driverNotFoundExceptionThrownDuringCarCreation(UUID driverExternalId) {
        var expected = String.format(DRIVER_NOT_FOUND_EXCEPTION_MESSAGE, driverExternalId);
        var actual = exception.getMessage();

        assertThat(actual).isEqualTo(expected);
    }

    @Given("An external driver identifier for car creation: {uuid}, that already has a car")
    public void driverExternalIdAlreadyHasCar(UUID driverExternalId) {
        Driver driver = DataUtil.defaultDriverWithCar();

        doReturn(Optional.of(driver))
                .when(driverRepo)
                .findByExternalId(driverExternalId);

        assertThat(driverRepo.findByExternalId(driverExternalId)).isPresent();
    }

    @Then("The DriverAlreadyHasCarException with the message containing driverExternalId: {uuid}, should be thrown during car creation")
    public void driverAlreadyHasCarExceptionThrown(UUID driverExternalId) {
        var expected = String.format(DRIVER_ALREADY_HAS_CAR_EXCEPTION_MESSAGE, driverExternalId);
        var actual = exception.getMessage();

        assertThat(actual).isEqualTo(expected);
    }

    @Given("An existed external driver identifier: {uuid}, and existed external car identifier: {uuid}")
    public void driverExternalIdExistAndCarExternalIdExist(UUID driverExternalId, UUID carExternalId) {
        Car car = DataUtil.defaultCar();
        Driver driver = DataUtil.defaultDriverWithCar();

        doReturn(Optional.of(driver))
                .when(driverRepo)
                .findByExternalId(driverExternalId);
        doReturn(Optional.of(car))
                .when(carRepo)
                .findByExternalId(carExternalId);

        assertThat(driverRepo.findByExternalId(driverExternalId)).isPresent();
    }

    @When("A driverExternalId: {uuid} and carExternalId: {uuid}, is passed to the deleteDriverCarMethod")
    public void driverExternalIdAndCarExternalIdPassedToDeleteDriverCarMethod(UUID driverExternalId, UUID carExternalId) {
        try {
            carService.deleteDriverCar(driverExternalId, carExternalId);
        } catch (EntityNotFoundException e) {
            exception = e;
        }
    }

    @Then("The car with externalId: {uuid}, should be deleted from the database")
    public void carDeletedFromDatabase(UUID carExternalId) {
        var car = carRepo.findByExternalId(carExternalId);
        verify(carRepo).delete(car.get());
    }

    @Given("A not existed external driver identifier: {uuid}, and existed external car identifier: {uuid}")
    public void driverExternalIdNotExistAndCarExternalIdExist(UUID driverExternalId, UUID carExternalId) {
        Car car = DataUtil.defaultCar();

        doReturn(Optional.empty())
                .when(driverRepo)
                .findByExternalId(driverExternalId);
        doReturn(Optional.of(car))
                .when(carRepo)
                .findByExternalId(carExternalId);

        assertThat(driverRepo.findByExternalId(driverExternalId)).isEmpty();
    }

    @Then("The EntityNotFoundException with the message containing driverExternalId: {uuid}, should be thrown during car removing")
    public void driverNotFoundExceptionThrownDuringCarRemoving(UUID driverExternalId) {
        var expected = String.format(DRIVER_NOT_FOUND_EXCEPTION_MESSAGE, driverExternalId);
        var actual = exception.getMessage();

        assertThat(actual).isEqualTo(expected);
    }

    @Given("An existed external driver identifier: {uuid}, and not existed external car identifier: {uuid}")
    public void driverExternalIdExistAndCarExternalIdNotExist(UUID driverExternalId, UUID carExternalId) {
        Driver driver = DataUtil.defaultDriverWithCar();

        doReturn(Optional.of(driver))
                .when(driverRepo)
                .findByExternalId(driverExternalId);
        doReturn(Optional.empty())
                .when(carRepo)
                .findByExternalId(carExternalId);

        assertThat(carRepo.findByExternalId(carExternalId)).isEmpty();
    }

    @Then("The EntityNotFoundException with the message containing carExternalId: {uuid}, should be thrown during car removing")
    public void CarNotFoundExceptionThrownDuringCarRemoving(UUID carExternalId) {
        var expected = String.format(CAR_NOT_FOUND_EXCEPTION_MESSAGE, carExternalId);
        var actual = exception.getMessage();

        assertThat(actual).isEqualTo(expected);
    }
}