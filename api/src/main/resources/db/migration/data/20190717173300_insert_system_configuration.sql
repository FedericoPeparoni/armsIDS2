do $$
declare
    v_query varchar;
begin

    v_query := 'INSERT INTO system_configurations (item_name, item_class, data_type, units, range, default_value, current_value, created_by) ' ||
               'SELECT $1,$2,$3,$4,$5,$6,$7,$8 WHERE NOT EXISTS (select id from system_configurations where item_name = $1)';

    -- add US102157 system configuration item
    execute format(v_query) using 'Aerodrome nationality determined by', system_item_type_id('crossing'), system_data_type_id('string'), null, 'Prefix,Location,Either', 'Location', 'Location', 'system';

end $$;
