package com.example.ridesservice.repository;

import com.example.ridesservice.model.Ride;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RideRepository extends JpaRepository<Ride, Long> {
    Optional<Ride> findByExternalIdAndPassengerExternalId(UUID externalId, UUID passengerExternalId);
}
