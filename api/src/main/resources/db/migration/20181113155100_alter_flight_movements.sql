-- the view is needed to be dropped first because of the error:
-- Reason: liquibase.exception.DatabaseException: ERROR: cannot alter type of a column used by a view or rule
-- Detail: rule _RETURN on view charge_view depends on column "aircraft_type" [Failed SQL: ALTER TABLE flight_movements]
drop view if exists charge_view;

ALTER TABLE flight_movements
ALTER COLUMN aircraft_type TYPE character varying(5);

CREATE OR REPLACE VIEW charge_view AS
 SELECT bc.name,
    fm.date_of_flight,
    fm.movement_type,
        CASE
            WHEN fm.movement_type::text = ANY (ARRAY['INT_DEPARTURE'::character varying::text, 'REG_DEPARTURE'::character varying::text]) THEN fm.dep_ad
            WHEN fm.movement_type::text = ANY (ARRAY['DOMESTIC'::character varying::text, 'INT_ARRIVAL'::character varying::text, 'REG_ARRIVAL'::character varying::text]) THEN fm.dest_ad
            WHEN fm.movement_type::text = ANY (ARRAY['INT_OVERFLIGHT'::character varying::text, 'REG_OVERFLIGHT'::character varying::text]) THEN ''::character varying
            ELSE 'ERROR'::character varying
        END AS bill_aerodrome,
        CASE
            WHEN fm.movement_type::text = ANY (ARRAY['INT_OVERFLIGHT'::character varying::text, 'INT_ARRIVAL'::character varying::text, 'INT_DEPARTURE'::character varying::text]) THEN 'INTERNATIONAL'::text
            WHEN fm.movement_type::text = ANY (ARRAY['REG_OVERFLIGHT'::character varying::text, 'REG_ARRIVAL'::character varying::text, 'REG_DEPARTURE'::character varying::text]) THEN 'REGIONAL'::text
            WHEN fm.movement_type::text = 'DOMESTIC'::text THEN 'DOMESTIC'::text
            ELSE 'ERROR'::text
        END AS flight_scope,
        CASE
            WHEN fm.movement_type::text = ANY (ARRAY['INT_OVERFLIGHT'::character varying::text, 'REG_OVERFLIGHT'::character varying::text]) THEN 'OVERFLIGHT'::text
            WHEN fm.movement_type::text = ANY (ARRAY['DOMESTIC'::character varying::text, 'INT_ARRIVAL'::character varying::text, 'REG_ARRIVAL'::character varying::text]) THEN 'ARRIVAL'::text
            WHEN fm.movement_type::text = ANY (ARRAY['INT_DEPARTURE'::character varying::text, 'REG_DEPARTURE'::character varying::text]) THEN 'DEPARTURE'::text
            ELSE 'ERROR'::text
        END AS flight_type,
    (fm.dep_ad::text || '-'::text) || fm.dest_ad::text AS route,
    fm.aircraft_type,
    fm.flight_level,
    fm.flight_rules,
    fm.passengers_chargeable_domestic,
    fm.passengers_chargeable_intern,
    fm.enroute_charges,
    fm.approach_charges,
    fm.aerodrome_charges,
    fm.late_arrival_charges,
    fm.late_departure_charges,
    fm.domestic_passenger_charges,
    fm.international_passenger_charges,
    fm.parking_charges,
    fm.total_charges,
    ( SELECT max(average_mtow_factors.upper_limit) AS max
           FROM average_mtow_factors
          WHERE fm.actual_mtow <= average_mtow_factors.upper_limit) AS mtow_category,
    ac.name AS account_name
   FROM billing_centers bc,
    aerodromes ad,
    flight_movements fm
     LEFT JOIN accounts ac ON fm.account = ac.id
  WHERE bc.id = ad.billing_center_id AND (ad.aerodrome_name::text = fm.dep_ad::text OR ad.aerodrome_name::text = fm.dest_ad::text) AND fm.movement_type::text <> 'OTHER'::text;
