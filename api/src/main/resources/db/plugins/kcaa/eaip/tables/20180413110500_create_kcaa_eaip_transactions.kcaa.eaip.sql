CREATE TABLE abms.kcaa_eaip_transactions
(
    id serial PRIMARY KEY,
    transction_id integer NOT NULL,
    req_number character varying NOT NULL UNIQUE,
    req_currency character varying(10),
    external_database_for_charge character varying NOT NULL,
    req_id integer,
    req_no character varying(50),
    req_status_id integer,
    req_total_amount double precision,
    req_total_amount_converted double precision,
    req_country_id integer,
    req_ar_id integer,
    req_maninfo_id integer,
    created_at timestamp with time zone NOT NULL DEFAULT now(),
    created_by character varying(50) NOT NULL DEFAULT 'system',
    updated_at timestamp with time zone,
    updated_by character varying(50),
    CONSTRAINT kcaa_eaip_transactions_transactions_fkey FOREIGN KEY (transction_id)
        REFERENCES abms.transactions (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
);
