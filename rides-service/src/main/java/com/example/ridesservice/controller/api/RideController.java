package com.example.ridesservice.controller.api;

import com.example.ridesservice.dto.response.GetRideResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@RestController
@RequestMapping("public/api/v1")
@RequiredArgsConstructor
public class RideController {
    private final RideService rideService;

    @GetMapping("/passengers/{passengerExternalId}/rides/{rideExternalId}")
    @ResponseStatus(HttpStatus.OK)
    public GetRideResponseDto findRideByPassengerExternalId(@PathVariable UUID passengerExternalId,
                                                            @PathVariable UUID rideExternalId) {
        return rideService.findRideByPassengerExternalId(passengerExternalId, rideExternalId);
    }


    @PostMapping("/passengers/{passengerExternalId}/rides")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateRideResponseDto bookRide(@PathVariable UUID passengerExternalId,
                                          @RequestBody CreateRideRequestDto createRideDto) {
        return rideService.bookRide(passengerExternalId, createRideDto);
    }


    @PostMapping("/drivers/{driverExternalId}/rides/accept")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public DriverInfoResponseDto acceptRide(@PathVariable UUID driverExternalId,
                                            @RequestBody DriverInfoRequestDto driverRequest) {
        return rideService.acceptRide(driverExternalId, driverRequest);
    }

    @PatchMapping("/drivers/{driverExternalId}/rides/start")
    @ResponseStatus(HttpStatus.OK)
    public RideResponseDto startRide(@PathVariable UUID driverExternalId,
                                     @RequestBody DriverInfoRequestDto driverRequest) {
        return rideService.startRide(driverExternalId, driverRequest);
    }

    @PatchMapping("/drivers/{driverExternalId}/rides/finish")
    @ResponseStatus(HttpStatus.OK)
    public RideResponseDto finishRide(@PathVariable UUID driverExternalId,
                                      @RequestBody DriverInfoRequest driverRequest) {
        return rideService.finishRide(driverExternalId, driverRequest);
    }


}
