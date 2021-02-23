BEGIN TRANSACTION;
DO $$ BEGIN

-- add columns without NOT NULL constraint if they don't exist already

BEGIN
    ALTER TABLE transactions
        ADD COLUMN payment_amount double precision;
EXCEPTION
    WHEN duplicate_column THEN RAISE NOTICE 'column ''payment_amount'' already exists in ''transactions''.';
END;

BEGIN
    ALTER TABLE transactions
        ADD COLUMN payment_currency_id integer;
EXCEPTION
    WHEN duplicate_column THEN RAISE NOTICE 'column ''payment_currency_id'' already exists in ''transactions''.';
END;

BEGIN
    ALTER TABLE transactions
        ADD COLUMN payment_exchange_rate double precision;
EXCEPTION
    WHEN duplicate_column THEN RAISE NOTICE 'column ''payment_exchange_rate'' already exists in ''transactions''.';
END;

-- add foreign key constraint to payment currency id

ALTER TABLE transactions
    DROP CONSTRAINT IF EXISTS transactions_payment_currency_id_fkey;
ALTER TABLE transactions
    ADD CONSTRAINT transactions_payment_currency_id_fkey FOREIGN KEY (payment_currency_id)
    REFERENCES currencies (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION;


-- update new columns if not set

UPDATE transactions
    SET payment_amount = COALESCE(payment_amount, amount),
        payment_currency_id = COALESCE(payment_currency_id, currency_id),
        payment_exchange_rate = COALESCE(payment_exchange_rate, 1)
    WHERE payment_amount IS NULL
        OR payment_currency_id IS NULL
        OR payment_exchange_rate IS NULL;


-- add NOT NULL constraint to new columns

ALTER TABLE transactions
    ALTER COLUMN payment_amount SET NOT NULL;

ALTER TABLE transactions
    ALTER COLUMN payment_currency_id SET NOT NULL;

ALTER TABLE transactions
    ALTER COLUMN payment_exchange_rate SET NOT NULL;

END $$;
COMMIT;
