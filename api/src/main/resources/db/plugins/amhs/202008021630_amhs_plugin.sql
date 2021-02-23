-- AMHS plugin
insert into plugins (name, description, enabled, visible, key) values ('AMHS', 'Process flight plans sent via AMHS', false, true, 'amhs');

-- AMHS item type
insert into system_item_types (name, plugin_id) values ('amhs', (SELECT last_value FROM plugins_id_seq));

-- AMHS config items
do $$
declare
    v_query varchar;
begin
    v_query := 'INSERT INTO system_configurations (item_name, item_class, data_type, units, range, default_value, current_value,created_by) VALUES ($1,$2,$3,$4,$5,$6,$7,$8)';
    execute format(v_query) using
        'AMHS: incoming message directory', -- item_name
        system_item_type_id('amhs'),        -- item_class
        system_data_type_id('filename'),    -- data_type
        null,                               -- units
        null,                               -- range
        null,                               -- default_value
        null,                               -- current_value
        'system'                            -- created_by
    ;
end $$;
