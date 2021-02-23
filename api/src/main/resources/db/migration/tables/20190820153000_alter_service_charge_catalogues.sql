do $$

declare
	v_psc_currency integer;
begin

-- add currency column
ALTER TABLE service_charge_catalogues ADD COLUMN currency integer;

-- get currency id from sys config ansp
SELECT cc.id INTO v_psc_currency
FROM system_configurations s, currencies cc
WHERE s.item_name = 'ANSP currency' AND cc.currency_code = s.current_value;

-- update all records with the ANSP currency where the currency is NULL
UPDATE service_charge_catalogues SET currency = v_psc_currency WHERE currency IS NULL;

-- set as not null
ALTER TABLE service_charge_catalogues ALTER COLUMN currency SET NOT NULL;

-- Add constraint to currency column
ALTER TABLE service_charge_catalogues ADD CONSTRAINT currency_fk FOREIGN KEY(currency)
	      REFERENCES currencies (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION;

end $$