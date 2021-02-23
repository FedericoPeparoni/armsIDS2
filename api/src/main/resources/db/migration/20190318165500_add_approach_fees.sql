do $$
declare
    v_query varchar;
    v_airnav_currency_id integer;
begin

    -- define system configuration insert query template
    v_query := 'INSERT INTO system_configurations (item_name, item_class, data_type, units, range, default_value, current_value, created_by) ' ||
               'SELECT $1,$2,$3,$4,$5,$6,$7,$8 WHERE NOT EXISTS (select id from system_configurations where item_name = $1)';

    -- define currency id from ANSP or USD by air navigation charges currency system configuration item
    IF (SELECT current_value FROM system_configurations WHERE item_name = 'Air navigation charges currency') = 'ANSP' THEN
        SELECT cc.id INTO v_airnav_currency_id
            FROM system_configurations s, currencies cc
            WHERE s.item_name = 'ANSP currency' AND cc.currency_code = s.current_value;
    ELSE
        SELECT cc.id INTO v_airnav_currency_id
            FROM currencies cc
            WHERE cc.currency_code = 'USD';
    END IF;

    -- add US100965 system configuration items
    EXECUTE format(v_query) USING 'Domestic approach charges currency', system_item_type_id('ansp'), system_data_type_id('string'), null, 'ANSP,USD', 'ANSP', 'ANSP', 'system';
    EXECUTE format(v_query) USING 'Regional approach charges currency', system_item_type_id('ansp'), system_data_type_id('string'), null, 'ANSP,USD', 'ANSP', 'ANSP', 'system';
    EXECUTE format(v_query) USING 'International approach charges currency', system_item_type_id('ansp'), system_data_type_id('string'), null, 'ANSP,USD', 'ANSP', 'ANSP', 'system';
    EXECUTE format(v_query) USING 'Domestic aerodrome charges currency', system_item_type_id('ansp'), system_data_type_id('string'), null, 'ANSP,USD', 'ANSP', 'ANSP', 'system';
    EXECUTE format(v_query) USING 'Regional aerodrome charges currency', system_item_type_id('ansp'), system_data_type_id('string'), null, 'ANSP,USD', 'ANSP', 'ANSP', 'system';
    EXECUTE format(v_query) USING 'International aerodrome charges currency', system_item_type_id('ansp'), system_data_type_id('string'), null, 'ANSP,USD', 'ANSP', 'ANSP', 'system';
    EXECUTE format(v_query) USING 'Domestic late arrival/departure charges currency', system_item_type_id('ansp'), system_data_type_id('string'), null, 'ANSP,USD', 'ANSP', 'ANSP', 'system';
    EXECUTE format(v_query) USING 'Regional late arrival/departure charges currency', system_item_type_id('ansp'), system_data_type_id('string'), null, 'ANSP,USD', 'ANSP', 'ANSP', 'system';
    EXECUTE format(v_query) USING 'International late arrival/departure charges currency', system_item_type_id('ansp'), system_data_type_id('string'), null, 'ANSP,USD', 'ANSP', 'ANSP', 'system';

    -- add new currency columns, not null constraint will be added later
    ALTER TABLE flight_movements ADD COLUMN aerodrome_charges_currency_id integer;
    ALTER TABLE flight_movements ADD COLUMN approach_charges_currency_id integer;
    ALTER TABLE flight_movements ADD COLUMN late_arrival_departure_charges_currency_id integer;

    -- add foreign key constraints for new columns
    ALTER TABLE flight_movements ADD CONSTRAINT aerodrome_charges_currency_id_fk FOREIGN KEY (aerodrome_charges_currency_id)
        REFERENCES abms.currencies (id) MATCH SIMPLE;
    ALTER TABLE flight_movements ADD CONSTRAINT approach_charges_currency_id_fk FOREIGN KEY (approach_charges_currency_id)
        REFERENCES abms.currencies (id) MATCH SIMPLE;
    ALTER TABLE flight_movements ADD CONSTRAINT late_arrival_departure_charges_currency_id_fk FOREIGN KEY (late_arrival_departure_charges_currency_id)
        REFERENCES abms.currencies (id) MATCH SIMPLE;

    -- update existing flight movement currency columns with original ANSP currency
    UPDATE flight_movements SET
        aerodrome_charges_currency_id = v_airnav_currency_id,
        approach_charges_currency_id = v_airnav_currency_id,
        late_arrival_departure_charges_currency_id = v_airnav_currency_id;

    -- add not null constraint to newly created currency columns
    ALTER TABLE flight_movements ALTER COLUMN aerodrome_charges_currency_id SET NOT NULL;
    ALTER TABLE flight_movements ALTER COLUMN approach_charges_currency_id SET NOT NULL;
    ALTER TABLE flight_movements ALTER COLUMN late_arrival_departure_charges_currency_id SET NOT NULL;

end $$;
