package com.example.passengerservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "cards")
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@EqualsAndHashCode(of = "externalId")
@Builder
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID externalId;

    private String number;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @ManyToMany(mappedBy = "cards", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Passenger> passengers = new HashSet<>();
}
