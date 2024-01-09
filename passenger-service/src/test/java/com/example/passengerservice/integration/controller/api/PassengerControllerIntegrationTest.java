package com.example.passengerservice.integration.controller.api;

import com.example.passengerservice.dto.request.ChangePhoneRequest;
import com.example.passengerservice.dto.request.PassengerRegistrationRequest;
import com.example.passengerservice.dto.request.PassengerRequest;
import com.example.passengerservice.dto.response.CreatePassengerResponse;
import com.example.passengerservice.dto.response.PassengerResponse;
import com.example.passengerservice.dto.response.PaymentMethodResponse;
import com.example.passengerservice.dto.response.error.ErrorResponse;
import com.example.passengerservice.integration.TestcontainersBase;
import com.example.passengerservice.mapper.PassengerMapper;
import com.example.passengerservice.model.Passenger;
import com.example.passengerservice.repository.PassengerRepository;
import com.example.passengerservice.util.DataUtil;
import com.example.passengerservice.util.HostUtil;
import io.restassured.http.ContentType;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.util.UUID;
import java.util.stream.Stream;

import static com.example.passengerservice.util.ApiRoutesConstants.*;
import static com.example.passengerservice.util.DataUtil.FIRST_NAME;
import static com.example.passengerservice.util.DataUtil.CARD_EXTERNAL_ID;
import static com.example.passengerservice.util.DataUtil.LAST_NAME;
import static com.example.passengerservice.util.DataUtil.PHONE;
import static com.example.passengerservice.util.DataUtil.NEW_FIRST_NAME;
import static com.example.passengerservice.util.DataUtil.NEW_LAST_NAME;
import static com.example.passengerservice.util.DataUtil.NEW_PHONE;
import static com.example.passengerservice.util.EntitiesUtil.NIKITA_ID;
import static com.example.passengerservice.util.EntitiesUtil.NIKITA_PHONE;
import static com.example.passengerservice.util.EntitiesUtil.NIKITA_RATE;
import static com.example.passengerservice.util.EntitiesUtil.NIKITA_EXTERNAL_ID;
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
public class PassengerControllerIntegrationTest extends TestcontainersBase {

    @Autowired
    private PassengerRepository passengerRepo;

    @Autowired
    private PassengerMapper passengerMapper;

    @LocalServerPort
    private Integer port;
    private String BASE_URL;

    @PostConstruct
    public void setUp() {
        BASE_URL = HostUtil.getHost() + port + PUBLIC_API_V1_PASSENGERS;
    }

