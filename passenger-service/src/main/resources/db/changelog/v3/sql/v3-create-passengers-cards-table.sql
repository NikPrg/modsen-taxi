CREATE TABLE passengers_cards
(
    passenger_id    bigint                not null
        constraint "passenger-fk"
            references passengers,
    card_id         bigint                not null
        constraint "card-fk"
            references cards,
    used_as_default boolean default false not null,
    created_at      timestamp,
    constraint pk_passenger_card
        primary key (card_id, passenger_id)
);