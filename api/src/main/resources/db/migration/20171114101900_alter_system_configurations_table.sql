do $$
declare
    v_query varchar;
begin

    v_query := 'UPDATE system_configurations SET item_class = $1, updated_at = CURRENT_TIMESTAMP, updated_by = ''system'' WHERE item_name = $2';
    execute format(v_query) using system_item_type_id('upload'), 'Automated upload enabled';

	v_query := 'UPDATE system_configurations SET item_class = $1, data_type = $2, units = $3, range = $4, default_value = $5, current_value = $6, updated_at = CURRENT_TIMESTAMP, updated_by = ''system'' WHERE item_name = $7';
    execute format(v_query) using system_item_type_id('upload'), system_data_type_id('int'), 'minutes', '1-maxint', '60', '60', 'Automated upload scheduling';

end $$;
