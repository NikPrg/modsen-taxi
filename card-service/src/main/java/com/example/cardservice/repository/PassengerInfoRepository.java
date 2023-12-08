package com.example.cardservice.repository;

import com.example.cardservice.model.PassengerInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PassengerInfoRepository extends JpaRepository<PassengerInfo, Long> {

    Optional<PassengerInfo> findByExternalId(UUID externalId);
}
