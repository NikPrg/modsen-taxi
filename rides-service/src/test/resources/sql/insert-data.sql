INSERT INTO drivers_info
(id, external_id, first_name, last_name, car_license_plate, car_model, car_color, driver_status)
VALUES
    (1, '54bb2de9-c518-488a-9898-015faa6dee3c', 'Vlad', 'Vladovich', '8628AX-3', 'Honda Civic', 'white', 'AVAILABLE'),
    (2, '1e4d3f63-6ab8-416c-8457-af5bdd9348c5', 'Petr', 'Petrovich', null, null, null, 'CREATED'),
    (3, '877019e9-1cee-4c22-b612-e49306dd7e4d', 'Ivan', 'Ivanovich', null, null, null, 'NO_CAR'),
    (4, '73247152-2e5a-473c-9dcc-2d630c2116ef', 'Gleb', 'Glebovich', '9522BM-7', 'Mercedes-Benz C-Class', 'black', 'UNAVAILABLE'),
    (5, '63de8825-f2f8-4c55-b8d1-6318f797e7a1', 'Anton', 'Antonovich', '5132KT-4', 'Chevrolet Malibu', 'pink', 'TOWARDS_PASSENGER');

INSERT INTO rides
    (id, external_id, passenger_external_id, driver_id, pick_up_address, destination_address, ride_cost, payment_method, ride_status, ride_started_at, ride_duration, ride_created_at, payment_status)
VALUES
    (1, '2f2cae82-a434-11ee-a506-0242ac120002', '55bb3530-96b7-4adb-a9a6-c9062439fed8', 1, 'St.Kupeq', 'St.Borisoqw', 2.1, 'CASH', 'FINISHED', '03:45:35', 11, '2023-11-12 03:43:35', 'PAID'),
    (2, '36922cce-a434-11ee-a506-0242ac120002', 'eea59cd6-0c9a-48de-8f17-263b496d1a5f', 4, 'St.Moroz', 'St.Vereteq', 22.1, null, 'STARTED', '04:45:35', null, '2023-11-12 04:43:35', null),
    (3, '3ada7336-a434-11ee-a506-0242ac120002', '8e5ef207-e829-4bb5-818f-0c7c9ad0f9ed', 5, 'St.Perod', 'St.Geros', 10.2, null, 'ACCEPTED', null, null, '2023-11-12 02:43:35', null),
    (4, '6df6a606-a478-11ee-a506-0242ac120002', '8e5ef207-e829-4bb5-818f-0c7c9ad0f9ed', null, 'St.Nesterov', 'St.Maler', 13.2, null, 'INITIATED', null, null, '2023-11-12 04:43:35', null);

ALTER SEQUENCE driver_info_id_generator RESTART WITH 20;
ALTER SEQUENCE ride_id_generator RESTART WITH 20;