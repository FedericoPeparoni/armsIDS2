
INSERT INTO system_item_types (id, name) VALUES (DEFAULT, 'workflow');

do $$
declare
    v_query varchar;
begin
    v_query := 'INSERT INTO system_configurations (item_name, item_class, data_type, units, range, default_value, current_value,created_by) VALUES ($1,$2,$3,$4,$5,$6,$7,$8)';
    execute format(v_query) using 'Require invoice manual approval', system_item_type_id('workflow'), system_data_type_id('boolean'), null, 't/f', 'f', 'f', 'system';
    execute format(v_query) using 'Require invoice manual publishing', system_item_type_id('workflow'), system_data_type_id('boolean'), null, 't/f', 'f', 'f', 'system';
    execute format(v_query) using 'Use separate invoice number sequence for each billing centre', system_item_type_id('workflow'), system_data_type_id('boolean'), null, 't/f', 'f', 'f', 'system';
    execute format(v_query) using 'Use separate receipt number sequence for each billing centre', system_item_type_id('workflow'), system_data_type_id('boolean'), null, 't/f', 'f', 'f', 'system';
end $$;

-- inconsistencies, make them all lower case if they are boolean
UPDATE system_configurations SET default_value ='f', current_value = 'f' WHERE data_type = system_data_type_id('boolean') AND default_value = 'f';
UPDATE system_configurations SET default_value ='t', current_value = 't' WHERE data_type = system_data_type_id('boolean') AND default_value = 't';
