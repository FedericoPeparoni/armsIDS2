-- Insert new permissions into Permissions
INSERT INTO permissions(name, created_at, created_by)
    VALUES ('aerodrome_operational_hours_view', now(), 'system');

INSERT INTO permissions(name, created_at, created_by)
    VALUES ('aerodrome_operational_hours_modify', now(), 'system');


-- Add new permissions to Administrator role in Role_permission
INSERT INTO role_permission(role_id, permission_id, created_at, created_by)
    VALUES ((SELECT id FROM roles WHERE name = 'Administrator'), (SELECT id FROM permissions WHERE name = 'aerodrome_operational_hours_view'), now(), 'system');

INSERT INTO role_permission(role_id, permission_id, created_at, created_by)
    VALUES ((SELECT id FROM roles WHERE name = 'Administrator'), (SELECT id FROM permissions WHERE name = 'aerodrome_operational_hours_modify'), now(), 'system');
