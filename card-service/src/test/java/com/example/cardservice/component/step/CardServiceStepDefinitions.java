package com.example.cardservice.component.step;

import com.example.cardservice.amqp.handler.SendRequestHandler;
import com.example.cardservice.amqp.message.CardInfoMessage;
import com.example.cardservice.dto.model.CardDto;
import com.example.cardservice.dto.request.CardRegistrationDto;
import com.example.cardservice.dto.response.AllCardsResponse;
import com.example.cardservice.dto.response.CreateCardResponse;
import com.example.cardservice.exception.EntityAlreadyExistException;
import com.example.cardservice.mapper.CardMapper;
import com.example.cardservice.model.Card;
import com.example.cardservice.model.PassengerCard;
import com.example.cardservice.model.PassengerInfo;
import com.example.cardservice.repository.CardRepository;
import com.example.cardservice.repository.PassengerInfoRepository;
import com.example.cardservice.service.CardService;
import com.example.cardservice.service.impl.CardServiceImpl;
import com.example.cardservice.util.DataUtil;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.example.cardservice.util.ExceptionMessagesConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.doNothing;

public class CardServiceStepDefinitions {
    private CardRepository cardRepo;
    private EntityManager entityManager;
    private CardMapper cardMapper;
    private PassengerInfoRepository passengerInfoRepo;
    private SendRequestHandler sendRequestHandler;
    private CardService cardService;

    private AllCardsResponse allCardsResponse;
    private CreateCardResponse createCardResponse;
    private Exception exception;

    @Before
    public void setUp() {
        this.cardRepo = mock(CardRepository.class);
        this.entityManager = mock(EntityManager.class);
        this.cardMapper = mock(CardMapper.class);
        this.sendRequestHandler = mock(SendRequestHandler.class);
        this.passengerInfoRepo = mock(PassengerInfoRepository.class);
        this.cardService = new CardServiceImpl(cardRepo, entityManager, cardMapper, passengerInfoRepo, sendRequestHandler);
    }

    @Given("An external passenger identifier: {uuid}, that exist")
    public void passengerExternalIdExit(UUID passengerExternalId) {
        PassengerInfo passengerInfo = DataUtil.defaultPassengerInfoWithCard();
        Card card = DataUtil.defaultCard();
        CardDto cardDto = DataUtil.defaultCardDto();
        AllCardsResponse expected = DataUtil.defaultAllCardsResponse();

        doReturn(Optional.of(passengerInfo))
                .when(passengerInfoRepo)
                .findByExternalIdFetch(passengerExternalId);
        doReturn(cardDto)
                .when(cardMapper)
                .toCardDto(card);

        AllCardsResponse actual = cardService.findCardsByPassengerExternalId(passengerExternalId);

        assertThat(actual).isEqualTo(expected);
    }

    @When("An external passenger identifier: {uuid}, is passed to the findCardsByPassengerExternalId method")
    public void externalIdPassedToFindCardsByPassengerExternalIdMethod(UUID passengerExternalId) {
        try {
            allCardsResponse = cardService.findCardsByPassengerExternalId(passengerExternalId);
        } catch (EntityNotFoundException e) {
            exception = e;
        }
    }

    @Then("The response should contain details of the cards with passengerExternalId: {uuid}")
    public void responseContainsCardDetails(UUID passengerExternalId) {
        var passengerInfo = passengerInfoRepo.findByExternalIdFetch(passengerExternalId).get();
        var expected = new AllCardsResponse(passengerInfo.getCards().stream()
                .map(PassengerCard::getCard)
                .map(cardMapper::toCardDto)
                .collect(Collectors.toList()));

        assertThat(allCardsResponse).isEqualTo(expected);
    }

    @Given("An external passenger identifier: {uuid}, that doesn't exist")
    public void passengerExternalIdNotExit(UUID passengerExternalId) {
        var actual = passengerInfoRepo.findByExternalIdFetch(passengerExternalId);
        assertThat(actual).isEmpty();
    }

    @Then("The EntityNotFoundException with the message containing passengerExternalId: {uuid}, should be thrown during cards search")
    public void passengerNotFoundExceptionThrown(UUID passengerExternalId) {
        var expected = String.format(PASSENGER_NOT_FOUND_EXCEPTION_MESSAGE, passengerExternalId);
        var actual = exception.getMessage();

        assertThat(actual).isEqualTo(expected);
    }

