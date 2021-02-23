do $$
declare
    v_query varchar;
begin

    v_query := 'UPDATE system_configurations SET item_class = $1, data_type = $2, range = $3, default_value = $4, current_value = $5, item_name = $6, updated_at = CURRENT_TIMESTAMP, updated_by = ''system'' WHERE item_name = $7';
    execute format(v_query) using system_item_type_id('workflow'), system_data_type_id('string'), 'Approach,Landing,ADAP', 'Approach', 'Approach', 'Approach fees label', 'Approach fee as ADAP';

end $$;
