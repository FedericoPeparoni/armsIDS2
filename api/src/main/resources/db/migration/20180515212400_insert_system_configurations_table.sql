--Insert new items in System Configurations
do $$
declare
    v_query varchar;
begin
    v_query := 'INSERT INTO system_configurations (item_name, item_class, data_type, units, range, default_value, current_value, created_by) VALUES ($1,$2,$3,$4,$5,$6,$7,$8)';
    execute format(v_query) using 'Current fiscal year', system_item_type_id('workflow'), system_data_type_id('string'), 'yy/yy', null, null, null, 'system';
    execute format(v_query) using 'Reset sequence numbers on new fiscal year', system_item_type_id('workflow'), system_data_type_id('boolean'), null, 't/f', 'f', 'f', 'system';
end $$;
