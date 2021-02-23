-- add system item type for caab sage plugin
INSERT INTO system_item_types (name, plugin_id) VALUES ('caab sage', plugin_id('caab.sage'));

-- add system configuration items
do $$
declare
    v_query varchar;
begin

    v_query := 'INSERT INTO system_configurations (item_name, item_class, data_type, units, range, default_value, current_value, created_by, client_storage_forbidden, system_validation_type) VALUES ($1,$2,$3,$4,$5,$6,$7,$8,$9,$10)';
    execute format(v_query) using 'CAAB Sage Database Connection URL', system_item_type_id('caab sage'), system_data_type_id('string'), null, null, 'jdbc:sqlserver://localhost:1433;databaseName=sage;', null, 'system', true, 'CONNECTION_URL';

end $$;
