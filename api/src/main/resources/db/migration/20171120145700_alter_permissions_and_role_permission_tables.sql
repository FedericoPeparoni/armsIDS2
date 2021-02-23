-- Insert "local_acreg_view" and "local_acreg_modify" into Permissions
INSERT INTO permissions(name, created_at, created_by)
    VALUES ('local_acreg_modify', now(), 'system');

INSERT INTO permissions(name, created_at, created_by)
    VALUES ('local_acreg_view', now(), 'system');

-- Add "local_acreg_view" and "local_acreg_modify" permissions to Administrator role in Role_permission
INSERT INTO role_permission(role_id, permission_id, created_at, created_by)
    VALUES ((SELECT id FROM roles WHERE name = 'Administrator'), (SELECT id FROM permissions WHERE name = 'local_acreg_modify'), now(), 'system');

INSERT INTO role_permission(role_id, permission_id, created_at, created_by)
    VALUES ((SELECT id FROM roles WHERE name = 'Administrator'), (SELECT id FROM permissions WHERE name = 'local_acreg_view'), now(), 'system');
