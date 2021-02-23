-- add system item type for kcaa erp plugin
INSERT INTO system_item_types (name, plugin_id) VALUES ('kcaa erp', plugin_id('kcaa.erp'));

-- add system configuration items
do $$
declare
    v_query varchar;
begin

    v_query := 'INSERT INTO system_configurations (item_name, item_class, data_type, units, range, default_value, current_value, created_by) VALUES ($1,$2,$3,$4,$5,$6,$7,$8)';
    execute format(v_query) using 'KCAA ERP Enroute Fees Id', system_item_type_id('kcaa erp'), system_data_type_id('string'), null, null, '10115', '10115', 'system';

end $$;
