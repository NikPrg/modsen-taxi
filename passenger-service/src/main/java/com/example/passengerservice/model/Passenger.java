package com.example.passengerservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "passengers", indexes = @Index(name = "passenger_eid_index", columnList = "externalId"))
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
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

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "passengers_cards",
            joinColumns = @JoinColumn(name = "passenger_id"),
            inverseJoinColumns = @JoinColumn(name = "card_id")
    )
    @Builder.Default
    private Set<Card> cards = new HashSet<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    public void addCard(Card card) {
        this.cards.add(card);
        card.getPassengers().add(this);
    }

    public void removeCard(Card card) {
        this.cards.remove(card);
        card.getPassengers().remove(this);
    }
}

