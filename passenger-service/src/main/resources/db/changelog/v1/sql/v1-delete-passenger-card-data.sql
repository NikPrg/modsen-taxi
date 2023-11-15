DELETE FROM passengers_cards
WHERE (passenger_id, card_id) IN (
    SELECT passenger_id, card_id
    FROM passengers_cards
    LIMIT 15
);