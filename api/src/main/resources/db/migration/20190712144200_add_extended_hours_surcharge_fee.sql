do $$
declare
    v_query varchar;
    v_extended_hours_currency_id integer;
begin

    -- define system configuration insert query template
    v_query := 'INSERT INTO system_configurations (item_name, item_class, data_type, units, range, default_value, current_value, created_by) ' ||
               'SELECT $1,$2,$3,$4,$5,$6,$7,$8 WHERE NOT EXISTS (select id from system_configurations where item_name = $1)';

    -- define currency id from ANSP or USD by air navigation charges currency system configuration item
    IF (SELECT current_value FROM system_configurations WHERE item_name = 'Air navigation charges currency') = 'ANSP' THEN
        SELECT cc.id INTO v_extended_hours_currency_id
            FROM system_configurations s, currencies cc
            WHERE s.item_name = 'ANSP currency' AND cc.currency_code = s.current_value;
    ELSE
        SELECT cc.id INTO v_extended_hours_currency_id
            FROM currencies cc
            WHERE cc.currency_code = 'USD';
    END IF;

    -- add US107786 system configuration items
    execute format(v_query) using 'Extended hours surcharge support', system_item_type_id('workflow'), system_data_type_id('boolean'), null, 't/f', 'f', 'f', 'system';
    EXECUTE format(v_query) USING 'Domestic extended hours surcharge currency', system_item_type_id('ansp'), system_data_type_id('string'), null, 'ANSP,USD', 'ANSP', 'ANSP', 'system';
    EXECUTE format(v_query) USING 'Regional extended hours surcharge currency', system_item_type_id('ansp'), system_data_type_id('string'), null, 'ANSP,USD', 'ANSP', 'ANSP', 'system';
    EXECUTE format(v_query) USING 'International extended hours surcharge currency', system_item_type_id('ansp'), system_data_type_id('string'), null, 'ANSP,USD', 'ANSP', 'ANSP', 'system';

    -- add new currency columns, not null constraint will be added later
    ALTER TABLE flight_movements ADD COLUMN extended_hours_surcharge double precision;
    ALTER TABLE flight_movements ADD COLUMN extended_hours_surcharge_currency_id integer;

    -- add foreign key constraint for new column
    ALTER TABLE flight_movements ADD CONSTRAINT extended_hours_surcharge_currency_id_fk FOREIGN KEY (extended_hours_surcharge_currency_id)
        REFERENCES currencies (id) MATCH SIMPLE;

    -- update existing flight movement currency column with original ANSP currency
    UPDATE flight_movements SET extended_hours_surcharge_currency_id = v_extended_hours_currency_id;

    -- add not null constraint to newly created currency columns
    ALTER TABLE flight_movements ALTER COLUMN extended_hours_surcharge_currency_id SET NOT NULL;

end $$;
