package com.example.passengerservice.model;

import com.example.passengerservice.model.enums.PaymentMethod;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.Index;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import lombok.Builder;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "passengers", indexes = @Index(name = "passenger_eid_index", columnList = "externalId"))
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "externalId")
@Builder
public class Passenger {

    @Id
    @SequenceGenerator(name = "passenger_generator", allocationSize = 5)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "passenger_generator")
    private Long id;

    private UUID externalId;

    private String firstName;

    private String lastName;

    private String phone;

    private Double rate;

    @Enumerated(EnumType.STRING)
    private PaymentMethod defaultPaymentMethod;

    @Embedded
    private Discount discount;

    @CreationTimestamp
    private LocalDateTime createdAt;

}
