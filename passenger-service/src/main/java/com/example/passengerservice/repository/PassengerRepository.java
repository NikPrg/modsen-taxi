package com.example.passengerservice.repository;

import com.example.passengerservice.model.Passenger;
import com.example.passengerservice.model.projections.PassengerView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Long> {
    @Query("SELECT CASE WHEN COUNT(p) >= 1 THEN true ELSE false END FROM Passenger p WHERE p.phone = :phone")
    boolean existsByPhone(String phone);

    Optional<Passenger> findByExternalId(UUID externalId);

    @Query(value = """
                        
            SELECT
                p.external_id as externalId,
                p.first_name as firstName,
                p.last_name as lastName,
                p.phone as phone,
                p.rate as rate,
                p.created_at as createdAt
            FROM
                passengers AS p
                 
            """, nativeQuery = true)
    Page<PassengerView> findAllPassengersView(Pageable pageable);

}
