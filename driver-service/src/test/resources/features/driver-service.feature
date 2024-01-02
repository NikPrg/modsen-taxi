Feature: Driver Service
  Scenario: Finding driver by existing externalId
    Given An external driver identifier: 54bb2de9-c518-488a-9898-015faa6dee3c, that exist
    When An external driver identifier: 54bb2de9-c518-488a-9898-015faa6dee3c, is passed to the findDriverByExternalId method
    Then The response should contain details of the driver with driverExternalId: 54bb2de9-c518-488a-9898-015faa6dee3c

  Scenario: Finding driver by not existing externalId
    Given An external driver identifier: 54bb2de9-c518-488a-9898-015faa6dee3c, that doesn't exist
    When An external driver identifier: 54bb2de9-c518-488a-9898-015faa6dee3c, is passed to the findDriverByExternalId method
    Then The EntityNotFoundException with the message containing driverExternalId: 54bb2de9-c518-488a-9898-015faa6dee3c, should be thrown

  Scenario: Creating a new driver
    Given A driver creation request
    When A create request is passed to the createDriver method
    Then The response should contain details of the created driver

  Scenario: Deleting a driver by existing externalId
    Given An external driver identifier: 54bb2de9-c518-488a-9898-015faa6dee3c, that exist
    When The driverExternalId: 54bb2de9-c518-488a-9898-015faa6dee3c, is passed to the deleteDriver method
    Then The driver with externalId: 54bb2de9-c518-488a-9898-015faa6dee3c, should be deleted from the database

  Scenario: Deleting a driver by non-existing externalId
    Given An external driver identifier: 54bb2de9-c518-488a-9898-015faa6dee3c, that doesn't exist
    When The driverExternalId: 54bb2de9-c518-488a-9898-015faa6dee3c, is passed to the deleteDriver method
    Then The EntityNotFoundException with the message containing driverExternalId: 54bb2de9-c518-488a-9898-015faa6dee3c, should be thrown
