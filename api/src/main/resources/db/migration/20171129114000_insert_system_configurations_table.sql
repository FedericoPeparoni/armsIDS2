do $$
declare
    v_query varchar;
begin
    v_query := 'INSERT INTO system_configurations (item_name, item_class, data_type, units, range, default_value, current_value, created_by) VALUES ($1,$2,$3,$4,$5,$6,$7,$8)';

    execute format(v_query) using 'Training cost currency', system_item_type_id('ansp'), system_data_type_id('string'), NULL, 'ANSP,USD', 'ANSP', 'ANSP', 'system';
    execute format(v_query) using 'Training cost per day',  system_item_type_id('ansp'), system_data_type_id('float'),  NULL, NULL,  '0.0', '0.0',  'system';
end $$;
