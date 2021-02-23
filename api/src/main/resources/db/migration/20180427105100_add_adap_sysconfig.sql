do $$
declare
    v_query varchar;
begin
    v_query := 'INSERT INTO system_configurations (item_name, item_class, data_type, units, range, default_value, current_value,created_by) VALUES ($1,$2,$3,$4,$5,$6,$7,$8)';
    execute format(v_query) using 'Apply aerodrome and approach fees on arrival or departure', system_item_type_id('workflow'), system_data_type_id('string'), null, 'Arrival,Departure', 'Departure', 'Departure', 'system';
end $$;

ALTER TABLE aerodromes RENAME COLUMN aerodromecategory_id TO aerodrome_category_id;

ALTER TABLE aerodromecategories RENAME TO aerodrome_categories;
