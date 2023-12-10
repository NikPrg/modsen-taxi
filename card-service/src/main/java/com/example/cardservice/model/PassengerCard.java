package com.example.cardservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;


import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "passengers_cards")
public class PassengerCard {
    @EmbeddedId
    private PassengerCardId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("passengerId")
    private PassengerInfo passenger;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("cardId")
    private Card card;

    private boolean usedAsDefault;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public PassengerCard(PassengerInfo passenger, Card card) {
        this.id = new PassengerCardId(passenger.getId(), card.getId());
        this.passenger = passenger;
        this.card = card;
    }
}
