--System Configurations
INSERT INTO system_configurations(item_name, item_class, data_type, units, range, default_value, current_value, created_at, created_by)
    VALUES ('Departure Range for Flight Match', 9, 1, 'minutes', '0-720', '0', '0', now(), 'system');
