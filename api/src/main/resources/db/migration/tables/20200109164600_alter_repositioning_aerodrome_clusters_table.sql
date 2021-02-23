ALTER TABLE repositioning_aerodrome_clusters

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
