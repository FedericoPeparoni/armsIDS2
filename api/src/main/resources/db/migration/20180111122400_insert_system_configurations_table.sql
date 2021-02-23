do $$
declare
    v_query varchar;
begin

    v_query := 'INSERT INTO system_configurations (item_name, item_class, data_type, units, range, default_value, current_value, created_by) VALUES ($1,$2,$3,$4,$5,$6,$7,$8)';
    execute format(v_query) using 'Cached event retry interval', system_item_type_id('system'), system_data_type_id('int'), null, '1-1440', '5', '5', 'system';

end $$;
