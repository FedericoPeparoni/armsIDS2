begin;
INSERT INTO system_configurations(item_name, item_class, data_type, units, range, default_value, current_value, created_at, created_by)
VALUES ('Domestic passenger fee percentage', 4, 5, 'ansp', '0-100', '100', '100', now(), 'system');
INSERT INTO system_configurations(item_name, item_class, data_type, units, range, default_value, current_value, created_at, created_by)
VALUES ('International passenger fee percentage', 4, 5, 'ansp', '0-100', '100', '100', now(), 'system');
end;