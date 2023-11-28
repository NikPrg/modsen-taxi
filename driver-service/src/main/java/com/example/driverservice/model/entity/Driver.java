package com.example.driverservice.model.entity;

import com.example.driverservice.model.enums.DriverStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import jakarta.persistence.Index;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "drivers", indexes = @Index(name = "driver_eid_index", columnList = "externalId"))
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "externalId")
public class Driver {
    @Id
    @SequenceGenerator(name = "driver_generator", allocationSize = 5)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "driver_generator")
    private Long id;

    private UUID externalId;

    private String firstName;

    private String lastName;

    private String phone;

    private Double rate;

    @Enumerated(EnumType.STRING)
    private DriverStatus driverStatus;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "driver", cascade = CascadeType.ALL)
    private Car car;
}