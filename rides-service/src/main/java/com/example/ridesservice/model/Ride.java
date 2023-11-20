package com.example.ridesservice.model;

import com.example.ridesservice.model.enums.PaymentMethod;
import com.example.ridesservice.model.enums.RideStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "rides")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "externalId")
@Builder
public class Ride {

    @Id
    @SequenceGenerator(name = "ride_generator", allocationSize = 10)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ride_generator")
    private Long id;

    private UUID externalId;

    private UUID passengerExternalId;

    private String pickUpAddress;

    private String destinationAddress;

    private Double rideCost;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private RideStatus rideStatus;

    private LocalTime rideStartedAt;

    private LocalTime rideFinishedAt;

    @CreationTimestamp
    private LocalDateTime rideCreatedAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "driver_id", referencedColumnName = "id")
    private DriverInfo driver;
}
