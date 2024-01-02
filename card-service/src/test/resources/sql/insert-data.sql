INSERT INTO cards
    (id, external_id, number)
VALUES
    (1, '0741a1b0-f7ab-45de-bc83-5c915c254741', '5532332131234421'),
    (2, '9c278f7b-dacc-4f7c-8526-f309682216fa', '5532332231234421'),
    (3, 'f69d426d-d9d7-4f09-94e7-2180d7eaafc7', '5532332331234421'),
    (4, '8fecfa1a-4779-4287-a847-f5a035d5a9e0', '5532332431234421'),
    (5, '6efcb2e3-4e6f-46d5-ab8c-b2ed17d75344', '5532332531234421');

INSERT INTO passengers_info
    (id, external_id)
VALUES
    (1, '55bb3530-96b7-4adb-a9a6-c9062439fed8'),
    (2, 'eea59cd6-0c9a-48de-8f17-263b496d1a5f'),
    (3, '8e5ef207-e829-4bb5-818f-0c7c9ad0f9ed'),
    (4, 'fa020f15-5c63-428e-a3fb-10ab3c333d9e'),
    (5, '2e68c5fc-e524-4dd3-9793-0111d3f86750');

INSERT INTO passengers_cards
    (passenger_id, card_id, used_as_default, created_at)
VALUES
    (1, 1, false, '2023-11-12 18:30:30'),
    (2, 2, true, '2023-11-12 17:30:30'),
    (3, 3, false, '2023-11-12 16:30:30'),
    (4, 4, true, '2023-11-12 15:30:30'),
    (4, 5, false, '2023-11-12 14:30:30');

ALTER SEQUENCE card_id_generator RESTART WITH 10;
ALTER SEQUENCE passenger_info_id_generator RESTART WITH 10;