CREATE TABLE IF NOT EXISTS kcaa_erp_invoice_line_items
(
    id serial PRIMARY KEY,
    document_type integer,
    document_no character varying(20),
    line_no integer,
    sales_line_no character varying(20),
    invoice_line_item_id integer NOT NULL,
    created_at timestamp with time zone NOT NULL DEFAULT now(),
    created_by character varying(50) NOT NULL DEFAULT 'system',
    updated_at timestamp with time zone,
    updated_by character varying(50),
    CONSTRAINT kcaa_erp_invoice_line_items_ivoice_line_items_fkey FOREIGN KEY (invoice_line_item_id)
        REFERENCES invoice_line_items (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
);
