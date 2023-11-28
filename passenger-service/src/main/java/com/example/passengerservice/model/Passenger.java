package com.example.passengerservice.model;

import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.Index;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
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
import java.util.*;

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

    @OneToMany(mappedBy = "passenger", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<PassengerCard> cards = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    public void addCard(Card card) {
        PassengerCard passengerCard = new PassengerCard(this, card);
        cards.add(passengerCard);
        card.getPassengers().add(passengerCard);
    }

    public void removeCard(Card card) {
       for (Iterator<PassengerCard> iterator = cards.iterator(); iterator.hasNext(); ){
           PassengerCard passengerCard = iterator.next();

           if(passengerCard.getPassenger().equals(this) && passengerCard.getCard().equals(card)){
               iterator.remove();
               passengerCard.getCard().getPassengers().remove(passengerCard);
               passengerCard.setPassenger(null);
               passengerCard.setCard(null);
           }
       }
    }

}

