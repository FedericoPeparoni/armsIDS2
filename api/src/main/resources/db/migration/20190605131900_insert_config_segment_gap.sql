do $$
declare
    v_query varchar;
begin

    v_query := 'INSERT INTO system_configurations (item_name, item_class, data_type, units, range, default_value, current_value, created_by) ' ||
               'SELECT $1,$2,$3,$4,$5,$6,$7,$8 WHERE NOT EXISTS (select id from system_configurations where item_name = $1)';

    execute format(v_query) using 'Minimum segment gap', system_item_type_id('crossing'), system_data_type_id('float'), 'km', '0.0,7.0', '7', '7', 'system';

end $$;
