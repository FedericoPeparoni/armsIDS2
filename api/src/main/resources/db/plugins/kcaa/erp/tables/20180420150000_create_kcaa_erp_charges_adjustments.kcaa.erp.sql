CREATE TABLE IF NOT EXISTS kcaa_erp_charges_adjustments
(
    id serial PRIMARY KEY,
    document_type integer,
    document_no character varying(20),
    line_no integer,
    sales_line_no character varying(20),
    charges_adjustment_id integer NOT NULL,
    created_at timestamp with time zone NOT NULL DEFAULT now(),
    created_by character varying(50) NOT NULL DEFAULT 'system',
    updated_at timestamp with time zone,
    updated_by character varying(50),
    CONSTRAINT kcaa_erp_charges_adjustment_charges_adjustment_fkey FOREIGN KEY (charges_adjustment_id)
        REFERENCES charges_adjustments (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
);
