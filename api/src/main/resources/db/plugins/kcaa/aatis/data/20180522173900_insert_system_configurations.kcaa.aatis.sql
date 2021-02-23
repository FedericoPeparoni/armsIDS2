-- add system item type for kcaa aatis plugin
INSERT INTO system_item_types (name, plugin_id) VALUES ('kcaa aatis', plugin_id('kcaa.aatis'));

-- add system configuration items
do $$
declare
    v_query varchar;
begin

    v_query := 'INSERT INTO system_configurations (item_name, item_class, data_type, units, range, default_value, current_value, created_by, client_storage_forbidden, system_validation_type) VALUES ($1,$2,$3,$4,$5,$6,$7,$8,$9,$10)';
    execute format(v_query) using 'KCAA AATIS Database Connection URL', system_item_type_id('kcaa aatis'), system_data_type_id('string'), null, null, 'jdbc:sqlserver://localhost:1433;databaseName=aatis;', null, 'system', true, 'CONNECTION_URL';

end $$;
