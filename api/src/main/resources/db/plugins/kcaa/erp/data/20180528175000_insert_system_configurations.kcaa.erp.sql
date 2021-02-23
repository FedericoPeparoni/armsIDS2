-- add system configuration items
do $$
declare
    v_query varchar;
begin

    v_query := 'INSERT INTO system_configurations (item_name, item_class, data_type, units, range, default_value, current_value, created_by) VALUES ($1,$2,$3,$4,$5,$6,$7,$8)';
    execute format(v_query) using 'KCAA ERP aircraft registration processor starting timestamp', system_item_type_id('kcaa erp'), system_data_type_id('string'), null, null, null, null, 'system';
    execute format(v_query) using 'KCAA ERP receipt processor starting timestamp', system_item_type_id('kcaa erp'), system_data_type_id('string'), null, null, null, null, 'system';

end $$;
