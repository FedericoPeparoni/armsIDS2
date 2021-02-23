do $$
declare
    v_query varchar;
begin
    v_query := 'UPDATE system_configurations SET item_name=$2, range=$3, default_value=$4, current_value=$5, updated_at=$6, updated_by=$7 WHERE item_name=$1';
    execute format(v_query) using 'Departure range for flight match', 'Minimum range for flight match', '0-120', '5', '5', now(), 'system';

    v_query := 'INSERT INTO system_configurations (item_name, item_class, data_type, units, range, default_value, current_value,created_by) VALUES ($1,$2,$3,$4,$5,$6,$7,$8)';
    execute format(v_query) using 'Percentage of EET to be used for flight match', system_item_type_id('default'), system_data_type_id('int'), 'percent', '0-200', '50', '50', 'system';
end $$;
