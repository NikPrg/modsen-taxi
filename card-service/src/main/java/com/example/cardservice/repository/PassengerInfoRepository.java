package com.example.cardservice.repository;

import com.example.cardservice.model.PassengerInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PassengerInfoRepository extends JpaRepository<PassengerInfo, Long> {
    Optional<PassengerInfo> findByExternalId(UUID externalId);

    @Query("SELECT p FROM PassengerInfo p LEFT JOIN FETCH p.cards c LEFT JOIN FETCH c.card WHERE p.externalId = :externalId")
    Optional<PassengerInfo> findByExternalIdFetch(@Param("externalId") UUID externalId);

    @Query("""
            
            SELECT p
            FROM PassengerInfo p
            JOIN FETCH p.cards pc
            WHERE pc.passenger.externalId = :passengerExternalId AND pc.card.externalId = :cardExternalId
            
            """)
    Optional<PassengerInfo> findByExternalIdAndCardExternalId(@Param("passengerExternalId") UUID passengerExternalId, @Param("cardExternalId") UUID cardExternalId);

    void deleteByExternalId(UUID externalId);

}
