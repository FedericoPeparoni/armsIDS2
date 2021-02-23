ALTER TABLE transaction_payments
    ADD COLUMN exported boolean NOT NULL DEFAULT false;
