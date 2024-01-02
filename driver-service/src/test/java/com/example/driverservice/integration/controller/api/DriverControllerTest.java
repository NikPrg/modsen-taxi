package com.example.driverservice.integration.controller.api;

import com.example.driverservice.dto.request.DriverRequest;
import com.example.driverservice.dto.request.UpdateDriverRequest;
import com.example.driverservice.dto.response.DriverResponse;
import com.example.driverservice.dto.response.error.ErrorResponse;
import com.example.driverservice.integration.TestcontainersBase;
import com.example.driverservice.mapper.DriverMapper;
import com.example.driverservice.model.entity.Driver;
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

import static com.example.driverservice.util.ApiRoutesConstants.DRIVER_EXTERNAL_ID_ENDPOINT;
import static com.example.driverservice.util.ApiRoutesConstants.PUBLIC_API_V1_DRIVERS;
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
public class DriverControllerTest extends TestcontainersBase {

    @Autowired
    private DriverMapper driverMapper;

    @LocalServerPort
    private Integer port;
    private String BASE_URL;

    @PostConstruct
    public void setUp() {
        BASE_URL = HostUtil.getHost() + port + PUBLIC_API_V1_DRIVERS;
    }

    @Test
    void findDriverByExternalId_shouldReturnExpectedResponse() {
        // arrange
        UUID vladExternalId = EntitiesUtil.VLAD_EXTERNAL_ID;
        Driver driver = EntitiesUtil.vladAvailableDriver();
        DriverResponse expected = driverMapper.toDto(driver);

        // act
        DriverResponse actual = when()
                .get(BASE_URL + DRIVER_EXTERNAL_ID_ENDPOINT, vladExternalId)
                .then()
                .log().all()
                .assertThat()
                .contentType(ContentType.JSON)
                .extract()
                .as(DriverResponse.class);

        // assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void findDriverByExternalId_shouldReturnNotFoundCode() {
        // arrange
        UUID notExistedExternalId = DataUtil.NOT_EXISTED_EXTERNAL_ID;

        // act
        ErrorResponse actual = when()
                .get(BASE_URL + DRIVER_EXTERNAL_ID_ENDPOINT, notExistedExternalId)
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
    void createDriver_shouldReturnCreatedCode() {
        // arrange
        DriverRequest requestBody = new DriverRequest("Vlad", "Mihalcea", "+375251234578");

        // act & assert
        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .post(BASE_URL)
                .then()
                .log().all()
                .assertThat()
                .contentType(ContentType.JSON)
                .statusCode(HttpStatus.CREATED.value());
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("invalidDriverCreatesForBadRequest")
    void createDriver_shouldReturnBadRequestCode(DriverRequest request) {
        // act
        ErrorResponse actual = given()
                .contentType(ContentType.JSON)
                .body(request)
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
    void updateDriver_shouldReturnOkCode() {
        // arrange
        UUID vladExternalId = EntitiesUtil.VLAD_EXTERNAL_ID;
        UpdateDriverRequest requestBody = new UpdateDriverRequest("Qwerty", "Qwerty", null);

        // act & assert
        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put(BASE_URL + DRIVER_EXTERNAL_ID_ENDPOINT, vladExternalId)
                .then()
                .log().all()
                .assertThat()
                .contentType(ContentType.JSON)
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void deleteDriver_shouldReturnNoContentCode() {
        // arrange
        UUID vladExternalId = EntitiesUtil.VLAD_EXTERNAL_ID;

        // act & assert
        when()
                .delete(BASE_URL + DRIVER_EXTERNAL_ID_ENDPOINT, vladExternalId)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void deleteDriver_shouldReturnNotFoundCode() {
        // arrange
        UUID notExistedPassengerExternalId = DataUtil.NOT_EXISTED_EXTERNAL_ID;

        // act & assert
        when()
                .delete(BASE_URL + DRIVER_EXTERNAL_ID_ENDPOINT, notExistedPassengerExternalId)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    private static Stream<DriverRequest> invalidDriverCreatesForBadRequest() {
        return Stream.of(
                new DriverRequest("", "test", "+375259422974"), //invalid firstName
                new DriverRequest(" ", "test", "+375259422974"), //invalid firstName
                new DriverRequest("a", "test", "+375259422974"), //invalid firstName
                new DriverRequest("test", "", "+375259422974"), //invalid lastName
                new DriverRequest("test", " ", "+375259422974"), //invalid lastName
                new DriverRequest("test", "a", "+375259422974"), //invalid lastName
                new DriverRequest("test", "test", "qwerty"), //invalid phone
                new DriverRequest("test", "test", "+37725467435"), //invalid phone
                new DriverRequest("test", "test", "+375999422978"), //invalid phone
                new DriverRequest("test", "test", "375259422974"), //invalid phone
                new DriverRequest("test", "test", "1216545645646554") //invalid phone
        );
    }
}
