Feature: Passenger Service
  Scenario: Finding passenger by existing externalId
    Given An external passenger identifier: 54bb2de9-c518-488a-9898-015faa6dee3c, that exist
    When An external passenger identifier: 54bb2de9-c518-488a-9898-015faa6dee3c, is passed to the findPassengerByExternalId method
    Then The response should contain details of the passenger with externalId: 54bb2de9-c518-488a-9898-015faa6dee3c

  Scenario: Finding passenger by not existing externalId
    Given An external passenger identifier: 54bb2de9-c518-488a-9898-015faa6dee3c, that doesn't exist
    When An external passenger identifier: 54bb2de9-c518-488a-9898-015faa6dee3c, is passed to the findPassengerByExternalId method
    Then The EntityNotFoundException with the message containing passengerExternalId: 54bb2de9-c518-488a-9898-015faa6dee3c, should be thrown

  Scenario: Creating a new passenger with unique data
    Given A passenger with phone: "+375259428749", firstName: "Nikita" and lastName: "Qwerty", doesn't exist
    When A create request with firstName: "Nikita", lastName: "Qwerty", phone: "+375259428749" is passed to the signUp method
    Then The response should contain details of the newly created passenger

  Scenario: Creating a new passenger with non-unique phone
    Given A passenger with phone: "+375259428749", that already exist
    When A create request with firstName: "Nikita", lastName: "Qwerty", phone: "+375259428749" is passed to the signUp method
    Then The IllegalArgumentException with the message containing phone: "+375259428749", should be thrown

  Scenario: Finding passenger paymentMethod by existing externalId
    Given An external passenger identifier: 54bb2de9-c518-488a-9898-015faa6dee3c, that exist
    When An external passenger identifier: 54bb2de9-c518-488a-9898-015faa6dee3c, is passed to the findPassengerPaymentMethod method
    Then The response should contain details of the passenger payment method with externalId: 54bb2de9-c518-488a-9898-015faa6dee3c

  Scenario: Finding passenger paymentMethod by not existing externalId
    Given An external passenger identifier: 54bb2de9-c518-488a-9898-015faa6dee3c, that doesn't exist
    When An external passenger identifier: 54bb2de9-c518-488a-9898-015faa6dee3c, is passed to the findPassengerPaymentMethod method
    Then The EntityNotFoundException with the message containing passengerExternalId: 54bb2de9-c518-488a-9898-015faa6dee3c, should be thrown

  Scenario: Deleting a passenger by existing externalId
    Given A passenger with externalId: 54bb2de9-c518-488a-9898-015faa6dee3c, that exist
    When The passengerExternalId: 54bb2de9-c518-488a-9898-015faa6dee3c, is passed to the delete method
    Then The passenger with passengerExternalId: 54bb2de9-c518-488a-9898-015faa6dee3c, should be deleted from the database

  Scenario: Deleting a passenger by non-existing externalId
    Given A passenger with externalId: 54bb2de9-c518-488a-9898-015faa6dee3c, that doesn't exist
    When The passengerExternalId: 54bb2de9-c518-488a-9898-015faa6dee3c, is passed to the delete method
    Then The EntityNotFoundException with the message containing passengerExternalId: 54bb2de9-c518-488a-9898-015faa6dee3c, should be thrown