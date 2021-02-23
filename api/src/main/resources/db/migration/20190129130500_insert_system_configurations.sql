do $$
declare
    v_query varchar;
begin

    v_query := 'INSERT INTO system_configurations (item_name, item_class, data_type, units, range, default_value, current_value, created_by) ' ||
               'SELECT $1,$2,$3,$4,$5,$6,$7,$8 WHERE NOT EXISTS (select id from system_configurations where item_name = $1)';

    -- add US100980 system configuration items
    execute format(v_query) using 'Apply interest penalty on', system_item_type_id('workflow'), system_data_type_id('string'), null, 'Daily,Next invoice,Invoice final payment', 'Daily', 'Daily', 'system';
    execute format(v_query) using 'Interest penalty invoice currency', system_item_type_id('workflow'), system_data_type_id('string'), null, 'ANSP,USD,Original invoice', 'ANSP', 'ANSP', 'system';

end $$;
