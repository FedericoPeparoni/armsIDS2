-- create bank codes table, this will be populated per client organization
CREATE TABLE IF NOT EXISTS bank_codes (
    id                               serial PRIMARY KEY,
    code                             character varying(20) NOT NULL,
    description                      character varying(50),
    account_number                   character varying(20) NOT NULL DEFAULT '0',
    branch_code                      character varying(20) NOT NULL DEFAULT '0',
    billing_center_id                integer NOT NULL,
    currency_id                      integer NOT NULL,
    created_at                       timestamp with time zone NOT NULL DEFAULT now(),
    created_by                       character varying(50) NOT NULL DEFAULT 'system',
    updated_at                       timestamp with time zone,
    updated_by                       character varying(50),
    version                          bigint NOT NULL DEFAULT 0
);

-- add foreign key to billing centers
ALTER TABLE bank_codes DROP CONSTRAINT IF EXISTS bank_code_billing_centers_fkey;
ALTER TABLE bank_codes
    ADD CONSTRAINT bank_code_billing_centers_fkey FOREIGN KEY (billing_center_id)
    REFERENCES billing_centers (id) MATCH SIMPLE
    ON DELETE RESTRICT
    ON UPDATE CASCADE;

-- add foreign key to currencies
ALTER TABLE bank_codes DROP CONSTRAINT IF EXISTS bank_code_currencies_fkey;
ALTER TABLE bank_codes
    ADD CONSTRAINT bank_code_currencies_fkey FOREIGN KEY (currency_id)
    REFERENCES currencies (id) MATCH SIMPLE
    ON DELETE RESTRICT
    ON UPDATE CASCADE;
