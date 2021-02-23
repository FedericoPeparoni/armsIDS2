CREATE TABLE IF NOT EXISTS kcaa_erp_transactions
(
    id serial PRIMARY KEY,
    document_type integer,
    sales_header_no character varying(20),
    transaction_id integer NOT NULL,
    created_at timestamp with time zone NOT NULL DEFAULT now(),
    created_by character varying(50) NOT NULL DEFAULT 'system',
    updated_at timestamp with time zone,
    updated_by character varying(50),
    CONSTRAINT kcaa_erp_transaction_transaction_fkey FOREIGN KEY (transaction_id)
        REFERENCES transactions (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
);
