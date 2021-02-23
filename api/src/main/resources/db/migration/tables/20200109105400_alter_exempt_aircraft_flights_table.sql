--Delete duplicate data
DELETE FROM exempt_aircraft_flights a USING exempt_aircraft_flights b
WHERE (a.aircraft_registration = b.aircraft_registration
OR (a.aircraft_registration is NULL AND b.aircraft_registration is NULL)
OR (a.aircraft_registration is NULL AND b.aircraft_registration = '')
OR (a.aircraft_registration = '' AND b.aircraft_registration is NULL)
)
AND (a.flight_id = b.flight_id
OR (a.flight_id is NULL AND b.flight_id is NULL)
OR (a.flight_id is NULL AND b.flight_id = '')
OR (a.flight_id = '' AND b.flight_id is NULL)
)
AND a.exemption_start_date = b.exemption_start_date
AND a.id < b.id;

--Drop old UNIQUE constraint
ALTER TABLE exempt_aircraft_flights DROP CONSTRAINT unique_fields_eaf;

--Add UNIQUE constraint
ALTER TABLE exempt_aircraft_flights ADD CONSTRAINT unique_registration_flight_id_start_date UNIQUE (aircraft_registration, flight_id, exemption_start_date);
