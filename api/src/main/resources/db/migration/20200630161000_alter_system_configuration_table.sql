do $$
declare
    v_query varchar;
begin
    v_query := 'INSERT INTO system_configurations (item_name, item_class, data_type, units, range, default_value, current_value, created_by) VALUES ($1,$2,$3,$4,$5,$6,$7,$8)';
    execute format(v_query) using 'Cronos connection URL', system_item_type_id('whitelist'), system_data_type_id('string'), null, null, null, null, 'system';
    execute format(v_query) using 'Cronos connection login', system_item_type_id('whitelist'), system_data_type_id('string'), null, null, null, null, 'system';
    execute format(v_query) using 'Cronos connection password', system_item_type_id('whitelist'), system_data_type_id('string'), null, null, null, null, 'system';
end $$;

DELETE FROM system_configurations WHERE item_name = 'Originator AFTN address';
DELETE FROM system_configurations WHERE item_name = 'Accepted flight notice addresses';
DELETE FROM system_configurations WHERE item_name = 'Declined flight notice addresses';
DELETE FROM system_configurations WHERE item_name = 'Flight notice priority';
