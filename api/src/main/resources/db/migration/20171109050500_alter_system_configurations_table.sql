do $$
declare
    v_query varchar;
begin
	
	v_query := 'INSERT INTO system_configurations (item_name, item_class, data_type, units, range, default_value, current_value,created_by) VALUES ($1,$2,$3,$4,$5,$6,$7,$8)';
    execute format(v_query) using 'Map south latitude', system_item_type_id('default'), system_data_type_id('float'), 'degrees', '-90 to 90', null, null, 'system';
    execute format(v_query) using 'Map west longitude', system_item_type_id('default'), system_data_type_id('float'), 'degrees', '-180 to 180', null, null, 'system';
    execute format(v_query) using 'Map north latitude', system_item_type_id('default'), system_data_type_id('float'), 'degrees', '-90 to 90', null, null, 'system';
    execute format(v_query) using 'Map east longitude', system_item_type_id('default'), system_data_type_id('float'), 'degrees', '-180 to 180', null, null, 'system';
end $$;
