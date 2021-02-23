-- add new fields in billing_ledgers
ALTER TABLE billing_ledgers ADD COLUMN flight_category_id integer;
	      
ALTER TABLE billing_ledgers  ADD  CONSTRAINT flight_category_fk FOREIGN KEY(flight_category_id)
	      REFERENCES flightmovement_categories (id) MATCH SIMPLE  ON UPDATE NO ACTION ON DELETE NO ACTION;