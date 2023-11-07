package com.example.passengerservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
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

    private String email;

    private String password;

    private Byte rate;

    @Enumerated(EnumType.STRING)
    private PaymentMethod defaultPaymentMethod;

    @Embedded
    private Discount discount;

    @OneToMany(mappedBy = "passenger", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Card> cards = new HashSet<>();

    private boolean idDeleted;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public void addCard(Card card){
        card.setPassenger(this);
        cards.add(card);
    }

    public void removeCard(Card card){
        cards.remove(card);
    }
}
