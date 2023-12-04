package com.example.driverservice.repository;

import com.example.driverservice.model.entity.Driver;
import com.example.driverservice.model.enums.DriverStatus;
import com.example.driverservice.model.projections.DriverView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    Optional<Driver> findByExternalId(UUID externalId);

    @Query(value = """
                        
            SELECT
                d.external_id as externalId,
                d.first_name as firstName,
                d.last_name as lastName,
                d.phone as phone,
                d.rate as rate,
                d.created_at as createdAt
            FROM
                drivers AS d 
                 
            """, nativeQuery = true)
    Page<DriverView> findAllDriversViews(Pageable pageable);

    Optional<Driver> findDriverByDriverStatus(DriverStatus driverStatus);
}