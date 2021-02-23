-- add billing center foreign key reference
ALTER TABLE billing_ledgers
    ADD COLUMN billing_center_id integer;
ALTER TABLE billing_ledgers
    ADD CONSTRAINT billing_ledgers_billing_center_id_fkey FOREIGN KEY (billing_center_id)
    REFERENCES billing_centers (id) MATCH SIMPLE
    ON UPDATE CASCADE
    ON DELETE RESTRICT;

-- update all existing billing ledgers to use the billing ledger of the user
UPDATE billing_ledgers bl
    SET billing_center_id = usr.billing_center_id
    FROM users usr
    WHERE bl.user_id = usr.id
    AND bl.billing_center_id IS NULL;
