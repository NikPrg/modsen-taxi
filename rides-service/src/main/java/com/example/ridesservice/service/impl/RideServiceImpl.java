package com.example.ridesservice.service.impl;

import com.example.ridesservice.dto.response.GetRideResponseDto;
import com.example.ridesservice.repository.RideRepository;
import com.example.ridesservice.service.RideService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RideServiceImpl implements RideService {

    private final RideRepository rideRepo;
    private final RideMapper rideMapper;

    private final WebClient webClient;

    public GetRideResponseDto findRideByPassengerExternalId(UUID passengerExternalId, UUID rideExternalId) {
        var ride = rideRepo.findByExternalIdAndPassengerExternalId(rideExternalId, passengerExternalId)
                .orElseThrow(() -> new PassengerRideNotFoundException(PASSENGER_RIDE_NOT_FOUND_EXCEPTION_MESSAGE
                        .formatted(passengerExternalId, rideExternalId)));

        webClient.get()



        return rideMapper.toGetDto(ride);
    }
}
