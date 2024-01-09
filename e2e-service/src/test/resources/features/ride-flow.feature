Feature: Ride Flow
  Scenario: Booking ride by a passenger
    Given An existed passenger with externalId: dd36ffce-6315-43b0-9a33-baf65aef40c8, and ride request with pickUpAddress: "PickUp address" and destinationAddress: "Destination address"
    When A passenger with externalId: dd36ffce-6315-43b0-9a33-baf65aef40c8, sends this request to the book ride endpoint
    Then A passenger should get details of ride order with status "INITIATED"
    And In a few seconds, the ride's status is expected to switch to "ACCEPTED"
    And A driver should assign to the ride
    And A driver's status should be changed to "TOWARDS_PASSENGER"

  Scenario: A driver starts the ride
    Given The ride has status "ACCEPTED"
    When A driver starts the ride
    Then A ride's status should be changed to "STARTED"

  Scenario: A driver finishes the ride
    Given The ride has status "STARTED"
    When A driver finishes the ride
    Then A ride's status should be changed to "FINISHED"
    And A driver's status should be changed to "AVAILABLE"

  Scenario: A passenger views his rides history
    Given A passenger has that finished ride
    Then A passenger ride not null