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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

import static com.example.ridesservice.util.ApiRoutesConstants.*;

@RestController
@RequestMapping(PUBLIC_API_V1_RIDES)
@RequiredArgsConstructor
public class RideController {

    private final RideService rideService;

    @GetMapping("{rideExternalId}/passengers/{passengerExternalId}")
    @ResponseStatus(HttpStatus.OK)
    public GetRideResponse findRideByPassengerExternalId(@PathVariable UUID passengerExternalId,
                                                         @PathVariable UUID rideExternalId) {
        return rideService.findRideByPassengerExternalId(passengerExternalId, rideExternalId);
    }

    @GetMapping("passengers/{passengerExternalId}")
    @ResponseStatus(HttpStatus.OK)
    public AllRidesResponse findAllPassengerRides(
            @PathVariable UUID passengerExternalId,
            @PageableDefault(size = 5, sort = "rideCreatedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return rideService.findAllPassengerRides(passengerExternalId, pageable);
    }

    @PostMapping("passengers/{passengerExternalId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateRideResponse bookRide(@PathVariable UUID passengerExternalId,
                                       @RequestBody @Valid CreateRideRequest createRideDto) {
        return rideService.bookRide(passengerExternalId, createRideDto);
    }

    @PostMapping("{rideExternalId}/drivers/{driverExternalId}/accept")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public AcceptRideResponse acceptRide(@PathVariable UUID driverExternalId,
                                         @PathVariable UUID rideExternalId) {

        return rideService.acceptRide(driverExternalId, rideExternalId);
    }

    @PatchMapping("{rideExternalId}/drivers/{driverExternalId}/start")
    @ResponseStatus(HttpStatus.OK)
    public StartRideResponse startRide(@PathVariable UUID driverExternalId,
                                       @PathVariable UUID rideExternalId) {
        return rideService.startRide(driverExternalId, rideExternalId);
    }

    @PatchMapping("{rideExternalId}/drivers/{driverExternalId}/finish")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public FinishRideResponse finishRide(@PathVariable UUID driverExternalId,
                                         @PathVariable UUID rideExternalId) {
        return rideService.finishRide(driverExternalId, rideExternalId);
    }
}
