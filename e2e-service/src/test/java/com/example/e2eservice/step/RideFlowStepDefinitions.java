package com.example.e2eservice.step;

import com.example.e2eservice.dto.request.CreateRideRequest;
import com.example.e2eservice.entity.DriverStatus;
import com.example.e2eservice.entity.RideStatus;
import com.example.e2eservice.feign.client.DriverClient;
import com.example.e2eservice.feign.client.RideClient;
import com.example.e2eservice.feign.response.driver.DriverResponse;
import com.example.e2eservice.feign.response.rides.CreateRideResponse;
import com.example.e2eservice.feign.response.rides.GetRideResponse;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.util.UUID;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@RequiredArgsConstructor
public class RideFlowStepDefinitions {

    private final RideClient rideClient;
    private final DriverClient driverClient;

    private static UUID rideExternalId;
    private static UUID passengerExternalId;
    private static UUID driverExternalId;

    private CreateRideRequest createRideRequest;
    private CreateRideResponse createRideResponse;
    private GetRideResponse getRideResponse;
    private DriverResponse driverResponse;

    @Given("An existed passenger with externalId: {uuid}, and ride request with pickUpAddress: {string} and destinationAddress: {string}")
    public void passengerExternalIdAndCreateRideRequest(UUID externalId, String pickUpAddress, String destinationAddress) {
        passengerExternalId = externalId;
        createRideRequest = CreateRideRequest.builder()
                .pickUpAddress(pickUpAddress)
                .destinationAddress(destinationAddress)
                .build();
    }

    @When("A passenger with externalId: {uuid}, sends this request to the book ride endpoint")
    public void passengerWithExternalIdSendsCreateRideRequest(UUID passengerExternalId) {
        createRideResponse = rideClient.bookRide(passengerExternalId, createRideRequest);
        rideExternalId = createRideResponse.externalId();
    }

    @Then("A passenger should get details of ride order with status {string}")
    public void passengerGetsDetailsOfCreatedRide(String status) {
        assertThat(createRideResponse.rideStatus()).isEqualTo(RideStatus.valueOf(status));
    }

    @And("In a few seconds, the ride's status is expected to switch to {string}")
    public void afterFewSecRideStatusShouldBeChanged(String status) {
        await()
                .pollInterval(Duration.ofSeconds(3))
                .atMost(10, SECONDS)
                .untilAsserted(() -> {
                    getRideResponse = rideClient.findRideByPassengerExternalId(passengerExternalId, rideExternalId);
                    assertThat(getRideResponse.rideStatus()).isEqualTo(RideStatus.valueOf(status));
                });
    }

    @And("A driver should assign to the ride")
    public void driverShouldBeAssigned() {
        assertThat(getRideResponse.driver()).isNotNull();
        driverExternalId = getRideResponse.driver().externalId();
    }

    @And("A driver's status should be changed to {string}")
    public void driverStatusShouldBeChanged(String status) {
        await()
                .pollInterval(Duration.ofSeconds(3))
                .atMost(10, SECONDS)
                .untilAsserted(() -> {
                    driverResponse = driverClient.findDriverByExternalId(driverExternalId);
                    assertThat(driverResponse.driverStatus()).isEqualTo(DriverStatus.valueOf(status));
                });
    }

    @Given("The ride has status {string}")
    public void rideHasStatus(String status) {
        getRideResponse = rideClient.findRideByPassengerExternalId(passengerExternalId, rideExternalId);
        assertThat(getRideResponse.rideStatus()).isEqualTo(RideStatus.valueOf(status));
    }

    @When("A driver starts the ride")
    public void driverStartsRide() {
        rideClient.startRide(driverExternalId, rideExternalId);
    }

    @Then("A ride's status should be changed to {string}")
    public void rideStatusChanged(String status) {
        getRideResponse = rideClient.findRideByPassengerExternalId(passengerExternalId, rideExternalId);
        assertThat(getRideResponse.rideStatus()).isEqualTo(RideStatus.valueOf(status));
    }

    @When("A driver finishes the ride")
    public void driverFinishesRide() {
        rideClient.finishRide(driverExternalId, rideExternalId);
    }

    @Given("A passenger has that finished ride")
    public void passengerHadAtLeastOneFinishedRide() {
        getRideResponse = rideClient.findRideByPassengerExternalId(passengerExternalId, rideExternalId);
    }

    @Then("A passenger ride not null")
    public void passengerRetrievesDriverProfile() {
        assertThat(getRideResponse).isNotNull();
    }

}
