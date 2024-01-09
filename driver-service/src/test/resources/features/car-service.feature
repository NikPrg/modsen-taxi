Feature: Car Service
  Scenario: Finding car by existing externalId
    Given An external car identifier: 54bb2de9-c518-488a-9898-015faa6dee3c, that exist
    When An external car identifier: 54bb2de9-c518-488a-9898-015faa6dee3c, is passed to the findCarByExternalId method
    Then The response should contain details of the car with carExternalId: 54bb2de9-c518-488a-9898-015faa6dee3c

  Scenario: Finding car by not existing externalId
    Given An external car identifier: 54bb2de9-c518-488a-9898-015faa6dee3c, that doesn't exist
    When An external car identifier: 54bb2de9-c518-488a-9898-015faa6dee3c, is passed to the findCarByExternalId method
    Then The EntityNotFoundException with the message containing carExternalId: 54bb2de9-c518-488a-9898-015faa6dee3c, should be thrown during car creation

  Scenario: Creating a new car for existed driver
    Given A car creation request with licensePlate: "8628AX-3", model: "Honda", color: "red", and driverExternalId: 54bb2de9-c518-488a-9898-015faa6dee3c, that exist
    When A creation request with licensePlate: "8628AX-3", model: "Honda", color: "red", and driverExternalId: 54bb2de9-c518-488a-9898-015faa6dee3c, is passed to the createCar method
    Then The response should contain details of the created car

  Scenario: Creating a new car for not existed driver
    Given An external driver identifier for car creation: 54bb2de9-c518-488a-9898-015faa6dee3c, that doesn't exist
    When A creation request with licensePlate: "8628AX-3", model: "Honda", color: "red", and driverExternalId: 54bb2de9-c518-488a-9898-015faa6dee3c, is passed to the createCar method
    Then The EntityNotFoundException with the message containing driverExternalId: 54bb2de9-c518-488a-9898-015faa6dee3c, should be thrown during car creation

  Scenario: Creating a new car for driver, that already has a car
    Given An external driver identifier for car creation: 54bb2de9-c518-488a-9898-015faa6dee3c, that already has a car
    When A creation request with licensePlate: "8628AX-3", model: "Honda", color: "red", and driverExternalId: 54bb2de9-c518-488a-9898-015faa6dee3c, is passed to the createCar method
    Then The DriverAlreadyHasCarException with the message containing driverExternalId: 54bb2de9-c518-488a-9898-015faa6dee3c, should be thrown during car creation

  Scenario: Deleting existed car for existed driver
    Given An existed external driver identifier: 54bb2de9-c518-488a-9898-015faa6dee3c, and existed external car identifier: 45bb2de9-c518-488a-9898-015faa6dee3c
    When A driverExternalId: 54bb2de9-c518-488a-9898-015faa6dee3c and carExternalId: 45bb2de9-c518-488a-9898-015faa6dee3c, is passed to the deleteDriverCarMethod
    Then The car with externalId: 45bb2de9-c518-488a-9898-015faa6dee3c, should be deleted from the database

  Scenario: Deleting existed car for not existed driver
    Given A not existed external driver identifier: 54bb2de9-c518-488a-9898-015faa6dee3c, and existed external car identifier: 45bb2de9-c518-488a-9898-015faa6dee3c
    When A driverExternalId: 54bb2de9-c518-488a-9898-015faa6dee3c and carExternalId: 45bb2de9-c518-488a-9898-015faa6dee3c, is passed to the deleteDriverCarMethod
    Then The EntityNotFoundException with the message containing driverExternalId: 54bb2de9-c518-488a-9898-015faa6dee3c, should be thrown during car removing

  Scenario: Deleting not existed car for existed driver
    Given An existed external driver identifier: 54bb2de9-c518-488a-9898-015faa6dee3c, and not existed external car identifier: 45bb2de9-c518-488a-9898-015faa6dee3c
    When A driverExternalId: 54bb2de9-c518-488a-9898-015faa6dee3c and carExternalId: 45bb2de9-c518-488a-9898-015faa6dee3c, is passed to the deleteDriverCarMethod
    Then The EntityNotFoundException with the message containing carExternalId: 45bb2de9-c518-488a-9898-015faa6dee3c, should be thrown during car removing