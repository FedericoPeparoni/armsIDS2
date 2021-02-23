do $$
declare
    v_query varchar;
begin
	
	v_query := 'INSERT INTO system_item_types (name) VALUES ($1)';
    execute format(v_query) using 'cron';
    
    v_query := 'INSERT INTO system_configurations (item_name, item_class, data_type, units, range, default_value, current_value,created_by) VALUES ($1,$2,$3,$4,$5,$6,$7,$8)';
    execute format(v_query) using 'Route cache scheduling', system_item_type_id('cron'), system_data_type_id('string'), 'cron expression', null, '00 00,10,20,30,40,50 * * * *', '00 00,10,20,30,40,50 * * * *', 'system';
end $$;
