--Account Exemptions
ALTER TABLE account_exemptions
    DROP COLUMN landing,
    DROP COLUMN departure,
    DROP COLUMN adult,
    DROP COLUMN child;

ALTER TABLE account_exemptions ADD COLUMN late_arrival bool NOT NULL DEFAULT FALSE;
ALTER TABLE account_exemptions ADD COLUMN late_departure bool NOT NULL DEFAULT FALSE;
ALTER TABLE account_exemptions ADD COLUMN domestic_pax bool NOT NULL DEFAULT FALSE;
ALTER TABLE account_exemptions ADD COLUMN international_pax bool NOT NULL DEFAULT FALSE;

--Aircraft Type Exemptions
ALTER TABLE aircraft_type_exemptions
    DROP COLUMN adult_fees_exempt,
    DROP COLUMN child_fees_exempt;

ALTER TABLE aircraft_type_exemptions ADD COLUMN domestic_pax bool NOT NULL DEFAULT FALSE;
ALTER TABLE aircraft_type_exemptions ADD COLUMN international_pax bool NOT NULL DEFAULT FALSE;

--Flight Route Exemptions
ALTER TABLE exempt_flight_routes
    DROP COLUMN adult_passenger_fees_are_exempt,
    DROP COLUMN child_passenger_fees_are_exempt;

ALTER TABLE exempt_flight_routes ADD COLUMN domestic_pax bool NOT NULL DEFAULT FALSE;
ALTER TABLE exempt_flight_routes ADD COLUMN international_pax bool NOT NULL DEFAULT FALSE;

--Flight Status Exemptions
ALTER TABLE exempt_flight_status
    DROP COLUMN adult_passenger_fees_exempt,
    DROP COLUMN child_passenger_fees_exempt;

ALTER TABLE exempt_flight_status ADD COLUMN domestic_pax bool NOT NULL DEFAULT FALSE;
ALTER TABLE exempt_flight_status ADD COLUMN international_pax bool NOT NULL DEFAULT FALSE;

--Repositioning Aerodrome Cluster Exemptions
ALTER TABLE repositioning_aerodrome_clusters
    DROP COLUMN adult_passenger_fees_are_exempt,
    DROP COLUMN child_passenger_fees_are_exempt;

ALTER TABLE repositioning_aerodrome_clusters ADD COLUMN domestic_pax bool NOT NULL DEFAULT FALSE;
ALTER TABLE repositioning_aerodrome_clusters ADD COLUMN international_pax bool NOT NULL DEFAULT FALSE;
