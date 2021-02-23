ALTER TABLE account_exemptions

    ALTER COLUMN enroute TYPE double precision USING (CASE WHEN enroute IS TRUE THEN 100 ELSE 0 END),
    ALTER COLUMN enroute SET DEFAULT 0,

    ALTER COLUMN approach_fees_exempt TYPE double precision USING (CASE WHEN approach_fees_exempt IS TRUE THEN 100 ELSE 0 END),
    ALTER COLUMN approach_fees_exempt SET DEFAULT 0,

    ALTER COLUMN aerodrome_fees_exempt TYPE double precision USING (CASE WHEN aerodrome_fees_exempt IS TRUE THEN 100 ELSE 0 END),
    ALTER COLUMN aerodrome_fees_exempt SET DEFAULT 0,

    ALTER COLUMN late_arrival DROP DEFAULT,
    ALTER COLUMN late_arrival TYPE double precision USING (CASE WHEN late_arrival IS TRUE THEN 100 ELSE 0 END),
    ALTER COLUMN late_arrival SET DEFAULT 0,

    ALTER COLUMN late_departure DROP DEFAULT,
    ALTER COLUMN late_departure TYPE double precision USING (CASE WHEN late_departure IS TRUE THEN 100 ELSE 0 END),
    ALTER COLUMN late_departure SET DEFAULT 0,

    ALTER COLUMN parking TYPE double precision USING (CASE WHEN parking IS TRUE THEN 100 ELSE 0 END),
    ALTER COLUMN parking SET DEFAULT 0,

    ALTER COLUMN domestic_pax DROP DEFAULT,
    ALTER COLUMN domestic_pax TYPE double precision USING (CASE WHEN domestic_pax IS TRUE THEN 100 ELSE 0 END),
    ALTER COLUMN domestic_pax SET DEFAULT 0,

    ALTER COLUMN international_pax DROP DEFAULT,
    ALTER COLUMN international_pax TYPE double precision USING (CASE WHEN international_pax IS TRUE THEN 100 ELSE 0 END),
    ALTER COLUMN international_pax SET DEFAULT 0,

    ADD COLUMN extended_hours double precision NOT NULL DEFAULT 0;

UPDATE account_exemptions SET flight_notes = '' WHERE flight_notes IS NULL;

ALTER TABLE account_exemptions ALTER COLUMN flight_notes SET NOT NULL;
