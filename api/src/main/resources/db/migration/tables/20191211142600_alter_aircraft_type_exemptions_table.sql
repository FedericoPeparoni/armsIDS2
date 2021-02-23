UPDATE aircraft_type_exemptions SET enroute_fees_exempt = 0 WHERE enroute_fees_exempt IS NULL;
UPDATE aircraft_type_exemptions SET parking_fees_exempt = 0 WHERE parking_fees_exempt IS NULL;
UPDATE aircraft_type_exemptions SET approach_fees_exempt = 0 WHERE approach_fees_exempt IS NULL;
UPDATE aircraft_type_exemptions SET aerodrome_fees_exempt = 0 WHERE aerodrome_fees_exempt IS NULL;
UPDATE aircraft_type_exemptions SET late_arrival_fees_exempt = 0 WHERE late_arrival_fees_exempt IS NULL;
UPDATE aircraft_type_exemptions SET late_departure_fees_exempt = 0 WHERE late_departure_fees_exempt IS NULL;
UPDATE aircraft_type_exemptions SET domestic_pax = 0 WHERE domestic_pax IS NULL;
UPDATE aircraft_type_exemptions SET international_pax = 0 WHERE international_pax IS NULL;


ALTER TABLE aircraft_type_exemptions

    ALTER COLUMN enroute_fees_exempt SET DEFAULT 0,
    ALTER COLUMN enroute_fees_exempt SET NOT NULL,

    ALTER COLUMN parking_fees_exempt SET DEFAULT 0,
    ALTER COLUMN parking_fees_exempt SET NOT NULL,

    ALTER COLUMN approach_fees_exempt SET DEFAULT 0,
    ALTER COLUMN approach_fees_exempt SET NOT NULL,

    ALTER COLUMN aerodrome_fees_exempt SET DEFAULT 0,
    ALTER COLUMN aerodrome_fees_exempt SET NOT NULL,

    ALTER COLUMN late_arrival_fees_exempt SET DEFAULT 0,
    ALTER COLUMN late_arrival_fees_exempt SET NOT NULL,

    ALTER COLUMN late_departure_fees_exempt SET DEFAULT 0,
    ALTER COLUMN late_departure_fees_exempt SET NOT NULL,

    ALTER COLUMN domestic_pax SET DEFAULT 0,
    ALTER COLUMN domestic_pax SET NOT NULL,

    ALTER COLUMN international_pax SET DEFAULT 0,
    ALTER COLUMN international_pax SET NOT NULL,

    ADD COLUMN extended_hours_fees_exempt double precision NOT NULL DEFAULT 0;
