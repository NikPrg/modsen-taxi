package com.example.ridesservice.integration.controller.api;

import com.example.ridesservice.dto.request.CreateRideRequest;
import com.example.ridesservice.dto.response.error.ErrorResponse;
import com.example.ridesservice.dto.response.ride.GetRideResponse;
import com.example.ridesservice.dto.response.ride.StartRideResponse;
import com.example.ridesservice.integration.TestcontainersBase;
import com.example.ridesservice.mapper.RideMapper;
import com.example.ridesservice.model.Ride;
import com.example.ridesservice.model.enums.RideStatus;
import com.example.ridesservice.util.DataUtil;
import com.example.ridesservice.util.EntityUtil;
import com.example.ridesservice.util.HostUtil;
import io.restassured.http.ContentType;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.util.UUID;

import static com.example.ridesservice.util.ApiRoutesConstants.*;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.assertj.core.api.Assertions.assertThat;

@Sql(
        scripts = {"classpath:sql/delete-data.sql", "classpath:sql/insert-data.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class RideControllerIntegrationTest extends TestcontainersBase {

    @Autowired
    private RideMapper rideMapper;

    @LocalServerPort
    private Integer port;
    private String BASE_URL;

    @PostConstruct
    public void setUp() {
        BASE_URL = HostUtil.getHost() + port + PUBLIC_API_V1_RIDES;
    }

    @Test
    void findRideByPassengerExternalId_shouldReturnExpectedResponse() {
        // arrange
        UUID finishedRideExternalId = EntityUtil.FINISHED_RIDE_EXTERNAL_ID;
        UUID passengerExternalId = EntityUtil.FINISHED_RIDE_PASSENGER_EXTERNAL_ID;
        Ride ride = EntityUtil.finishedRide();
        GetRideResponse expected = rideMapper.toGetRideDto(ride);

        // act
        GetRideResponse actual = when()
                .get(BASE_URL + RIDE_EXTERNAL_ID_PASSENGERS_PASSENGER_EXTERNAL_ID_ENDPOINT, finishedRideExternalId, passengerExternalId)
                .then()
                .log().all()
                .assertThat()
                .contentType(ContentType.JSON)
                .extract()
                .as(GetRideResponse.class);

        // assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void findRideByPassengerExternalId_whenRideNotExist_shouldReturnNotFoundCode() {
        // arrange
        UUID notExistedRideExternalId = DataUtil.NOT_EXISTED_EXTERNAL_ID;
        UUID passengerExternalId = EntityUtil.FINISHED_RIDE_PASSENGER_EXTERNAL_ID;

        // act
        ErrorResponse actual = when()
                .get(BASE_URL + RIDE_EXTERNAL_ID_PASSENGERS_PASSENGER_EXTERNAL_ID_ENDPOINT, notExistedRideExternalId, passengerExternalId)
                .then()
                .log().all()
                .assertThat()
                .contentType(ContentType.JSON)
                .extract()
                .as(ErrorResponse.class);

        // assert
        assertThat(actual.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void bookRide_shouldReturnCreatedCode() {
        // arrange
        UUID passengerExternalId = EntityUtil.FINISHED_RIDE_PASSENGER_EXTERNAL_ID;
        CreateRideRequest requestBody = new CreateRideRequest("test", "test");

        // act & assert
        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(BASE_URL + PASSENGERS_PASSENGER_EXTERNAL_ID_ENDPOINT, passengerExternalId)
                .then()
                .log().all()
                .assertThat()
                .contentType(ContentType.JSON)
                .statusCode(HttpStatus.CREATED.value());
    }

    @Test
    void acceptRide_shouldReturnOkCode() {
        // arrange
        UUID rideExternalId = EntityUtil.INITIATED_RIDE_EXTERNAL_ID;
        UUID availableDriverExternalId = EntityUtil.VLAD_EXTERNAL_ID;

        // act & assert
        when()
                .put(BASE_URL + RIDE_EXTERNAL_ID_DRIVERS_DRIVER_EXTERNAL_ID_ACCEPT_ENDPOINT, rideExternalId, availableDriverExternalId)
                .then()
                .log().all()
                .assertThat()
                .contentType(ContentType.JSON)
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void acceptRide_whenRideNotExist_shouldReturnNotFoundCode() {
        // arrange
        UUID notExistedRideExternalId = DataUtil.NOT_EXISTED_EXTERNAL_ID;
        UUID availableDriverExternalId = EntityUtil.VLAD_EXTERNAL_ID;

        // act
        ErrorResponse actual = when()
                .put(BASE_URL + RIDE_EXTERNAL_ID_DRIVERS_DRIVER_EXTERNAL_ID_ACCEPT_ENDPOINT, notExistedRideExternalId, availableDriverExternalId)
                .then()
                .log().all()
                .assertThat()
                .contentType(ContentType.JSON)
                .extract()
                .as(ErrorResponse.class);

        // assert
        assertThat(actual.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void acceptRide_whenRideAlreadyAccepted_shouldReturnBadRequestCode() {
        // arrange
        UUID rideExternalId = EntityUtil.ACCEPTED_RIDE_EXTERNAL_ID;
        UUID availableDriverExternalId = EntityUtil.VLAD_EXTERNAL_ID;

        // act
        ErrorResponse actual = when()
                .put(BASE_URL + RIDE_EXTERNAL_ID_DRIVERS_DRIVER_EXTERNAL_ID_ACCEPT_ENDPOINT, rideExternalId, availableDriverExternalId)
                .then()
                .log().all()
                .assertThat()
                .contentType(ContentType.JSON)
                .extract()
                .as(ErrorResponse.class);

        // assert
        assertThat(actual.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void acceptRide_whenDriverNotExist_shouldReturnBadRequestCode() {
        // arrange
        UUID rideExternalId = EntityUtil.INITIATED_RIDE_EXTERNAL_ID;
        UUID notExistedDriverExternalId = DataUtil.NOT_EXISTED_EXTERNAL_ID;

        // act
        ErrorResponse actual = when()
                .put(BASE_URL + RIDE_EXTERNAL_ID_DRIVERS_DRIVER_EXTERNAL_ID_ACCEPT_ENDPOINT, rideExternalId, notExistedDriverExternalId)
                .then()
                .log().all()
                .assertThat()
                .contentType(ContentType.JSON)
                .extract()
                .as(ErrorResponse.class);

        // assert
        assertThat(actual.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void acceptRide_whenDriverAlreadyInUse_shouldReturnBadRequestCode() {
        // arrange
        UUID rideExternalId = EntityUtil.INITIATED_RIDE_EXTERNAL_ID;
        UUID unavailableDriverExternalId = EntityUtil.GLEB_EXTERNAL_ID;

        // act
        ErrorResponse actual = when()
                .put(BASE_URL + RIDE_EXTERNAL_ID_DRIVERS_DRIVER_EXTERNAL_ID_ACCEPT_ENDPOINT, rideExternalId, unavailableDriverExternalId)
                .then()
                .log().all()
                .assertThat()
                .contentType(ContentType.JSON)
                .extract()
                .as(ErrorResponse.class);

        // assert
        assertThat(actual.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void startRide_shouldReturnExpectedResponse() {
        // arrange
        UUID rideExternalId = EntityUtil.ACCEPTED_RIDE_EXTERNAL_ID;
        Long acceptedRideId = EntityUtil.ACCEPTED_RIDE_ID;
        UUID towardsPassengerDriverExternalId = EntityUtil.ANTON_EXTERNAL_ID;
        StartRideResponse expected = new StartRideResponse(acceptedRideId, rideExternalId, RideStatus.STARTED);

        // act
        StartRideResponse actual = when()
                .put(BASE_URL + RIDE_EXTERNAL_ID_DRIVERS_DRIVER_EXTERNAL_ID_START_ENDPOINT, rideExternalId, towardsPassengerDriverExternalId)
                .then()
                .log().all()
                .assertThat()
                .contentType(ContentType.JSON)
                .extract()
                .as(StartRideResponse.class);

        // assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void startRide_whenRideNotExist_shouldReturnNotFoundCode() {
        // arrange
        UUID notExistedRideExternalId = DataUtil.NOT_EXISTED_EXTERNAL_ID;
        UUID towardsPassengerDriverExternalId = EntityUtil.ANTON_EXTERNAL_ID;

        // act
        ErrorResponse actual = when()
                .put(BASE_URL + RIDE_EXTERNAL_ID_DRIVERS_DRIVER_EXTERNAL_ID_START_ENDPOINT, notExistedRideExternalId, towardsPassengerDriverExternalId)
                .then()
                .log().all()
                .assertThat()
                .contentType(ContentType.JSON)
                .extract()
                .as(ErrorResponse.class);

        // assert
        assertThat(actual.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void startRide_whenRideNotAccepted_shouldReturnBadRequestCode() {
        // arrange
        UUID initiatedRideExternalId = EntityUtil.INITIATED_RIDE_EXTERNAL_ID;
        UUID towardsPassengerDriverExternalId = EntityUtil.ANTON_EXTERNAL_ID;

        // act
        ErrorResponse actual = when()
                .put(BASE_URL + RIDE_EXTERNAL_ID_DRIVERS_DRIVER_EXTERNAL_ID_START_ENDPOINT, initiatedRideExternalId, towardsPassengerDriverExternalId)
                .then()
                .log().all()
                .assertThat()
                .contentType(ContentType.JSON)
                .extract()
                .as(ErrorResponse.class);

        // assert
        assertThat(actual.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void startRide_whenDriverNotBelongDrive_shouldReturnBadRequestCode() {
        // arrange
        UUID acceptedRideExternalId = EntityUtil.ACCEPTED_RIDE_EXTERNAL_ID;
        UUID driverExternalId = EntityUtil.VLAD_EXTERNAL_ID;

        // act
        ErrorResponse actual = when()
                .put(BASE_URL + RIDE_EXTERNAL_ID_DRIVERS_DRIVER_EXTERNAL_ID_START_ENDPOINT, acceptedRideExternalId, driverExternalId)
                .then()
                .log().all()
                .assertThat()
                .contentType(ContentType.JSON)
                .extract()
                .as(ErrorResponse.class);

        // assert
        assertThat(actual.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void startRide_whenRideAlreadyStarted_shouldReturnBadRequestCode() {
        // arrange
        UUID startedRideExternalId = EntityUtil.STARTED_RIDE_EXTERNAL_ID;
        UUID unavailableDriverExternalId = EntityUtil.GLEB_EXTERNAL_ID;

        // act
        ErrorResponse actual = when()
                .put(BASE_URL + RIDE_EXTERNAL_ID_DRIVERS_DRIVER_EXTERNAL_ID_START_ENDPOINT, startedRideExternalId, unavailableDriverExternalId)
                .then()
                .log().all()
                .assertThat()
                .contentType(ContentType.JSON)
                .extract()
                .as(ErrorResponse.class);

        // assert
        assertThat(actual.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void startRide_whenRideAlreadyFinished_shouldReturnBadRequestCode() {
        // arrange
        UUID finishedRideExternalId = EntityUtil.FINISHED_RIDE_EXTERNAL_ID;
        UUID availableDriverExternalId = EntityUtil.VLAD_EXTERNAL_ID;

        // act
        ErrorResponse actual = when()
                .put(BASE_URL + RIDE_EXTERNAL_ID_DRIVERS_DRIVER_EXTERNAL_ID_START_ENDPOINT, finishedRideExternalId, availableDriverExternalId)
                .then()
                .log().all()
                .assertThat()
                .contentType(ContentType.JSON)
                .extract()
                .as(ErrorResponse.class);

        // assert
        assertThat(actual.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void finishRide_whenRideNotExist_shouldReturnNotFoundCode() {
        // arrange
        UUID notExistedRideExternalId = DataUtil.NOT_EXISTED_EXTERNAL_ID;
        UUID unavailableDriverExternalId = EntityUtil.GLEB_EXTERNAL_ID;

        // act
        ErrorResponse actual = when()
                .put(BASE_URL + RIDE_EXTERNAL_ID_DRIVERS_DRIVER_EXTERNAL_ID_FINISH_ENDPOINT, notExistedRideExternalId, unavailableDriverExternalId)
                .then()
                .log().all()
                .assertThat()
                .contentType(ContentType.JSON)
                .extract()
                .as(ErrorResponse.class);

        // assert
        assertThat(actual.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void finishRide_whenRideNotAccepted_shouldReturnBadRequestCode() {
        // arrange
        UUID initiatedRideExternalId = EntityUtil.INITIATED_RIDE_EXTERNAL_ID;
        UUID unavailableDriverExternalId = EntityUtil.GLEB_EXTERNAL_ID;

        // act
        ErrorResponse actual = when()
                .put(BASE_URL + RIDE_EXTERNAL_ID_DRIVERS_DRIVER_EXTERNAL_ID_FINISH_ENDPOINT, initiatedRideExternalId, unavailableDriverExternalId)
                .then()
                .log().all()
                .assertThat()
                .contentType(ContentType.JSON)
                .extract()
                .as(ErrorResponse.class);

        // assert
        assertThat(actual.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}