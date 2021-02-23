-- add system configuration items
do $$
declare
    v_query varchar;
begin

    v_query := 'INSERT INTO system_configurations (item_name, item_class, data_type, units, range, default_value, current_value, created_by) VALUES ($1,$2,$3,$4,$5,$6,$7,$8)';
    execute format(v_query) using 'CAAB Sage International Landing Charges - Scheduled Code', system_item_type_id('caab sage'), system_data_type_id('string'), null, null, null, null, 'system';
    execute format(v_query) using 'CAAB Sage International Landing Charges - Non–Scheduled Code', system_item_type_id('caab sage'), system_data_type_id('string'), null, null, null, null, 'system';
    execute format(v_query) using 'CAAB Sage Domestic Landing Charges – Scheduled Code', system_item_type_id('caab sage'), system_data_type_id('string'), null, null, null, null, 'system';
    execute format(v_query) using 'CAAB Sage Domestic Landing Charges - Non-Scheduled Code', system_item_type_id('caab sage'), system_data_type_id('string'), null, null, null, null, 'system';
    execute format(v_query) using 'CAAB Sage Enroute Navigation Charges - Scheduled Code', system_item_type_id('caab sage'), system_data_type_id('string'), null, null, null, null, 'system';
    execute format(v_query) using 'CAAB Sage Enroute Navigation Charges - Non-Scheduled Code', system_item_type_id('caab sage'), system_data_type_id('string'), null, null, null, null, 'system';
    execute format(v_query) using 'CAAB Sage International Passenger Service Charges Code', system_item_type_id('caab sage'), system_data_type_id('string'), null, null, null, null, 'system';
    execute format(v_query) using 'CAAB Sage Domestic Passenger Service Charges Code', system_item_type_id('caab sage'), system_data_type_id('string'), null, null, null, null, 'system';
    execute format(v_query) using 'CAAB Sage Parking Charges Code', system_item_type_id('caab sage'), system_data_type_id('string'), null, null, null, null, 'system';

end $$;
