--Insert MTOW unit of measure into System Configurations
INSERT INTO system_configurations(item_name, item_class, data_type, units, range, default_value, current_value, created_at, created_by)
    VALUES ('MTOW unit of measure', 12, 2, null, 'tons,kg', 'tons', null, now(), 'system');
