-- Insert "countries_modify" into Permissions
INSERT INTO permissions(name, created_at, created_by)
    VALUES ('countries_modify', now(), 'system');

-- Add "countries_modify" permissions to Administrator role in Role_permission
INSERT INTO role_permission(role_id, permission_id, created_at, created_by)
    VALUES ((SELECT id FROM roles WHERE name = 'Administrator'), (SELECT id FROM permissions WHERE name = 'countries_modify'), now(), 'system');
