CREATE TABLE charges_adjustments
(
    id serial PRIMARY KEY,
    transaction_id integer,
    billing_ledger_id integer,
    date timestamp with time zone,
    flight_id character varying(30),
    aerodrome character varying(30),
    charge_description character varying(300),
    charge_amount double precision,

    CONSTRAINT charges_adjustments_transactions_fkey FOREIGN KEY (transaction_id)
        REFERENCES transactions (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT charges_adjustments_billing_ledger_fkey FOREIGN KEY (billing_ledger_id)
        REFERENCES billing_ledgers (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
);
