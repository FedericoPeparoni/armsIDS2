do $$
declare
    v_query varchar;
begin

    v_query := 'INSERT INTO system_configurations (item_name, item_class, data_type, units, range, default_value, current_value, created_by) ' ||
               'SELECT $1,$2,$3,$4,$5,$6,$7,$8 WHERE NOT EXISTS (select id from system_configurations where item_name = $1)';

    -- add US100702 system configuration items
    execute format(v_query) using 'Require bank account external system id', system_item_type_id('external'), system_data_type_id('boolean'), null, 't/f', 'f', 'f', 'system';

end $$;
