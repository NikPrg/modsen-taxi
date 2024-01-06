package com.example.e2eservice.feign.client;

import com.example.e2eservice.dto.request.CreateRideRequest;
import com.example.e2eservice.feign.response.rides.CreateRideResponse;
import com.example.e2eservice.feign.response.rides.FinishRideResponse;
import com.example.e2eservice.feign.response.rides.GetRideResponse;
import com.example.e2eservice.feign.response.rides.StartRideResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name = "${app.api.feign.clients.rides.name}", url = "${app.api.feign.clients.rides.base-url}")
public interface RideClient {

    @PostMapping("${app.api.feign.clients.rides.routes.book-ride}")
    CreateRideResponse bookRide(@PathVariable UUID passengerExternalId,
                                @RequestBody CreateRideRequest createRideDto);

    @GetMapping("${app.api.feign.clients.rides.routes.find-ride-by-passenger-external-id}")
    GetRideResponse findRideByPassengerExternalId(@PathVariable UUID passengerExternalId,
                                                  @PathVariable UUID rideExternalId);

    @PutMapping("${app.api.feign.clients.rides.routes.start-ride}")
    StartRideResponse startRide(@PathVariable UUID driverExternalId,
                                @PathVariable UUID rideExternalId);

    @PutMapping("${app.api.feign.clients.rides.routes.finish-ride}")
    FinishRideResponse finishRide(@PathVariable UUID driverExternalId,
                                  @PathVariable UUID rideExternalId);

}
