-- change name in the table enroute_air_navigation_charge_formulas

ALTER TABLE enroute_air_navigation_charge_formulas DROP COLUMN IF EXISTS mtow_category;
ALTER TABLE enroute_air_navigation_charge_formulas DROP  CONSTRAINT IF EXISTS enroute_air_navigation_charge_formulas_fk1;
ALTER TABLE enroute_air_navigation_charge_formulas DROP COLUMN IF EXISTS flightmovement_category;
ALTER TABLE enroute_air_navigation_charge_formulas DROP  CONSTRAINT IF EXISTS enroute_air_navigation_charge_formulas_fk2;

ALTER TABLE enroute_air_navigation_charge_formulas ADD COLUMN enroute_charge_category_id integer NOT NULL;
ALTER TABLE enroute_air_navigation_charge_formulas ADD COLUMN flightmovement_category_id integer not null default 0;

ALTER TABLE enroute_air_navigation_charge_formulas ADD
	CONSTRAINT enroute_air_navigation_charge_formulas_fk1 FOREIGN KEY(enroute_charge_category_id)
	      REFERENCES enroute_air_navigation_charge_categories (id) MATCH SIMPLE  ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE enroute_air_navigation_charge_formulas ADD
      CONSTRAINT enroute_air_navigation_charge_formulas_fk2 FOREIGN KEY(flightmovement_category_id)
	      REFERENCES flightmovement_categories (id) MATCH SIMPLE  ON UPDATE NO ACTION ON DELETE NO ACTION; 
	      
-- change names in flightmovement_categories table
ALTER TABLE flightmovement_categories DROP COLUMN IF EXISTS enroute_currency_calculated;
ALTER TABLE flightmovement_categories DROP COLUMN IF EXISTS enroute_currency_invoice;
ALTER TABLE flightmovement_categories DROP CONSTRAINT IF EXISTS flightmovement_categories_fk1;
ALTER TABLE flightmovement_categories DROP CONSTRAINT IF EXISTS flightmovement_categories_fk2;

ALTER TABLE flightmovement_categories ADD COLUMN enroute_result_currency_id integer;
ALTER TABLE flightmovement_categories ADD COLUMN enroute_invoice_currency_id integer;
ALTER TABLE flightmovement_categories ADD
	CONSTRAINT flightmovement_categories_fk1 FOREIGN KEY(enroute_result_currency_id)
	      REFERENCES currencies (id) MATCH SIMPLE  ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE flightmovement_categories ADD
    CONSTRAINT flightmovement_categories_fk2 FOREIGN KEY(enroute_invoice_currency_id)
	      REFERENCES currencies (id) MATCH SIMPLE  ON UPDATE NO ACTION ON DELETE NO ACTION;