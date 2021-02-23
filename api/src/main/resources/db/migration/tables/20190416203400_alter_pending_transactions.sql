-- remove exported pending transactions column as it is always false
ALTER TABLE pending_transactions
    DROP COLUMN exported;
