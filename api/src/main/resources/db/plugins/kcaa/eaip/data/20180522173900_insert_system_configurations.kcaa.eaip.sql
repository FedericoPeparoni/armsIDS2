-- add system item type for kcaa eaip plugin
INSERT INTO system_item_types (name, plugin_id) VALUES ('kcaa eaip', plugin_id('kcaa.eaip'));

-- add system configuration items
do $$
declare
    v_query varchar;
begin

    v_query := 'INSERT INTO system_configurations (item_name, item_class, data_type, units, range, default_value, current_value, created_by, client_storage_forbidden, system_validation_type) VALUES ($1,$2,$3,$4,$5,$6,$7,$8,$9,$10)';
    execute format(v_query) using 'KCAA eAIP Database Connection URL', system_item_type_id('kcaa eaip'), system_data_type_id('string'), null, null, 'jdbc:sqlserver://localhost:1433;databaseName=eaip;', null, 'system', true, 'CONNECTION_URL';

end $$;
