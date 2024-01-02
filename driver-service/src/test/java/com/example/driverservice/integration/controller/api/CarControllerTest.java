package com.example.driverservice.integration.controller.api;

import com.example.driverservice.dto.request.CarRequest;
import com.example.driverservice.dto.request.UpdateCarRequest;
import com.example.driverservice.dto.response.CarResponse;
import com.example.driverservice.dto.response.error.ErrorResponse;
import com.example.driverservice.integration.TestcontainersBase;
import com.example.driverservice.mapper.CarMapper;
import com.example.driverservice.model.entity.Car;
import com.example.driverservice.util.DataUtil;
import com.example.driverservice.util.EntitiesUtil;
import com.example.driverservice.util.HostUtil;
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

import static com.example.driverservice.util.ApiRoutesConstants.*;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.assertj.core.api.Assertions.assertThat;

@Sql(
        scripts = {"classpath:sql/insert-data.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
@Sql(
        scripts = {"classpath:sql/delete-data.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class CarControllerTest extends TestcontainersBase {

    @Autowired
    private CarMapper carMapper;

    @LocalServerPort
    private Integer port;
    private String BASE_URL;

    @PostConstruct
    public void setUp() {
        BASE_URL = HostUtil.getHost() + port + PUBLIC_API_V1;
    }

    @Test
    void findCarByExternalId_shouldReturnExpectedResponse() {
        // arrange
        UUID vCarExternalId = EntitiesUtil.V_CAR_EXTERNAL_ID;
        Car car = EntitiesUtil.vCar();
        CarResponse expected = carMapper.toDto(car);

        // act
        CarResponse actual = when()
                .get(BASE_URL + CARS_CARD_EXTERNAL_ID_ENDPOINT, vCarExternalId)
                .then()
                .log().all()
                .assertThat()
                .contentType(ContentType.JSON)
                .extract()
                .as(CarResponse.class);

        // assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void findCarByExternalId_shouldReturnNotFoundCode() {
        // arrange
        UUID notExistedExternalId = DataUtil.NOT_EXISTED_EXTERNAL_ID;

        // act
        ErrorResponse actual = when()
                .get(BASE_URL + CARS_CARD_EXTERNAL_ID_ENDPOINT, notExistedExternalId)
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
    void createCar_shouldReturnCreatedCode() {
        // arrange
        UUID ivanExternalId = EntitiesUtil.IVAN_EXTERNAL_ID;
        CarRequest requestBody = new CarRequest("8229AX-3", "Mazda", "deep blue");

        // act & assert
        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(BASE_URL + DRIVERS_DRIVER_EXTERNAL_ID_CARS_ENDPOINT, ivanExternalId)
                .then()
                .log().all()
                .assertThat()
                .contentType(ContentType.JSON)
                .statusCode(HttpStatus.CREATED.value());
    }

    @Test
    void createCar_whenDriverNotExist_shouldReturnNotFoundCode() {
        // arrange
        UUID notExistedExternalId = DataUtil.NOT_EXISTED_EXTERNAL_ID;
        CarRequest requestBody = new CarRequest("8229AX-3", "Mazda", "deep blue");

        // act
        ErrorResponse actual = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(BASE_URL + DRIVERS_DRIVER_EXTERNAL_ID_CARS_ENDPOINT, notExistedExternalId)
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
    void createCar_whenDriverAlreadyHasCar_shouldReturnBadRequestCode() {
        // arrange
        UUID vladExternalId = EntitiesUtil.VLAD_EXTERNAL_ID;
        CarRequest requestBody = new CarRequest("8229AX-3", "Mazda", "deep blue");

        // act
        ErrorResponse actual = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(BASE_URL + DRIVERS_DRIVER_EXTERNAL_ID_CARS_ENDPOINT, vladExternalId)
                .then()
                .log().all()
                .assertThat()
                .contentType(ContentType.JSON)
                .extract()
                .as(ErrorResponse.class);

        // assert
        assertThat(actual.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("invalidCarCreatesForBadRequest")
    void createCar_whenValidationError_shouldReturnBadRequestCode(CarRequest request) {
        // arrange
        UUID ivanExternalId = EntitiesUtil.IVAN_EXTERNAL_ID;

        // act
        ErrorResponse actual = given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(BASE_URL + DRIVERS_DRIVER_EXTERNAL_ID_CARS_ENDPOINT, ivanExternalId)
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
    void updateDriverCar_shouldReturnOkCode() {
        // arrange
        UUID vladExternalId = EntitiesUtil.VLAD_EXTERNAL_ID;
        UpdateCarRequest requestBody = new UpdateCarRequest("8244MH-0", "test", "test");

        // act & assert
        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put(BASE_URL + DRIVERS_DRIVER_EXTERNAL_ID_CARS_ENDPOINT, vladExternalId)
                .then()
                .log().all()
                .assertThat()
                .contentType(ContentType.JSON)
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void updateDriverCar_whenDriverNotExist_shouldReturnNotFoundCode() {
        // arrange
        UUID notExistedExternalId = DataUtil.NOT_EXISTED_EXTERNAL_ID;
        UpdateCarRequest requestBody = new UpdateCarRequest("8244MH-0", "test", "test");

        // act & assert
        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put(BASE_URL + DRIVERS_DRIVER_EXTERNAL_ID_CARS_ENDPOINT, notExistedExternalId)
                .then()
                .log().all()
                .assertThat()
                .contentType(ContentType.JSON)
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void updateDriverCar_whenDriverHasNoCar_shouldReturnNotFoundCode() {
        // arrange
        UUID ivanExternalId = EntitiesUtil.IVAN_EXTERNAL_ID;
        UpdateCarRequest requestBody = new UpdateCarRequest("8244MH-0", "test", "test");

        // act
        ErrorResponse actual = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put(BASE_URL + DRIVERS_DRIVER_EXTERNAL_ID_CARS_ENDPOINT, ivanExternalId)
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
    void deleteDriverCar_shouldReturnNoContentCode() {
        // arrange
        UUID vladExternalId = EntitiesUtil.VLAD_EXTERNAL_ID;
        UUID vCarExternalId = EntitiesUtil.V_CAR_EXTERNAL_ID;

        // act & assert
        when()
                .delete(BASE_URL + DRIVERS_DRIVER_EXTERNAL_ID_CARS_CAR_EXTERNAL_ID_ENDPOINT, vladExternalId, vCarExternalId)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void deleteDriverCar_whenDriverNotExist_shouldReturnNotFoundCode() {
        // arrange
        UUID notExistedExternalId = DataUtil.NOT_EXISTED_EXTERNAL_ID;
        UUID vCarExternalId = EntitiesUtil.V_CAR_EXTERNAL_ID;

        // act
        ErrorResponse actual = when()
                .delete(BASE_URL + DRIVERS_DRIVER_EXTERNAL_ID_CARS_CAR_EXTERNAL_ID_ENDPOINT, notExistedExternalId, vCarExternalId)
                .then()
                .log().all()
                .extract()
                .as(ErrorResponse.class);

        // assert
        assertThat(actual.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void deleteDriverCar_whenCarNotExist_shouldReturnNotFoundCode() {
        // arrange
        UUID vladExternalId = EntitiesUtil.VLAD_EXTERNAL_ID;
        UUID notExistedExternalId = DataUtil.NOT_EXISTED_EXTERNAL_ID;

        // act
        ErrorResponse actual = when()
                .delete(BASE_URL + DRIVERS_DRIVER_EXTERNAL_ID_CARS_CAR_EXTERNAL_ID_ENDPOINT, vladExternalId, notExistedExternalId)
                .then()
                .log().all()
                .extract()
                .as(ErrorResponse.class);

        // assert
        assertThat(actual.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void deleteDriverCar_whenCarNotBelongDriver_shouldReturnBadRequestCode() {
        // arrange
        UUID vladExternalId = EntitiesUtil.VLAD_EXTERNAL_ID;
        UUID gCarExternalId = EntitiesUtil.G_CAR_EXTERNAL_ID;

        // act
        ErrorResponse actual = when()
                .delete(BASE_URL + DRIVERS_DRIVER_EXTERNAL_ID_CARS_CAR_EXTERNAL_ID_ENDPOINT, vladExternalId, gCarExternalId)
                .then()
                .log().all()
                .extract()
                .as(ErrorResponse.class);

        // assert
        assertThat(actual.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private static Stream<CarRequest> invalidCarCreatesForBadRequest() {
        return Stream.of(
                new CarRequest("", "test", "test"), //invalid licencePlate
                new CarRequest(" ", "test", "test"), //invalid licencePlate
                new CarRequest("a", "test", "test"), //invalid licencePlate
                new CarRequest("ad11d1s-123", "test", "test"), //invalid licencePlate
                new CarRequest("814521-0", "test", "test"), //invalid licencePlate
                new CarRequest("MH1231-0", "test", "test"), //invalid licencePlate
                new CarRequest("8244MH-0", "", "test"), //invalid model
                new CarRequest("8244MH-0", " ", "test"), //invalid model
                new CarRequest("8244MH-0", "test", ""), //invalid color
                new CarRequest("8244MH-0", "test", " ") //invalid color
        );
    }
}
