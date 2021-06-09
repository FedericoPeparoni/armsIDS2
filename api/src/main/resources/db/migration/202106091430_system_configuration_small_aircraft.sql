do $$
declare
    v_query varchar;
begin

    v_query := 'INSERT INTO system_configurations (item_name, item_class, data_type, units, range, default_value, current_value, created_by) ' ||
               'SELECT $1,$2,$3,$4,$5,$6,$7,$8 WHERE NOT EXISTS (select id from system_configurations where item_name = $1)';

    execute format(v_query) using 'Small aircraft minimum weight (kg)', system_item_type_id('workflow'), system_data_type_id('int'),
        'kg', '0,100000', '501', '501', 'system';

    execute format(v_query) using 'Exempt small AC with valid AOC - enroute', system_item_type_id('workflow'), system_data_type_id('boolean'),
        null, 't/f', 't', 't', 'system';

    execute format(v_query) using 'Exempt small AC with valid AOC - approach', system_item_type_id('workflow'), system_data_type_id('boolean'),
        null, 't/f', 'f', 'f', 'system';

    execute format(v_query) using 'Exempt small AC with valid AOC - aerodrome', system_item_type_id('workflow'), system_data_type_id('boolean'),
        null, 't/f', 'f', 'f', 'system';

    execute format(v_query) using 'Exempt small AC with valid AOC - passenger', system_item_type_id('workflow'), system_data_type_id('boolean'),
        null, 't/f', 'f', 'f', 'system';

end $$;
