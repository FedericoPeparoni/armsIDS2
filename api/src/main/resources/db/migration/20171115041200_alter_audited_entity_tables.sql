do $$
declare
    v_query varchar;
begin
    v_query := 'do $query$ BEGIN
        BEGIN
            ALTER TABLE %1$s ADD COLUMN version bigint NOT NULL DEFAULT 0;
        EXCEPTION WHEN duplicate_column THEN
            RAISE NOTICE ''Column ''''version'''' already exists in ''''%1$s''''.'';
        END;
    END $query$;';

    execute format(v_query, 'accounts');
    execute format(v_query, 'aerodromes');
    execute format(v_query, 'aerodromecategories');
    execute format(v_query, 'repositioning_aerodrome_clusters');
    execute format(v_query, 'aircraft_registrations');
    execute format(v_query, 'aircraft_types');
    execute format(v_query, 'atc_movement_logs');
    execute format(v_query, 'billing_centers');
    execute format(v_query, 'billing_ledgers');
    execute format(v_query, 'certificates');
    execute format(v_query, 'certificate_templates');
    execute format(v_query, 'passenger_service_charge_returns');
    execute format(v_query, 'recurring_charges');
    execute format(v_query, 'service_charge_catalogues');
    execute format(v_query, 'currencies');
    execute format(v_query, 'currency_exchange_rates');
    execute format(v_query, 'account_exemptions');
    execute format(v_query, 'aircraft_type_exemptions');
    execute format(v_query, 'exempt_flight_routes');
    execute format(v_query, 'exempt_flight_status');
    execute format(v_query, 'flight_movements');
    execute format(v_query, 'ldp_billing_formulas');
    execute format(v_query, 'navigation_billing_formulas');
    execute format(v_query, 'invoice_templates');
    execute format(v_query, 'passenger_manifests');
    execute format(v_query, 'average_mtow_factors');
    execute format(v_query, 'radar_summaries');
    execute format(v_query, 'rejected_items');
    execute format(v_query, 'report_templates');
    execute format(v_query, 'roles');
    execute format(v_query, 'nominal_routes');
    execute format(v_query, 'system_configurations');
    execute format(v_query, 'tower_movement_logs');
    execute format(v_query, 'unspecified_departure_destination_locations');
    execute format(v_query, 'unspecified_aircraft_types');
    execute format(v_query, 'users');
    execute format(v_query, 'utilities_schedules');
end $$;
