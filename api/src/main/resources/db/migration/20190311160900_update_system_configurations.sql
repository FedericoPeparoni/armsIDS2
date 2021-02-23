-- Define new item "Cached event maximum retries" as per technical spec
do $$
declare
    v_query varchar;
begin
    v_query := 'INSERT INTO system_configurations (item_name, item_class, data_type, units, range, default_value, current_value, created_by) VALUES ($1,$2,$3,$4,$5,$6,$7,$8)';
    execute format(v_query) using 'Cached event maximum retries', system_item_type_id('system'), system_data_type_id('int'), 'count', '1,500', '168', '168', 'system';
end $$;

-- Update default value, current value and units in "Cached event retry interval" to match technical spec
UPDATE system_configurations
    SET default_value = '60', current_value = '60', units = 'minutes'
    WHERE item_name = 'Cached event retry interval';
