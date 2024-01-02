package com.example.ridesservice.controller.api;

import com.example.ridesservice.dto.request.CreateRideRequest;
import com.example.ridesservice.dto.response.ride.*;
import com.example.ridesservice.service.RideService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.example.ridesservice.util.ApiRoutesConstants.*;

@RestController
@RequestMapping(PUBLIC_API_V1_RIDES)
@RequiredArgsConstructor
public class RideController {

    private final RideService rideService;

    @GetMapping(RIDE_EXTERNAL_ID_PASSENGERS_PASSENGER_EXTERNAL_ID_ENDPOINT)
    @ResponseStatus(HttpStatus.OK)
    public GetRideResponse findRideByPassengerExternalId(@PathVariable UUID passengerExternalId,
                                                         @PathVariable UUID rideExternalId) {
        return rideService.findRideByPassengerExternalId(passengerExternalId, rideExternalId);
    }

    @GetMapping(PASSENGERS_PASSENGER_EXTERNAL_ID_ENDPOINT)
    @ResponseStatus(HttpStatus.OK)
    public AllRidesResponse findAllPassengerRides(
            @PathVariable UUID passengerExternalId,
            @PageableDefault(size = 5, sort = "rideCreatedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return rideService.findAllPassengerRides(passengerExternalId, pageable);
    }

    @PostMapping(PASSENGERS_PASSENGER_EXTERNAL_ID_ENDPOINT)
    @ResponseStatus(HttpStatus.CREATED)
    public CreateRideResponse bookRide(@PathVariable UUID passengerExternalId,
                                       @RequestBody @Valid CreateRideRequest createRideDto) {
        return rideService.bookRide(passengerExternalId, createRideDto);
    }

    @PutMapping(RIDE_EXTERNAL_ID_DRIVERS_DRIVER_EXTERNAL_ID_ACCEPT_ENDPOINT)
    @ResponseStatus(HttpStatus.OK)
    public AcceptRideResponse acceptRide(@PathVariable UUID driverExternalId,
                                         @PathVariable UUID rideExternalId) {

        return rideService.acceptRide(driverExternalId, rideExternalId);
    }

    @PutMapping(RIDE_EXTERNAL_ID_DRIVERS_DRIVER_EXTERNAL_ID_START_ENDPOINT)
    @ResponseStatus(HttpStatus.OK)
    public StartRideResponse startRide(@PathVariable UUID driverExternalId,
                                       @PathVariable UUID rideExternalId) {
        return rideService.startRide(driverExternalId, rideExternalId);
    }

    @PutMapping(RIDE_EXTERNAL_ID_DRIVERS_DRIVER_EXTERNAL_ID_FINISH_ENDPOINT)
    @ResponseStatus(HttpStatus.OK)
    public FinishRideResponse finishRide(@PathVariable UUID driverExternalId,
                                         @PathVariable UUID rideExternalId) {
        return rideService.finishRide(driverExternalId, rideExternalId);
    }
}
