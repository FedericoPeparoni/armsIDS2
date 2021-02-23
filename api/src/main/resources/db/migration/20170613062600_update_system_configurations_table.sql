INSERT INTO system_data_types (id, name) VALUES (DEFAULT, 'timestamp');

do $$
declare
    v_query varchar;
begin
    v_query := 'INSERT INTO system_configurations (item_name, item_class, data_type, units, range, default_value, current_value,created_by) VALUES ($1,$2,$3,$4,$5,$6,$7,$8)';
    execute format(v_query) using 'Flight plan processor starting time', system_item_type_id('system'), system_data_type_id('timestamp'), null, null, '2017-01-01 00:00:00', '2017-01-01 00:00:00', 'system';

end $$;
