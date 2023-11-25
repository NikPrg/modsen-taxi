package com.example.passengerservice.repository;

import com.example.passengerservice.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    @Query("SELECT c FROM Card c JOIN FETCH c.passengers p WHERE c.externalId = :externalId")
    Optional<Card> findByExternalId(UUID externalId);

    @Query("SELECT c FROM Card c JOIN FETCH c.passengers p WHERE c.number = :number")
    Optional<Card> findByNumber(String number);
}
