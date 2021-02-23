do $$
declare
    v_query varchar;
begin
    v_query := 'INSERT INTO system_configurations (item_name, item_class, data_type, units, range, default_value, current_value, created_by) VALUES ($1,$2,$3,$4,$5,$6,$7,$8)';
    execute format(v_query) using 'Require admin approval for self-care accounts', system_item_type_id('self-care'), system_data_type_id('boolean'), null, 't/f', 'f', 'f', 'system';
    execute format(v_query) using 'Require admin approval for self-care aircraft registration', system_item_type_id('self-care'), system_data_type_id('boolean'), null, 't/f', 'f', 'f', 'system';
    execute format(v_query) using 'Require admin approval for self-care flight schedules', system_item_type_id('self-care'), system_data_type_id('boolean'), null, 't/f', 'f', 'f', 'system';
    execute format(v_query) using 'Approval request approval response', system_item_type_id('self-care'), system_data_type_id('string'), null, null, null, null, 'system';
    execute format(v_query) using 'Approval request rejection response', system_item_type_id('self-care'), system_data_type_id('string'), null, null, null, null, 'system';
end $$;
