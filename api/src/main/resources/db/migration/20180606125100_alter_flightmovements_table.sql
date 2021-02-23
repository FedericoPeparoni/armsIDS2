-- insert default flight category
INSERT INTO flightmovement_categories (id, name,sort_order,short_name,created_by) VALUES (0,'OTHER',100, 'OT','admin');

-- add new fields in flight_movements
ALTER TABLE flight_movements ADD COLUMN flight_category_type varchar(2);
ALTER TABLE flight_movements ADD COLUMN flight_category_scope varchar(2);
ALTER TABLE flight_movements ADD COLUMN flight_category_nationality varchar(2);
ALTER TABLE flight_movements ADD COLUMN flight_category_id integer NOT NULL default 0;
ALTER TABLE flight_movements ADD COLUMN enroute_result_currency_id  integer;
ALTER TABLE flight_movements ADD COLUMN enroute_invoice_currency_id integer;

ALTER TABLE flight_movements ADD CONSTRAINT enroute_result_currency_fk FOREIGN KEY(enroute_result_currency_id)
	      REFERENCES currencies (id) MATCH SIMPLE  ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE flight_movements  ADD  CONSTRAINT enroute_invoice_currency_fk FOREIGN KEY(enroute_invoice_currency_id)
	      REFERENCES currencies (id) MATCH SIMPLE  ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE flight_movements  ADD  CONSTRAINT flight_category_fk FOREIGN KEY(flight_category_id)
	      REFERENCES flightmovement_categories (id) MATCH SIMPLE  ON UPDATE NO ACTION ON DELETE NO ACTION;