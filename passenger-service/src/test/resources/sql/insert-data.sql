CREATE TABLE IF NOT EXISTS passengers
(
    id                         bigint primary key not null,
    external_id                uuid               not null unique,
    first_name                 varchar            not null,
    last_name                  varchar            not null,
    phone                      varchar unique     not null,
    rate                       double precision default 5.0,
    default_payment_method     varchar,
    discount_value_in_percents int,
    discount_created_at        timestamp,
    discount_expired_at        timestamp,
    discount_is_used           boolean,
    created_at                 timestamp          not null
);

INSERT INTO passengers
(id, external_id, first_name, last_name, phone, rate, default_payment_method, created_at)
VALUES (1, '55bb3530-96b7-4adb-a9a6-c9062439fed8', 'Nikita', 'Rodriguez', '+375252412972', 5.0, 'CASH', '2023-11-12 18:30:30'),
       (2, 'eea59cd6-0c9a-48de-8f17-263b496d1a5f', 'Saveliy', 'Bennett', '+375254561234', 4.5, 'CARD', '2023-11-12 17:30:30'),
       (3, '8e5ef207-e829-4bb5-818f-0c7c9ad0f9ed', 'Artem', 'Patel', '+375252499227', 5.0, 'CASH', '2023-11-12 16:30:30'),
       (4, 'fa020f15-5c63-428e-a3fb-10ab3c333d9e', 'Ivan', 'Schmidt', '+375251234578', 4.1, 'CARD', '2023-11-12 15:30:30'),
       (5, '2e68c5fc-e524-4dd3-9793-0111d3f86750', 'Eugen', 'Harrington', '+375257170873', 5.0, 'CASH', '2023-11-12 14:30:30');
