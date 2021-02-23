-- Remove cast on charge_view
drop view if exists charge_view;
create view charge_view as (
    select bc.name,
        fm.date_of_flight,
        fm.movement_type,
        CASE WHEN movement_type in ('INT_DEPARTURE', 'REG_DEPARTURE' ) THEN fm.dep_ad
        WHEN movement_type in ( 'DOMESTIC', 'INT_ARRIVAL', 'REG ARRIVAL' ) THEN fm.dest_ad
        WHEN movement_type in ( 'INT_OVERFLIGHT', 'REG_OVERFLIGHT' ) THEN ''
        ELSE 'ERROR'
        END "bill_aerodrome",
        CASE WHEN movement_type in ( 'INT_OVERFLIGHT', 'INT_ARRIVAL', 'INT_DEPARTURE' ) THEN 'INTERNATIONAL'
        WHEN movement_type in ( 'REG_OVERFLIGHT', 'REG_ARRIVAL', 'REG_DEPARTURE' ) THEN 'REGIONAL'
        WHEN movement_type in ( 'DOMESTIC' ) THEN 'DOMESTIC'
        ELSE 'ERROR'
        END "flight_scope",
        CASE WHEN movement_type in ( 'INT_OVERFLIGHT', 'REG_OVERFLIGHT' ) THEN 'OVERFLIGHT'
        WHEN movement_type in ( 'INT_ARRIVAL', 'REG_ARRIVAL' ) THEN 'ARRIVAL'
        WHEN movement_type in ( 'INT_DEPARTURE', 'REG_DEPARTURE' ) THEN 'DEPARTURE'
        WHEN movement_type in ( 'DOMESTIC' ) THEN 'DOMESTIC'
        ELSE 'ERROR'
        END "flight_type",
        fm.dep_ad || '-' || fm.dest_ad "route",
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
        (select max(average_mtow_factor) from average_mtow_factors where fm.actual_mtow < upper_limit) as mtow_category
    from billing_centers bc,
        aerodromes ad,
        flight_movements fm
    where bc. id = ad. billing_center_id
          and ( ad.aerodrome_name = fm.dep_ad or ad.aerodrome_name = fm.dest_ad )
          and movement_type != 'OTHER'
);
