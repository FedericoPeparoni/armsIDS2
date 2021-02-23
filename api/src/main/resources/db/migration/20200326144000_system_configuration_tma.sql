do $$
declare
    v_query varchar;
begin

    v_query := 'INSERT INTO system_configurations (item_name, item_class, data_type, units, range, default_value, current_value, created_by) ' ||
               'SELECT $1,$2,$3,$4,$5,$6,$7,$8 WHERE NOT EXISTS (select id from system_configurations where item_name = $1)';

    execute format(v_query) using 'Validate flight level on airspace', system_item_type_id('workflow'), system_data_type_id('boolean'), null, 't/f', 't', 'f', 'system';
    execute format(v_query) using 'Default flight level', system_item_type_id('workflow'), system_data_type_id('int'), '100s of ft', 000-999, 300, 300, 'system';

end $$;