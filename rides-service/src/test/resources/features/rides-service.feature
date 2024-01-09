Feature: Rides Service
  Scenario: Finding a ride by existing rideExternalId and passengerExternalId
    Given An external ride identifier: 54bb2de9-c518-488a-9898-015faa6dee3c, that exist and passengerExternalId: 45bb2de9-c518-488a-9898-015faa6dee3c, that exist
    When A rideExternalId: 54bb2de9-c518-488a-9898-015faa6dee3c and passengerExternalId: 45bb2de9-c518-488a-9898-015faa6dee3c, is passed to the findRideMethod
    Then The response should contain details of the ride with rideExternalId: 54bb2de9-c518-488a-9898-015faa6dee3c, and passengerExternalId: 45bb2de9-c518-488a-9898-015faa6dee3c

  Scenario: Finding a ride by non-existing rideExternalId and passengerExternalId
    Given An external ride identifier: 54bb2de9-c518-488a-9898-015faa6dee3c, that doesn't exist and passengerExternalId: 45bb2de9-c518-488a-9898-015faa6dee3c, that exist
    When A rideExternalId: 54bb2de9-c518-488a-9898-015faa6dee3c and passengerExternalId: 45bb2de9-c518-488a-9898-015faa6dee3c, is passed to the findRideMethod
    Then The PassengerRideNotFoundException with the message containing rideExternalId: 54bb2de9-c518-488a-9898-015faa6dee3c, and passengerExternalId: 45bb2de9-c518-488a-9898-015faa6dee3c, should be thrown

  Scenario: Booking a new ride
    Given An external passenger identifier: 45bb2de9-c518-488a-9898-015faa6dee3c, that exist, pickUpAddress: "PickUp address", destinationAddress: "Destination address"
    When A create request with pickUpAddress: "PickUp address", destinationAddress: "Destination address", and passengerExternalId: 45bb2de9-c518-488a-9898-015faa6dee3c, passed to bookRide method
    Then The response should contain details of the created ride with pickUpAddress: "PickUp address", destinationAddress: "Destination address", passengerExternalId: 45bb2de9-c518-488a-9898-015faa6dee3c

  Scenario: Accepting a ride
    Given Driver with externalId: 77bb2de9-c518-488a-9898-015faa6dee3c assigned to a ride with externalId: 54bb2de9-c518-488a-9898-015faa6dee3c
    When An external driver identifier: 77bb2de9-c518-488a-9898-015faa6dee3c and rideExternalId: 54bb2de9-c518-488a-9898-015faa6dee3c is passed to acceptRideMethod
    Then Ride status should be changed to "ACCEPTED"