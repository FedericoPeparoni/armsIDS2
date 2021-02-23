do $$
declare
    v_query varchar;
begin

    v_query := 'INSERT INTO system_configurations (item_name, item_class, data_type, units, range, default_value, current_value, created_by) ' ||
               'SELECT $1,$2,$3,$4,$5,$6,$7,$8 WHERE NOT EXISTS (select id from system_configurations where item_name = $1)';

    -- add US102157 system configuration item
    execute format(v_query) using 'THRU PLAN estimated stop time', system_item_type_id('data'), system_data_type_id('int'), 'minutes', '0,60', '20', '20', 'system';

end $$;
