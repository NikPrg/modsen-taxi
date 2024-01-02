package com.example.cardservice.integration.controller.api;

import com.example.cardservice.dto.request.CardRegistrationDto;
import com.example.cardservice.dto.response.AllCardsResponse;
import com.example.cardservice.dto.response.DefaultCardResponse;
import com.example.cardservice.dto.response.error.ErrorResponse;
import com.example.cardservice.integration.TestcontainersBase;
import com.example.cardservice.mapper.CardMapper;
import com.example.cardservice.model.Card;
import com.example.cardservice.model.PassengerCard;
import com.example.cardservice.model.PassengerInfo;
import com.example.cardservice.repository.CardRepository;
import com.example.cardservice.repository.PassengerInfoRepository;
import com.example.cardservice.util.DataUtil;
import com.example.cardservice.util.EntitiesUtil;
import com.example.cardservice.util.HostUtil;
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

import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.example.cardservice.util.ApiRoutesConstants.*;
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
public class CardControllerIntegrationTest extends TestcontainersBase {

    @Autowired
    private CardRepository cardRepo;

    @Autowired
    private PassengerInfoRepository passengerInfoRepo;

    @Autowired
    private CardMapper cardMapper;

    @LocalServerPort
    private Integer port;
    private String BASE_URL;

    @PostConstruct
    public void setUp() {
        BASE_URL = HostUtil.getHost() + port + PUBLIC_API_V1_PASSENGERS;
    }

