--Insert new items in System Configurations
do $$
declare
    v_query varchar;
begin
    v_query := 'INSERT INTO system_configurations (item_name, item_class, data_type, units, range, default_value, current_value, created_by) VALUES ($1,$2,$3,$4,$5,$6,$7,$8)';
    execute format(v_query) using 'Default country', system_item_type_id('ansp'), system_data_type_id('string'), null, null, null, null, 'system';
end $$;

-- add column
alter table unspecified_departure_destination_locations add column country_code integer;
alter table unspecified_departure_destination_locations add
	CONSTRAINT unspecified_departure_destination_loc_country_code_fkey FOREIGN KEY (country_code)
      REFERENCES countries (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;
      