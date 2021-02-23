ALTER TABLE exempt_flight_routes

    ALTER COLUMN enroute_fees_are_exempt DROP DEFAULT,
    ALTER COLUMN enroute_fees_are_exempt TYPE double precision USING (CASE WHEN enroute_fees_are_exempt IS TRUE THEN 100 ELSE 0 END),
    ALTER COLUMN enroute_fees_are_exempt SET DEFAULT 0,

    ALTER COLUMN approach_fees_are_exempt DROP DEFAULT,
    ALTER COLUMN approach_fees_are_exempt TYPE double precision USING (CASE WHEN approach_fees_are_exempt IS TRUE THEN 100 ELSE 0 END),
    ALTER COLUMN approach_fees_are_exempt SET DEFAULT 0,

    ALTER COLUMN aerodrome_fees_are_exempt DROP DEFAULT,
    ALTER COLUMN aerodrome_fees_are_exempt TYPE double precision USING (CASE WHEN aerodrome_fees_are_exempt IS TRUE THEN 100 ELSE 0 END),
    ALTER COLUMN aerodrome_fees_are_exempt SET DEFAULT 0,

    ALTER COLUMN late_arrival_fees_are_exempt DROP DEFAULT,
    ALTER COLUMN late_arrival_fees_are_exempt TYPE double precision USING (CASE WHEN late_arrival_fees_are_exempt IS TRUE THEN 100 ELSE 0 END),
    ALTER COLUMN late_arrival_fees_are_exempt SET DEFAULT 0,

    ALTER COLUMN late_departure_fees_are_exempt DROP DEFAULT,
    ALTER COLUMN late_departure_fees_are_exempt TYPE double precision USING (CASE WHEN late_departure_fees_are_exempt IS TRUE THEN 100 ELSE 0 END),
    ALTER COLUMN late_departure_fees_are_exempt SET DEFAULT 0,

    ALTER COLUMN parking_fees_are_exempt DROP DEFAULT,
    ALTER COLUMN parking_fees_are_exempt TYPE double precision USING (CASE WHEN parking_fees_are_exempt IS TRUE THEN 100 ELSE 0 END),
    ALTER COLUMN parking_fees_are_exempt SET DEFAULT 0,

    ALTER COLUMN domestic_pax DROP DEFAULT,
    ALTER COLUMN domestic_pax TYPE double precision USING (CASE WHEN domestic_pax IS TRUE THEN 100 ELSE 0 END),
    ALTER COLUMN domestic_pax SET DEFAULT 0,

    ALTER COLUMN international_pax DROP DEFAULT,
    ALTER COLUMN international_pax TYPE double precision USING (CASE WHEN international_pax IS TRUE THEN 100 ELSE 0 END),
    ALTER COLUMN international_pax SET DEFAULT 0,

    ADD COLUMN extended_hours double precision NOT NULL DEFAULT 0;

--Update flight_notes with NULL value and then add NOT NULL constraint
UPDATE exempt_flight_routes SET flight_notes = '' WHERE flight_notes IS NULL;
ALTER TABLE exempt_flight_routes ALTER COLUMN flight_notes SET NOT NULL;

--Delete duplicate data
DELETE FROM exempt_flight_routes USING exempt_flight_routes alias
WHERE exempt_flight_routes.departure_aerodrome = alias.departure_aerodrome
AND exempt_flight_routes.destination_aerodrome = alias.destination_aerodrome
AND  exempt_flight_routes.id < alias.id;

--Add UNIQUE constraint
ALTER TABLE exempt_flight_routes ADD CONSTRAINT unique_departure_destination UNIQUE (departure_aerodrome, destination_aerodrome);