    @Test
    void findCardsByPassengerExternalId_shouldReturnExpectedResponse() {
        // arrange
        UUID nikitaExternalId = EntitiesUtil.NIKITA_EXTERNAL_ID;
        PassengerInfo passengerInfo = passengerInfoRepo.findByExternalIdFetch(nikitaExternalId).get();
        var expected = new AllCardsResponse(passengerInfo.getCards().stream()
                .map(PassengerCard::getCard)
                .map(cardMapper::toCardDto)
                .collect(Collectors.toList()));

        // act
        AllCardsResponse actual = when()
                .get(BASE_URL + PASSENGER_EXTERNAL_ID_CARDS, nikitaExternalId)
                .then()
                .log().all()
                .assertThat()
                .contentType(ContentType.JSON)
                .extract()
                .as(AllCardsResponse.class);

        // assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void findCardsByPassengerExternalId_shouldReturnNotFoundCode() {
        // arrange
        UUID notExistedPassengerExternalId = DataUtil.NOT_EXISTED_EXTERNAL_ID;

        // act
        ErrorResponse actual = when()
                .get(BASE_URL + PASSENGER_EXTERNAL_ID_CARDS, notExistedPassengerExternalId)
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
    void findCardsByPassengerExternalId_shouldReturnEmptyResponse() {
        // arrange
        UUID eugenExternalId = EntitiesUtil.EUGEN_EXTERNAL_ID;
        var expected = new AllCardsResponse(Collections.emptyList());

        // act
        AllCardsResponse actual = when()
                .get(BASE_URL + PASSENGER_EXTERNAL_ID_CARDS, eugenExternalId)
                .then()
                .log().all()
                .assertThat()
                .contentType(ContentType.JSON)
                .extract()
                .as(AllCardsResponse.class);

        // assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void findDefaultCardByPassengerExternalId_shouldReturnExpectedResponse() {
        // arrange
        UUID saveliyExternalId = EntitiesUtil.SAVELIY_EXTERNAL_ID;
        Card card = cardRepo.findDefaultCardByPassengerExternalId(saveliyExternalId).get();
        var expected = new DefaultCardResponse(card.getExternalId());

        // act
        DefaultCardResponse actual = when()
                .get(BASE_URL + PASSENGER_EXTERNAL_ID_CARDS_DEFAULT, saveliyExternalId)
                .then()
                .log().all()
                .assertThat()
                .contentType(ContentType.JSON)
                .extract()
                .as(DefaultCardResponse.class);

        // assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void findDefaultCardByPassengerExternalId_whenPassengerNotExist_shouldReturnNotFoundCode() {
        // arrange
        UUID notExistedExternalId = DataUtil.NOT_EXISTED_EXTERNAL_ID;

        // act
        ErrorResponse actual = when()
                .get(BASE_URL + PASSENGER_EXTERNAL_ID_CARDS_DEFAULT, notExistedExternalId)
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
    void findDefaultCardByPassengerExternalId_whenCardNotExist_shouldReturnNotFoundCode() {
        // arrange
        UUID eugenExternalId = EntitiesUtil.EUGEN_EXTERNAL_ID;

        // act
        ErrorResponse actual = when()
                .get(BASE_URL + PASSENGER_EXTERNAL_ID_CARDS_DEFAULT, eugenExternalId)
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
    void createCard_shouldReturnCreatedCode() {
        // arrange
        UUID nikitaExternalId = EntitiesUtil.NIKITA_EXTERNAL_ID;
        CardRegistrationDto requestBody = new CardRegistrationDto("5532331131234421");

        // act & assert
        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .post(BASE_URL + PASSENGER_EXTERNAL_ID_CARDS, nikitaExternalId)
                .then()
                .log().all()
                .assertThat()
                .contentType(ContentType.JSON)
                .statusCode(HttpStatus.CREATED.value());
    }

    @Test
    void createCard_whenCardAlreadyExistForPassenger_shouldReturnBadRequestCode() {
        // arrange
        UUID nikitaExternalId = EntitiesUtil.NIKITA_EXTERNAL_ID;
        CardRegistrationDto existedCardRequest = new CardRegistrationDto("5532332131234421");

        // act
        ErrorResponse actual = given()
                .contentType(ContentType.JSON)
                .body(existedCardRequest)
                .post(BASE_URL + PASSENGER_EXTERNAL_ID_CARDS, nikitaExternalId)
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
    void createCard_whenCardContainsInDb_shouldNotSaveDuplicateEntity() {
        // arrange
        UUID nikitaExternalId = EntitiesUtil.NIKITA_EXTERNAL_ID;
        CardRegistrationDto requestBody = new CardRegistrationDto("5532332431234421");
        long expectedCardAmount = cardRepo.findAll().size();

        // act
        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .post(BASE_URL + PASSENGER_EXTERNAL_ID_CARDS, nikitaExternalId)
                .then()
                .log().all()
                .assertThat()
                .contentType(ContentType.JSON);
        long actualCardAmount = cardRepo.findAll().size();

        // assert
        assertThat(actualCardAmount).isEqualTo(expectedCardAmount);
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("invalidCardCreatesForBadRequest")
    void updatePassenger_shouldReturnBadRequestCode(CardRegistrationDto request) {
        // arrange
        UUID nikitaExternalId = EntitiesUtil.NIKITA_EXTERNAL_ID;

        // act
        ErrorResponse actual = given()
                .contentType(ContentType.JSON)
                .body(request)
                .post(BASE_URL + PASSENGER_EXTERNAL_ID_CARDS, nikitaExternalId)
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
    void deletePassengerCard_shouldReturnNoContentCode() {
        // arrange
        UUID nikitaExternalId = EntitiesUtil.NIKITA_EXTERNAL_ID;
        UUID aCardExternalId = EntitiesUtil.A_CARD_EXTERNAL_ID;

        // act & assert
        when()
                .delete(BASE_URL + PASSENGER_EXTERNAL_ID_CARDS_CARD_EXTERNAL_ID, nikitaExternalId, aCardExternalId)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void deletePassengerCard_shouldReturnNotFoundCode() {
        // arrange
        UUID notExistedExternalId = DataUtil.NOT_EXISTED_EXTERNAL_ID;
        UUID aCardExternalId = EntitiesUtil.A_CARD_EXTERNAL_ID;

        // act & assert
        when()
                .delete(BASE_URL + PASSENGER_EXTERNAL_ID_CARDS_CARD_EXTERNAL_ID, notExistedExternalId, aCardExternalId)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    private static Stream<CardRegistrationDto> invalidCardCreatesForBadRequest() {
        return Stream.of(
                new CardRegistrationDto(""),
                new CardRegistrationDto(" "),
                new CardRegistrationDto("qwerty"),
                new CardRegistrationDto("11111111111111111111111"),
                new CardRegistrationDto("9999999999999999"),
                new CardRegistrationDto("5532-3324-3123-4421"),
                new CardRegistrationDto("5532332431234421Q"),
                new CardRegistrationDto("5532.3324.3123.4421"),
                new CardRegistrationDto("1234567891234567")
        );
    }
}
