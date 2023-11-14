package com.example.driverservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "drivers", indexes = @Index(name = "driver_eid_index", columnList = "externalId"))
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
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

    @CreationTimestamp
    private LocalDateTime created_at;

    @OneToOne(mappedBy = "driver")
    private Car car;
}
