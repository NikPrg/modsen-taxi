package com.example.ridesservice.repository;

import com.example.ridesservice.model.DriverInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DriverInfoRepository extends JpaRepository<DriverInfo, Long> {
    Optional<DriverInfo> findByExternalId(UUID externalId);
}
