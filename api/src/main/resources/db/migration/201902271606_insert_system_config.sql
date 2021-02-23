do $$
declare
    v_query varchar;
begin

    v_query := 'INSERT INTO system_configurations (item_name, item_class, data_type, units, range, default_value, current_value, created_by) ' ||
               'SELECT $1,$2,$3,$4,$5,$6,$7,$8 WHERE NOT EXISTS (select id from system_configurations where item_name = $1)';

    -- add US100965 system configuration items
    execute format(v_query) using 'Flight total decimal places', system_item_type_id('crossing'), system_data_type_id('int'), null, '0,5', '2', '2', 'system';

end $$;

update system_configurations  set current_value =2 where item_name ='Flight leg decimal places';
