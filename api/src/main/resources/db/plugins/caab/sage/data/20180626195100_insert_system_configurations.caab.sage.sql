-- add system configuration items
do $$
declare
    v_query varchar;
begin

    v_query := 'INSERT INTO system_configurations (item_name, item_class, data_type, units, range, default_value, current_value, created_by) VALUES ($1,$2,$3,$4,$5,$6,$7,$8)';
    execute format(v_query) using 'CAAB Sage Cash Customer Account', system_item_type_id('caab sage'), system_data_type_id('string'), null, null, 'CASH01', 'CASH01', 'system';

end $$;
