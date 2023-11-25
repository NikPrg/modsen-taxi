package com.example.driverservice.repository;

import com.example.driverservice.model.entity.Car;
import com.example.driverservice.model.projections.CarView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
    Optional<Car> findByExternalId(UUID externalId);

    @Query(value = """
                        
            SELECT
                c.external_id as externalId,
                c.license_plate as licensePlate,
                c.model as model,
                c.color as color,
                c.created_at as createdAt
            FROM
                cars AS c 
                 
            """, nativeQuery = true)
    Page<CarView> findAllCarsViews(Pageable pageable);

}