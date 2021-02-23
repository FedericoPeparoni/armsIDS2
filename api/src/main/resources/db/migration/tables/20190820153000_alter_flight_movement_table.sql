do $$
declare
    v_tasp_charge_currency_id integer;
begin

    -- define USD currency id
    SELECT cc.id INTO v_tasp_charge_currency_id
    FROM currencies cc
    WHERE cc.currency_code = 'USD';

    -- add new currency column, not null constraint will be added later
    ALTER TABLE flight_movements ADD COLUMN tasp_charge_currency_id integer;

    -- add foreign key constraint for new column
    ALTER TABLE flight_movements ADD CONSTRAINT tasp_charge_currency_id_fk FOREIGN KEY (tasp_charge_currency_id)
        REFERENCES currencies (id) MATCH SIMPLE;

    -- update existing flight movement currency column with USD currency
    UPDATE flight_movements SET tasp_charge_currency_id = v_tasp_charge_currency_id;

    -- add not null constraint to newly created currency columns
    ALTER TABLE flight_movements ALTER COLUMN tasp_charge_currency_id SET NOT NULL;

end $$;
