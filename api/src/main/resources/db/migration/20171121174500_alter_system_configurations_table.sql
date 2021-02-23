do $$
declare
    v_query varchar;
begin

    v_query := 'INSERT INTO system_configurations (item_name, item_class, data_type, units, range, default_value, current_value, created_by) VALUES ($1,$2,$3,$4,$5,$6,$7,$8)';
    execute format(v_query) using 'Radar summary IP address', system_item_type_id('upload'), system_data_type_id('string'), 'address', null, null, null, 'system';
    execute format(v_query) using 'Radar summary port number', system_item_type_id('upload'), system_data_type_id('int'), 'port', '1025-65536', null, null, 'system';

end $$;
