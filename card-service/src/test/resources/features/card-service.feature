Feature: Card Service
  Scenario: Finding cards by existing passengerExternalId
    Given An external passenger identifier: 54bb2de9-c518-488a-9898-015faa6dee3c, that exist
    When An external passenger identifier: 54bb2de9-c518-488a-9898-015faa6dee3c, is passed to the findCardsByPassengerExternalId method
    Then The response should contain details of the cards with passengerExternalId: 54bb2de9-c518-488a-9898-015faa6dee3c

  Scenario: Finding cards by not existing passengerExternalId
    Given An external passenger identifier: 54bb2de9-c518-488a-9898-015faa6dee3c, that doesn't exist
    When An external passenger identifier: 54bb2de9-c518-488a-9898-015faa6dee3c, is passed to the findCardsByPassengerExternalId method
    Then The EntityNotFoundException with the message containing passengerExternalId: 54bb2de9-c518-488a-9898-015faa6dee3c, should be thrown during cards search

  Scenario: Creating a new card for existed passenger
    Given A card creation request with number: "5532332131234421" and passengerExternalId: 54bb2de9-c518-488a-9898-015faa6dee3c, that exist
    When A creation request with number: "5532332131234421" and passengerExternalId: 54bb2de9-c518-488a-9898-015faa6dee3c, is passed to the createCard method
    Then The response should contain details of the created card

  Scenario: Creating a new card for not existed passenger
    Given A card creation request with number: "5532332131234421" and passengerExternalId: 54bb2de9-c518-488a-9898-015faa6dee3c, that doesn't exist
    When A creation request with number: "5532332131234421" and passengerExternalId: 54bb2de9-c518-488a-9898-015faa6dee3c, is passed to the createCard method
    Then The EntityNotFoundException with the message containing passengerExternalId: 54bb2de9-c518-488a-9898-015faa6dee3c, should be thrown during card creation

  Scenario: Creating a new card for existed passenger that already has the same card
    Given A card creation request with number: "5532332131234421" that already for passengerExternalId: 54bb2de9-c518-488a-9898-015faa6dee3c
    When A creation request with number: "5532332131234421" and passengerExternalId: 54bb2de9-c518-488a-9898-015faa6dee3c, is passed to the createCard method
    Then The EntityAlreadyExistException with the message containing passengerExternalId: 54bb2de9-c518-488a-9898-015faa6dee3c, should be thrown during card creation

  Scenario: Deleting not existed card for existed passenger
    Given An existed external passenger identifier: 54bb2de9-c518-488a-9898-015faa6dee3c, and not existed external card identifier: 45bb2de9-c518-488a-9898-015faa6dee3c
    When A passengerExternalId: 54bb2de9-c518-488a-9898-015faa6dee3c and cardExternalId: 45bb2de9-c518-488a-9898-015faa6dee3c, is passed to the deletePassengerCardMethod
    Then The EntityNotFoundException with the message containing passengerExternalId: 54bb2de9-c518-488a-9898-015faa6dee3c and cardExternalId: 45bb2de9-c518-488a-9898-015faa6dee3c, should be thrown during card removing
