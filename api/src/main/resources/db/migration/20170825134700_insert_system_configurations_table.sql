--System Configurations
INSERT INTO system_configurations(item_name, item_class, data_type, units, range, default_value, current_value, created_at, created_by)
    VALUES ('Default billing period', 12, 2, null, 'weekly,monthly', 'weekly', null, now(), 'system');

INSERT INTO system_configurations(item_name, item_class, data_type, units, range, default_value, current_value, created_at, created_by)
    VALUES ('First day of week', 12, 2, null, 'Sunday,Monday,Tuesday,Wednesday,Thursday,Friday,Saturday', 'Monday', null, now(), 'system');
