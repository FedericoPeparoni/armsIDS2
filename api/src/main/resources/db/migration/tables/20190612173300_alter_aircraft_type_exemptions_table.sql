-- remove not null constraints as not longer required
ALTER TABLE aircraft_type_exemptions
    ALTER COLUMN enroute_fees_exempt DROP NOT NULL,
    ALTER COLUMN approach_fees_exempt DROP NOT NULL,
    ALTER COLUMN aerodrome_fees_exempt DROP NOT NULL,
    ALTER COLUMN late_arrival_fees_exempt DROP NOT NULL,
    ALTER COLUMN late_departure_fees_exempt DROP NOT NULL,
    ALTER COLUMN parking_fees_exempt DROP NOT NULL,
    ALTER COLUMN domestic_pax DROP NOT NULL,
    ALTER COLUMN international_pax DROP NOT NULL;

-- remove default constraints as no longer required
ALTER TABLE aircraft_type_exemptions
    ALTER COLUMN enroute_fees_exempt SET DEFAULT null,
    ALTER COLUMN approach_fees_exempt SET DEFAULT null,
    ALTER COLUMN aerodrome_fees_exempt SET DEFAULT null,
    ALTER COLUMN late_arrival_fees_exempt SET DEFAULT null,
    ALTER COLUMN late_departure_fees_exempt SET DEFAULT null,
    ALTER COLUMN parking_fees_exempt SET DEFAULT null,
    ALTER COLUMN domestic_pax SET DEFAULT null,
    ALTER COLUMN international_pax SET DEFAULT null;

-- must alter existing table so that existing data is mapped
-- TRUE maps to 100 and FALSE maps to 0
ALTER TABLE aircraft_type_exemptions
    ALTER enroute_fees_exempt TYPE DOUBLE PRECISION USING CASE WHEN enroute_fees_exempt THEN 100 ELSE null END,
    ALTER approach_fees_exempt TYPE DOUBLE PRECISION USING CASE WHEN approach_fees_exempt THEN 100 ELSE null END,
    ALTER aerodrome_fees_exempt TYPE DOUBLE PRECISION USING CASE WHEN aerodrome_fees_exempt THEN 100 ELSE null END,
    ALTER late_arrival_fees_exempt TYPE DOUBLE PRECISION USING CASE WHEN late_arrival_fees_exempt THEN 100 ELSE null END,
    ALTER late_departure_fees_exempt TYPE DOUBLE PRECISION USING CASE WHEN late_departure_fees_exempt THEN 100 ELSE null END,
    ALTER parking_fees_exempt TYPE DOUBLE PRECISION USING CASE WHEN parking_fees_exempt THEN 100 ELSE null END,
    ALTER domestic_pax TYPE DOUBLE PRECISION USING CASE WHEN domestic_pax THEN 100 ELSE null END,
    ALTER international_pax TYPE DOUBLE PRECISION USING CASE WHEN international_pax THEN 100 ELSE null END;
