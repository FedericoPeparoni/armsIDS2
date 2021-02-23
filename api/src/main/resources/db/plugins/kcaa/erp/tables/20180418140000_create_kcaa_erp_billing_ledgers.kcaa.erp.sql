CREATE TABLE IF NOT EXISTS kcaa_erp_billing_ledgers
(
    id serial PRIMARY KEY,
    document_type integer,
    sales_header_no character varying(20),
    billing_ledger_id integer NOT NULL,
    created_at timestamp with time zone NOT NULL DEFAULT now(),
    created_by character varying(50) NOT NULL DEFAULT 'system',
    updated_at timestamp with time zone,
    updated_by character varying(50),
    CONSTRAINT kcaa_erp_billing_ledger_billing_ledger_fkey FOREIGN KEY (billing_ledger_id)
        REFERENCES billing_ledgers (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
);
