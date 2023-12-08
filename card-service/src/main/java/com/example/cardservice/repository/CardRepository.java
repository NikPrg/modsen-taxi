package com.example.cardservice.repository;

import com.example.cardservice.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    @Query("SELECT c FROM Card c JOIN FETCH c.passengers p JOIN FETCH p.passenger WHERE c.externalId = :externalId")
    Optional<Card> findByExternalId(UUID externalId);

    @Query("SELECT c FROM Card c JOIN FETCH c.passengers p JOIN FETCH p.passenger WHERE c.number = :number")
    Optional<Card> findByNumber(String number);

    @Query(value = """
                       
            SELECT c
            FROM Card c
            JOIN FETCH c.passengers pc
            WHERE pc.passenger.externalId = :passengerExternalId AND c.externalId = :cardExternalId
                       
            """)
    Optional<Card> existsCardForPassenger(@Param("cardExternalId") UUID cardExternalId, @Param("passengerExternalId") UUID passengerExternalId);

}