    @Test
    void findPassengerByExternalId_shouldReturnExpectedPassenger() {
        // arrange
        UUID passengerExternalId = NIKITA_EXTERNAL_ID;
        var passenger = passengerRepo.findByExternalId(passengerExternalId);
        var expected = passengerMapper.toDto(passenger.get());

        // act
        var actual = when()
                .get(BASE_URL + PASSENGER_EXTERNAL_ID_ENDPOINT, passengerExternalId)
                .then()
                .log().all()
                .assertThat()
                .contentType(ContentType.JSON)
                .extract()
                .as(PassengerResponse.class);

        // assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void findPassengerByExternalId_shouldReturnNotFoundCode() {
        // arrange
        UUID notExistedPassengerExternalId = DataUtil.NOT_EXISTED_EXTERNAL_ID;

        // act
        ErrorResponse actual = when()
                .get(BASE_URL + PASSENGER_EXTERNAL_ID_ENDPOINT, notExistedPassengerExternalId)
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
    void findPassengerPaymentMethod_shouldReturnExpectedResponse() {
        // arrange
        UUID passengerExternalId = NIKITA_EXTERNAL_ID;
        Passenger passenger = passengerRepo.findByExternalId(passengerExternalId).get();
        PaymentMethodResponse expected = passengerMapper.toPaymentMethodDto(passenger);

        // act
        PaymentMethodResponse actual = when()
                .get(BASE_URL + PASSENGER_EXTERNAL_ID_PAYMENT_METHOD_ENDPOINT, passengerExternalId)
                .then()
                .log().all()
                .assertThat()
                .contentType(ContentType.JSON)
                .extract()
                .as(PaymentMethodResponse.class);

        // assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void findPassengerPaymentMethod_shouldReturnNotFoundCode() {
        // arrange
        UUID notExistedPassengerExternalId = DataUtil.NOT_EXISTED_EXTERNAL_ID;

        // act
        ErrorResponse actual = when()
                .get(BASE_URL + PASSENGER_EXTERNAL_ID_PAYMENT_METHOD_ENDPOINT, notExistedPassengerExternalId)
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
    void createPassenger_shouldReturnCreatedPassengerId() {
        // arrange
        long expectedId = 11;
        PassengerRegistrationRequest requestBody = PassengerRegistrationRequest.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .phone(PHONE)
                .build();

        // act
        CreatePassengerResponse actual = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(BASE_URL)
                .then()
                .log().all()
                .assertThat()
                .contentType(ContentType.JSON)
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(CreatePassengerResponse.class);

        // assert
        assertThat(actual.id()).isEqualTo(expectedId);
    }

    @Test
    void createPassenger_withExistingPhone_shouldReturnBadRequestCode() {
        // arrange
        PassengerRegistrationRequest requestBody = PassengerRegistrationRequest.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .phone(NIKITA_PHONE)
                .build();

        // act
        ErrorResponse actual = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(BASE_URL)
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
    void updatePassenger_shouldReturnUpdatedPassenger() {
        // arrange
        PassengerRequest requestBody = new PassengerRequest(NEW_FIRST_NAME, NEW_LAST_NAME);
        PassengerResponse expected = PassengerResponse.builder()
                .id(NIKITA_ID)
                .externalId(NIKITA_EXTERNAL_ID)
                .firstName(NEW_FIRST_NAME)
                .lastName(NEW_LAST_NAME)
                .phone(NIKITA_PHONE)
                .rate(NIKITA_RATE)
                .build();

        // act
        PassengerResponse actual = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put(BASE_URL + PASSENGER_EXTERNAL_ID_ENDPOINT, NIKITA_EXTERNAL_ID)
                .then()
                .log().all()
                .assertThat()
                .contentType(ContentType.JSON)
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(PassengerResponse.class);

        // assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void updatePassenger_shouldReturnNotFoundCode() {
        // arrange
        UUID notExistedPassengerExternalId = DataUtil.NOT_EXISTED_EXTERNAL_ID;
        PassengerRequest requestBody = new PassengerRequest(NEW_FIRST_NAME, NEW_LAST_NAME);

        // act
        ErrorResponse actual = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put(BASE_URL + PASSENGER_EXTERNAL_ID_ENDPOINT, notExistedPassengerExternalId)
                .then()
                .log().all()
                .assertThat()
                .contentType(ContentType.JSON)
                .extract()
                .as(ErrorResponse.class);

        // assert
        assertThat(actual.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("invalidPassengerUpdatesForBadRequest")
    void updatePassenger_shouldReturnBadRequestCode(PassengerRequest request) {
        // act
        ErrorResponse actual = given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put(BASE_URL + PASSENGER_EXTERNAL_ID_ENDPOINT, NIKITA_EXTERNAL_ID)
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
    void updatePassengerPhone_shouldReturnNoContentCode() {
        // arrange
        ChangePhoneRequest requestBody = new ChangePhoneRequest(NEW_PHONE);

        // act & assert
        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .patch(BASE_URL + PASSENGER_EXTERNAL_ID_PHONE_ENDPOINT, NIKITA_EXTERNAL_ID)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void updatePassengerPhone_shouldReturnNotFoundCode() {
        // arrange
        UUID notExistedPassengerExternalId = DataUtil.NOT_EXISTED_EXTERNAL_ID;
        ChangePhoneRequest requestBody = new ChangePhoneRequest(NEW_PHONE);

        // act
        ErrorResponse actual = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .patch(BASE_URL + PASSENGER_EXTERNAL_ID_PHONE_ENDPOINT, notExistedPassengerExternalId)
                .then()
                .log().all()
                .extract()
                .as(ErrorResponse.class);

        // assert
        assertThat(actual.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("invalidPassengerPhoneUpdatesForBadRequest")
    void updatePassengerPhone_shouldReturnBadRequestCode(ChangePhoneRequest request) {
        // act
        ErrorResponse actual = given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .patch(BASE_URL + PASSENGER_EXTERNAL_ID_PHONE_ENDPOINT, NIKITA_EXTERNAL_ID)
                .then()
                .log().all()
                .extract()
                .as(ErrorResponse.class);

        // assert
        assertThat(actual.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void addCardAsDefaultPaymentMethod_shouldReturnNoContentCode() {
        // act & assert
        when()
                .put(BASE_URL + PASSENGER_EXT_ID_CARDS_CARD_EXT_ID_ENDPOINT, NIKITA_EXTERNAL_ID, CARD_EXTERNAL_ID)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void addCardAsDefaultPaymentMethod_shouldReturnNotFoundCode() {
        // arrange
        UUID notExistedPassengerExternalId = DataUtil.NOT_EXISTED_EXTERNAL_ID;

        // act
        ErrorResponse actual = when()
                .put(BASE_URL + PASSENGER_EXT_ID_CARDS_CARD_EXT_ID_ENDPOINT, notExistedPassengerExternalId, CARD_EXTERNAL_ID)
                .then()
                .log().all()
                .extract()
                .as(ErrorResponse.class);

        // assert
        assertThat(actual.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void deletePassenger_shouldReturnNoContentCode() {
        // act & assert
        when()
                .delete(BASE_URL + PASSENGER_EXTERNAL_ID_ENDPOINT, NIKITA_EXTERNAL_ID)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void deletePassenger_shouldReturnNotFoundCode() {
        // arrange
        UUID notExistedPassengerExternalId = DataUtil.NOT_EXISTED_EXTERNAL_ID;

        // act & assert
        when()
                .delete(BASE_URL + PASSENGER_EXTERNAL_ID_ENDPOINT, notExistedPassengerExternalId)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    private static Stream<ChangePhoneRequest> invalidPassengerPhoneUpdatesForBadRequest() {
        return Stream.of(
                new ChangePhoneRequest(""),
                new ChangePhoneRequest(" "),
                new ChangePhoneRequest("qwerty"),
                new ChangePhoneRequest("+37725467435"),
                new ChangePhoneRequest("+375999422978"),
                new ChangePhoneRequest("375259422974")
        );
    }

    private static Stream<PassengerRequest> invalidPassengerUpdatesForBadRequest() {
        return Stream.of(
                new PassengerRequest("", "Qwerty"), // invalid firstName
                new PassengerRequest(" ", "Qwerty"),// invalid firstName
                new PassengerRequest("T", "Qwerty"),// invalid firstName
                new PassengerRequest("Steven", ""), // invalid lastName
                new PassengerRequest("Steven", " "), // invalid lastName
                new PassengerRequest("Steven", "R") // invalid lastName
        );
    }

}
