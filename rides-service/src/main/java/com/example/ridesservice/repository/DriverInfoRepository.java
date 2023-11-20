package com.example.ridesservice.repository;

import com.example.ridesservice.model.DriverInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverInfoRepository extends JpaRepository<DriverInfo, Long> {
}
