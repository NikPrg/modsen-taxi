package com.example.driverservice.component.step;

import com.example.driverservice.amqp.handler.SendRequestHandler;
import com.example.driverservice.amqp.message.DriverInfoMessage;
import com.example.driverservice.dto.request.DriverRequest;
import com.example.driverservice.dto.response.CreateDriverResponse;
import com.example.driverservice.dto.response.DriverResponse;
import com.example.driverservice.mapper.DriverMapper;
import com.example.driverservice.model.entity.Driver;
import com.example.driverservice.repository.DriverRepository;
import com.example.driverservice.service.DriverService;
import com.example.driverservice.service.impl.DriverServiceImpl;
import com.example.driverservice.util.DataComposerUtils;
import com.example.driverservice.util.DataUtil;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;
import java.util.UUID;

import static com.example.driverservice.util.ExceptionMessagesConstants.DRIVER_NOT_FOUND_EXCEPTION_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.doNothing;


public class DriverServiceStepDefinitions {

    private DriverRepository driverRepo;
    private DriverMapper driverMapper;
    private SendRequestHandler sendRequestHandler;
    private WebClient webClient;
    private DataComposerUtils dataComposerUtils;
    private DriverService driverService;

    private DriverResponse driverResponse;
    private CreateDriverResponse createDriverResponse;
    private Exception exception;

    @Before
    public void setUp() {
        this.driverRepo = mock(DriverRepository.class);
        this.driverMapper = mock(DriverMapper.class);
        this.sendRequestHandler = mock(SendRequestHandler.class);
        this.webClient = mock(WebClient.class);
        this.dataComposerUtils = mock(DataComposerUtils.class);
        this.driverService = new DriverServiceImpl(driverRepo, driverMapper, sendRequestHandler, webClient, dataComposerUtils);
    }

    @Given("An external driver identifier: {uuid}, that exist")
    public void driverWithExternalIdExists(UUID driverExternalId) {
        Driver driver = DataUtil.defaultDriverWithNoCarStatus();
        DriverResponse expectedResult = DataUtil.defaultDriverResponse();

        doReturn(Optional.of(driver))
                .when(driverRepo)
                .findByExternalId(driverExternalId);
        doReturn(expectedResult)
                .when(driverMapper)
                .toDto(driver);

        var actual = driverRepo.findByExternalId(driverExternalId);
        assertThat(actual).isPresent();
    }

    @Given("An external driver identifier: {uuid}, that doesn't exist")
    public void driverWithExternalIdNotExists(UUID driverExternalId) {
        var actual = driverRepo.findByExternalId(driverExternalId);
        assertThat(actual).isEmpty();
    }

    @Given("A driver creation request")
    public void driverCreateDetails() {
        DriverRequest driverRequest = DataUtil.defaultDriverRequest();
        Driver driver = DataUtil.defaultDriverWithCreatedStatus();
        DriverInfoMessage message = DataUtil.defaultDriverInfoMessageWithNoCar();
        CreateDriverResponse expected = DataUtil.defaultCreateDriverResponse();

        doReturn(driver)
                .when(driverMapper)
                .toDriver(driverRequest);
        doReturn(message)
                .when(dataComposerUtils)
                .buildDriverInfoMessage(driver);
        doNothing()
                .when(sendRequestHandler)
                .sendDriverInfoRequestToKafka(message);
        doReturn(expected)
                .when(driverMapper)
                .toCreateDto(driver);

        CreateDriverResponse actual = driverService.createDriver(driverRequest);

        assertThat(actual).isEqualTo(expected);
    }

    @When("An external driver identifier: {uuid}, is passed to the findDriverByExternalId method")
    public void externalIdPassedToFindByExternalIdMethod(UUID driverExternalId) {
        try {
            driverResponse = driverService.findDriverByExternalId(driverExternalId);
        } catch (EntityNotFoundException e) {
            exception = e;
        }
    }

    @When("A create request is passed to the createDriver method")
    public void createRequestPassedToCreateDriverMethod() {
        DriverRequest driverRequest = DataUtil.defaultDriverRequest();
        createDriverResponse = driverService.createDriver(driverRequest);
    }

    @When("The driverExternalId: {uuid}, is passed to the deleteDriver method")
    public void driverExternalIdPassedToDeleteDriverMethod(UUID driverExternalId) {
        try {
            driverService.deleteDriver(driverExternalId);
        } catch (EntityNotFoundException e) {
            exception = e;
        }
    }

    @Then("The response should contain details of the driver with driverExternalId: {uuid}")
    public void responseContainsDriverDetails(UUID driverExternalId) {
        var driver = driverRepo.findByExternalId(driverExternalId).get();
        var expected = driverMapper.toDto(driver);

        assertThat(driverResponse).isEqualTo(expected);
    }

    @Then("The EntityNotFoundException with the message containing driverExternalId: {uuid}, should be thrown")
    public void driverNotFoundExceptionThrown(UUID driverExternalId) {
        var expected = String.format(DRIVER_NOT_FOUND_EXCEPTION_MESSAGE, driverExternalId);
        var actual = exception.getMessage();

        assertThat(actual).isEqualTo(expected);
    }

    @Then("The response should contain details of the created driver")
    public void responseContainsCreatedDriverDetails() {
        CreateDriverResponse expected = DataUtil.defaultCreateDriverResponse();
        assertThat(createDriverResponse).isEqualTo(expected);
    }

    @Then("The driver with externalId: {uuid}, should be deleted from the database")
    public void driverDeletedFromDatabase(UUID driverExternalId) {
        var driver = driverRepo.findByExternalId(driverExternalId);
        verify(driverRepo).delete(driver.get());
    }
}
