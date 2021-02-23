--System Configurations
INSERT INTO system_configurations(item_name, item_class, data_type, units, range, default_value, current_value, created_at, created_by)
    VALUES ('Backdate payment allowed', 12, 3, null, 't/f', 't', 't', now(), 'system');

-- Add payment date column
    ALTER TABLE transactions
    ADD COLUMN payment_date timestamp with time zone;