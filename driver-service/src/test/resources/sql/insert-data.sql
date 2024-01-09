INSERT INTO drivers
    (id, external_id, first_name, last_name, phone, rate, created_at, driver_status)
VALUES
    (11, '54bb2de9-c518-488a-9898-015faa6dee3c', 'Vlad', 'Vladovich', '+375259422972', 5.0, '2023-11-12 11:30:30', 'AVAILABLE'),
    (12, '1e4d3f63-6ab8-416c-8457-af5bdd9348c5', 'Petr', 'Petrovich', '+375259987452', 3.0, '2023-11-12 12:30:30', 'CREATED'),
    (13, '877019e9-1cee-4c22-b612-e49306dd7e4d', 'Ivan', 'Ivanovich', '+375251234578', 4.5, '2023-11-12 13:30:30', 'NO_CAR'),
    (14, '73247152-2e5a-473c-9dcc-2d630c2116ef', 'Gleb', 'Glebovich', '+375254567845', 4.0, '2023-11-12 14:30:30', 'UNAVAILABLE'),
    (15, '63de8825-f2f8-4c55-b8d1-6318f797e7a1', 'Anton', 'Antonovich', '+375254561425', 5.0, '2023-11-12 15:30:30', 'TOWARDS_PASSENGER');

INSERT INTO cars
    (id, external_id, driver_id, license_plate, model, color, created_at)
VALUES
    (11, '2952848e-0da5-44c2-a359-d96736466bb0', 11, '8628AX-3', 'Honda Civic', 'white', '2023-12-12 10:30:30'),
    (12, '7eb73b09-adc4-4662-b6a1-e81f45bce426', 14, '9522BM-7', 'Mercedes-Benz C-Class', 'black', '2023-12-12 11:30:30'),
    (13, '622c3d86-b26f-42bf-a857-6d19fd92684a', 15, '5132KT-4', 'Chevrolet Malibu', 'pink', '2023-12-12 12:30:30');

ALTER SEQUENCE driver_generator RESTART WITH 20;
ALTER SEQUENCE car_generator RESTART WITH 20;