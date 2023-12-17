package com.example.paymentservice.repository;

import com.example.paymentservice.model.CardBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CardBalanceRepository extends JpaRepository<CardBalance, Long> {
    boolean existsByCardExternalId(UUID cardExternalId);
    Optional<CardBalance> findByCardExternalId(UUID cardExternalId);
}
