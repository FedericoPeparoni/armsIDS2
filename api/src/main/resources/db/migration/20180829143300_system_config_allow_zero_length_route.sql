--System Configurations
INSERT INTO system_configurations(item_name, item_class, data_type, units, range, default_value, current_value, created_at, created_by)
    VALUES ('Ignore zero-length track on fixed cost flights', 12, 3, null, 't/f', 't', 't', now(), 'system');
