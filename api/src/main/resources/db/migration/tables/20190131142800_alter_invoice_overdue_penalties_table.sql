ALTER TABLE invoice_overdue_penalties RENAME COLUMN penalty_amount TO default_penalty_amount;
ALTER TABLE invoice_overdue_penalties ADD COLUMN punitive_penalty_amount double precision NOT NULL DEFAULT 0.0;
ALTER TABLE invoice_overdue_penalties DROP COLUMN penalty_rate;
