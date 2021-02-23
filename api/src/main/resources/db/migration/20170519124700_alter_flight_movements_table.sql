DROP VIEW IF EXISTS charge_view CASCADE;

ALTER TABLE flight_movements ALTER COLUMN item18_dep TYPE varchar;
ALTER TABLE flight_movements ALTER COLUMN item18_dest TYPE varchar;
ALTER TABLE flight_movements ALTER COLUMN item18_rmk TYPE varchar;

CREATE OR REPLACE VIEW abms.charge_view AS
 SELECT bc.name,
    fm.date_of_flight,
    fm.movement_type,
        CASE
            WHEN fm.movement_type::text = ANY (ARRAY['INT_DEPARTURE'::character varying, 'REG_DEPARTURE'::character varying]::text[]) THEN fm.dep_ad
            WHEN fm.movement_type::text = ANY (ARRAY['DOMESTIC'::character varying, 'INT_ARRIVAL'::character varying, 'REG ARRIVAL'::character varying]::text[]) THEN fm.dest_ad
            WHEN fm.movement_type::text = ANY (ARRAY['INT_OVERFLIGHT'::character varying, 'REG_OVERFLIGHT'::character varying]::text[]) THEN ''::character varying
            ELSE 'ERROR'::character varying
        END AS bill_aerodrome,
        CASE
            WHEN fm.movement_type::text = ANY (ARRAY['INT_OVERFLIGHT'::character varying, 'INT_ARRIVAL'::character varying, 'INT_DEPARTURE'::character varying]::text[]) THEN 'INTERNATIONAL'::text
            WHEN fm.movement_type::text = ANY (ARRAY['REG_OVERFLIGHT'::character varying, 'REG_ARRIVAL'::character varying, 'REG_DEPARTURE'::character varying]::text[]) THEN 'REGIONAL'::text
            WHEN fm.movement_type::text = 'DOMESTIC'::text THEN 'DOMESTIC'::text
            ELSE 'ERROR'::text
        END AS flight_scope,
        CASE
            WHEN fm.movement_type::text = ANY (ARRAY['INT_OVERFLIGHT'::character varying, 'REG_OVERFLIGHT'::character varying]::text[]) THEN 'OVERFLIGHT'::text
            WHEN fm.movement_type::text = ANY (ARRAY['INT_ARRIVAL'::character varying, 'REG_ARRIVAL'::character varying]::text[]) THEN 'ARRIVAL'::text
            WHEN fm.movement_type::text = ANY (ARRAY['INT_DEPARTURE'::character varying, 'REG_DEPARTURE'::character varying]::text[]) THEN 'DEPARTURE'::text
            WHEN fm.movement_type::text = 'DOMESTIC'::text THEN 'DOMESTIC'::text
            ELSE 'ERROR'::text
        END AS flight_type,
    (fm.dep_ad::text || '-'::text) || fm.dest_ad::text AS route,
    fm.aircraft_type,
    fm.flight_level,
    fm.flight_category,
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
    fm.tasp_charge,
    fm.total_charges,
    ( SELECT max(average_mtow_factors.average_mtow_factor) AS max
           FROM abms.average_mtow_factors
          WHERE fm.actual_mtow::double precision < average_mtow_factors.upper_limit) AS mtow_category
   FROM abms.billing_centers bc,
    abms.aerodromes ad,
    abms.flight_movements fm
  WHERE bc.id = ad.billing_center_id AND (ad.aerodrome_name::text = fm.dep_ad::text OR ad.aerodrome_name::text = fm.dest_ad::text) AND fm.movement_type::text <> 'OTHER'::text;
