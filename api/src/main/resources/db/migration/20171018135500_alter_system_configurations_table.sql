-- Update item_name in System Configurations
UPDATE system_configurations SET
    item_name = 'Default billing period for NON-IATA invoices'
WHERE id = 65;

UPDATE system_configurations SET
    item_name = 'First day of week for NON-IATA invoices'
WHERE id = 66;

--Insert new items in System Configurations
INSERT INTO system_configurations(item_name, item_class, data_type, units, range, default_value, current_value, created_at, created_by)
    VALUES ('Default billing period for IATA invoices', 12, 2, null, 'weekly,monthly', 'monthly', null, now(), 'system');

INSERT INTO system_configurations(item_name, item_class, data_type, units, range, default_value, current_value, created_at, created_by)
    VALUES ('First day of week for IATA invoices', 12, 2, null, 'Sunday,Monday,Tuesday,Wednesday,Thursday,Friday,Saturday', 'Monday', null, now(), 'system');
