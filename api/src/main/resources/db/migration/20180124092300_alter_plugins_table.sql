-- alter plugins table
ALTER TABLE plugins ADD COLUMN version bigint NOT NULL DEFAULT 0;

-- add system item class for plugin
INSERT INTO system_item_types (name, plugin_id) VALUES ('prototype', plugin_id('prototype'));

-- add system configuration items
do $$
declare
    v_query varchar;
begin

    v_query := 'INSERT INTO system_configurations (item_name, item_class, data_type, units, range, default_value, current_value, created_by) VALUES ($1,$2,$3,$4,$5,$6,$7,$8)';
    execute format(v_query) using 'Mock fatal error occurrence frequency', system_item_type_id('prototype'), system_data_type_id('int'), 'percent', '0-100', '50', '50', 'system';

end $$;
