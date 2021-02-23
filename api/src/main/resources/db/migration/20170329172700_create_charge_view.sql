/**
    Returns a list of flights to be used for statistics
 */
DROP VIEW IF EXISTS charge_view;
CREATE VIEW charge_view AS (
    SELECT
        bc.name,
        fm.date_of_flight,
        fm.movement_type,
        CASE WHEN movement_type IN ('INT_DEPARTURE', 'REG_DEPARTURE')
            THEN fm.dep_ad
        WHEN movement_type IN ('DOMESTIC', 'INT_ARRIVAL', 'REG ARRIVAL')
            THEN fm.dest_ad
        WHEN movement_type IN ('INT_OVERFLIGHT', 'REG_OVERFLIGHT')
            THEN ''
        ELSE 'ERROR'
        END                            "bill_aerodrome",
        CASE WHEN movement_type IN ('INT_OVERFLIGHT', 'INT_ARRIVAL', 'INT_DEPARTURE')
            THEN 'INTERNATIONAL'
        WHEN movement_type IN ('REG_OVERFLIGHT', 'REG_ARRIVAL', 'REG_DEPARTURE')
            THEN 'REGIONAL'
        WHEN movement_type IN ('DOMESTIC')
            THEN 'DOMESTIC'
        ELSE 'ERROR'
        END                            "flight_scope",
        CASE WHEN movement_type IN ('INT_OVERFLIGHT', 'REG_OVERFLIGHT')
            THEN 'OVERFLIGHT'
        WHEN movement_type IN ('INT_ARRIVAL', 'REG_ARRIVAL')
            THEN 'ARRIVAL'
        WHEN movement_type IN ('INT_DEPARTURE', 'REG_DEPARTURE')
            THEN 'DEPARTURE'
        WHEN movement_type IN ('DOMESTIC')
            THEN 'DOMESTIC'
        ELSE 'ERROR'
        END                            "flight_type",
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
        fm.total_charges
    FROM billing_centers bc,
        aerodromes ad,
        flight_movements fm
    WHERE bc.id = ad.billing_center_id
          AND (ad.aerodrome_name = fm.dep_ad OR ad.aerodrome_name = fm.dest_ad)
          AND fm.movement_type != 'OTHER'
);
