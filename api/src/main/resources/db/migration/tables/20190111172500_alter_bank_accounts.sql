-- alter bank accounts table: add currency and external identifier
ALTER TABLE bank_accounts
    ADD COLUMN currency_id integer NOT NULL;

ALTER TABLE bank_accounts
    ADD COLUMN external_accounting_system_id character varying(20);

ALTER TABLE bank_accounts
    ADD UNIQUE (external_accounting_system_id);

ALTER TABLE bank_accounts
    ADD FOREIGN KEY (currency_id)
    REFERENCES currencies (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION;
