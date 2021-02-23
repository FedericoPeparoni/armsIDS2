CREATE TABLE abms.kcaa_aatis_transactions
(
    id serial PRIMARY KEY,
    transction_id integer NOT NULL,
    invoice_permit_number character varying NOT NULL UNIQUE,
    external_database_for_charge character varying NOT NULL,
    adhoc_total_fee_payment_amount double precision NOT NULL,
    created_at timestamp with time zone NOT NULL DEFAULT now(),
    created_by character varying(50) NOT NULL DEFAULT 'system',
    updated_at timestamp with time zone,
    updated_by character varying(50),
    CONSTRAINT kcaa_aatis_transactions_transactions_fkey FOREIGN KEY (transction_id)
        REFERENCES abms.transactions (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
);
