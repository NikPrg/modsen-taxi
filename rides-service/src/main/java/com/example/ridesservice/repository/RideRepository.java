package com.example.ridesservice.repository;

import com.example.ridesservice.model.Ride;
import com.example.ridesservice.model.projection.RideView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface RideRepository extends JpaRepository<Ride, Long> {
    Optional<Ride> findByExternalIdAndPassengerExternalId(UUID externalId, UUID passengerExternalId);

    @Query(value = """
                        
            SELECT
                r.external_id as externalId,
                r.pick_up_address as pickUpAddress,
                r.destination_address as destinationAddress,
                r.ride_cost as rideCost,
                r.ride_duration as rideDuration,
                r.ride_created_at as rideCreatedAt
            FROM
                rides AS r 
            WHERE 
                r.passenger_external_id=:passengerExternalId
                 
            """, nativeQuery = true)
    Page<RideView> findAllPassengerRideViews(@Param("passengerExternalId") UUID passengerExternalId, Pageable pageable);

    Optional<Ride> findByExternalId(UUID externalId);

}