    @Given("A card creation request with number: {string} and passengerExternalId: {uuid}, that exist")
    public void cardWithNumberAndPassengerExternalIdExist(String cardNumber, UUID passengerExternalId) {
        PassengerInfo passengerInfo = DataUtil.initPassengerInfo();
        Card card = DataUtil.defaultCard();
        CardInfoMessage message = DataUtil.defaultCardInfoMessage();

        doReturn(Optional.of(passengerInfo))
                .when(passengerInfoRepo)
                .findByExternalId(passengerExternalId);
        doReturn(Optional.of(card))
                .when(cardRepo)
                .findByNumber(cardNumber);
        doReturn(Boolean.FALSE)
                .when(cardRepo)
                .existsCardForPassenger(DataUtil.CARD_EXTERNAL_ID, passengerExternalId);
        doNothing()
                .when(sendRequestHandler)
                .sendNewCardInfoToKafka(message);

        assertThat(passengerInfoRepo.findByExternalId(passengerExternalId)).isPresent();
    }

    @When("A creation request with number: {string} and passengerExternalId: {uuid}, is passed to the createCard method")
    public void cardNumberAndPassengerExtIdPassedToCreateCardMethod(String cardNumber, UUID passengerExternalId) {
        CardRegistrationDto request = new CardRegistrationDto(cardNumber);

        try {
            createCardResponse = cardService.create(request, passengerExternalId);
        } catch (EntityNotFoundException | EntityAlreadyExistException e) {
            exception = e;
        }
    }

    @Then("The response should contain details of the created card")
    public void responseContainsCreatedCarDetails() {
        CreateCardResponse expected = DataUtil.defaultCreateCardResponse();
        assertThat(createCardResponse).isEqualTo(expected);
    }

    @Given("A card creation request with number: {string} and passengerExternalId: {uuid}, that doesn't exist")
    public void cardWithNumberAndPassengerExternalIdNotExist(String cardNumber, UUID passengerExternalId) {
        doReturn(Optional.empty())
                .when(passengerInfoRepo)
                .findByExternalId(passengerExternalId);

        assertThat(cardNumber).isNotBlank();
    }

    @Then("The EntityNotFoundException with the message containing passengerExternalId: {uuid}, should be thrown during card creation")
    public void passengerNotFoundExceptionThrownDuringCardCreation(UUID passengerExternalId) {
        var expected = String.format(PASSENGER_NOT_FOUND_EXCEPTION_MESSAGE, passengerExternalId);
        var actual = exception.getMessage();

        assertThat(actual).isEqualTo(expected);
    }

    @Given("A card creation request with number: {string} that already for passengerExternalId: {uuid}")
    public void cardWithNumberAlreadyExistForPassengerExternalId(String cardNumber, UUID passengerExternalId) {
        PassengerInfo passengerInfo = new PassengerInfo(1L, passengerExternalId, null);
        Card card = DataUtil.defaultCard();

        doReturn(Optional.of(passengerInfo))
                .when(passengerInfoRepo)
                .findByExternalId(passengerExternalId);
        doReturn(Optional.of(card))
                .when(cardRepo)
                .findByNumber(cardNumber);
        doReturn(Boolean.TRUE)
                .when(cardRepo)
                .existsCardForPassenger(DataUtil.CARD_EXTERNAL_ID, passengerExternalId);

        assertThat(passengerInfoRepo.findByExternalId(passengerExternalId)).isPresent();
    }

    @Then("The EntityAlreadyExistException with the message containing passengerExternalId: {uuid}, should be thrown during card creation")
    public void cardAlreadyExistForPassengerExceptionThrown(UUID passengerExternalId) {
        var expected = String.format(CARD_ALREADY_EXIST_PASSENGER_EXCEPTION_MESSAGE, passengerExternalId);
        var actual = exception.getMessage();

        assertThat(actual).isEqualTo(expected);
    }

    @Given("An existed external passenger identifier: {uuid}, and not existed external card identifier: {uuid}")
    public void passengerExternalIdExistAndCardExternalIdExist(UUID passengerExternalId, UUID cardExternalId) {
        doReturn(Optional.empty())
                .when(passengerInfoRepo)
                .findByExternalIdAndCardExternalId(passengerExternalId, cardExternalId);

        assertThat(passengerInfoRepo.findByExternalIdAndCardExternalId(passengerExternalId, cardExternalId)).isEmpty();
    }

    @When("A passengerExternalId: {uuid} and cardExternalId: {uuid}, is passed to the deletePassengerCardMethod")
    public void passengerExternalIdAndCardExternalIdPassedToDeleteCardMethod(UUID passengerExternalId, UUID cardExternalId) {
        try {
            cardService.deletePassengerCard(passengerExternalId, cardExternalId);
        } catch (EntityNotFoundException e) {
            exception = e;
        }
    }

    @Then("The EntityNotFoundException with the message containing passengerExternalId: {uuid} and cardExternalId: {uuid}, should be thrown during card removing")
    public void passengerNotFoundExceptionThrown(UUID passengerExternalId, UUID cardExternalId) {
        var expected = String.format(PASSENGER_WITH_SPECIFIED_CARD_NOT_FOUND_EXCEPTION_MESSAGE, passengerExternalId, cardExternalId);
        var actual = exception.getMessage();

        assertThat(actual).isEqualTo(expected);
    }
}
