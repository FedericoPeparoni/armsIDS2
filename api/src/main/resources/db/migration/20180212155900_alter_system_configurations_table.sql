--Insert new items in System Configurations
INSERT INTO system_configurations(item_name, item_class, data_type, units, range, default_value, current_value, created_at, created_by)
    VALUES (
    'Calculate overdue invoice penalties daily',
    system_item_type_id('workflow'),
    system_data_type_id('boolean'),
    'null',
    't/f',
    'f',
    'f',
    now(),
    'system'
    );
