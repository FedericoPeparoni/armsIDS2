-- add foreign key to billing centers
ALTER TABLE caab_sage_bank_codes
    ADD COLUMN billing_center_id integer;
ALTER TABLE caab_sage_bank_codes
    ADD CONSTRAINT bank_codes_billing_centers_fkey FOREIGN KEY (billing_center_id)
    REFERENCES billing_centers (id) MATCH SIMPLE
    ON DELETE RESTRICT
    ON UPDATE CASCADE;

-- add foreign key to currencies
ALTER TABLE caab_sage_bank_codes
    ADD COLUMN currency_id integer;
ALTER TABLE caab_sage_bank_codes
    ADD CONSTRAINT bank_codes_currencies_fkey FOREIGN KEY (currency_id)
    REFERENCES currencies (id) MATCH SIMPLE
    ON DELETE RESTRICT
    ON UPDATE CASCADE;
