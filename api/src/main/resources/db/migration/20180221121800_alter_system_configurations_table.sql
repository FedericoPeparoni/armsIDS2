do $$
declare
    v_query varchar;
begin
    v_query := 'INSERT INTO system_configurations (item_name, item_class, data_type, units, range, default_value, current_value,created_by) VALUES ($1,$2,$3,$4,$5,$6,$7,$8)';
    execute format(v_query) using 'Minimum window to detect duplicate flights', system_item_type_id('default'), system_data_type_id('int'), 'int', '0-120', '60', '60', 'system';
    v_query := 'INSERT INTO system_configurations (item_name, item_class, data_type, units, range, default_value, current_value,created_by) VALUES ($1,$2,$3,$4,$5,$6,$7,$8)';
    execute format(v_query) using 'Percentage of EET to detect duplicate flights', system_item_type_id('default'), system_data_type_id('int'), 'percent', '0-200', '100', '100', 'system';
end $$;
