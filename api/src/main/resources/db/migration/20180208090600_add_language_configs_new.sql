-- Update old insert statement - the current and default language should be English

-- OLD CODE:

--do $$
--declare
--    v_query varchar;
--begin
--
--	v_query := 'INSERT INTO system_configurations (item_name, item_class, data_type, range, default_value, current_value, created_by) VALUES ($1,$2,$3,$4,$5,$6,$7)';
--    execute format(v_query) using 'Language selection', system_item_type_id('system'), system_data_type_id('string'), '[{"code": "en", "label": "English"}, { "code": "es", "label": "Español"}]', '{ "code": "es", "label": "Español"}', '{ "code": "es", "label": "Español"}', 'system';
--    execute format(v_query) using 'Language enabled', system_item_type_id('system'), system_data_type_id('boolean'), 't,f', 't', 't', 'system';
--    execute format(v_query) using 'Language supported', system_item_type_id('system'), system_data_type_id('string'), '[{"code": "en", "label": "English"}, { "code": "es", "label": "Español"}]', '[{"code": "en", "label": "English"}, { "code": "es", "label": "Español"}]', '[{"code": "en", "label": "English"}, { "code": "es", "label": "Español"}]', 'system';
--end $$;

-- NEW CODE:

DO
$do$
declare
    v_query varchar;
BEGIN
    v_query := 'INSERT INTO system_configurations (item_name, item_class, data_type, range, default_value, current_value, created_by) VALUES ($1,$2,$3,$4,$5,$6,$7)';
IF NOT EXISTS (SELECT * FROM system_configurations where item_name = 'Language selection') THEN
   execute format(v_query) using 'Language selection', system_item_type_id('system'), system_data_type_id('string'), '[{"code": "en", "label": "English"}, { "code": "es", "label": "Español"}]', '{"code":"en","label":"English"}', '{"code":"en","label":"English"}', 'system';
END IF;

IF NOT EXISTS (SELECT * FROM system_configurations where item_name = 'Language enabled') THEN
   execute format(v_query) using 'Language enabled', system_item_type_id('system'), system_data_type_id('boolean'), 't,f', 't', 't', 'system';
END IF;

IF NOT EXISTS (SELECT * FROM system_configurations where item_name = 'Language supported') THEN
   execute format(v_query) using 'Language supported', system_item_type_id('system'), system_data_type_id('string'), '[{"code": "en", "label": "English"}, { "code": "es", "label": "Español"}]', '[{"code": "en", "label": "English"}, { "code": "es", "label": "Español"}]', '[{"code": "en", "label": "English"}, { "code": "es", "label": "Español"}]', 'system';
END IF;

END
$do$
