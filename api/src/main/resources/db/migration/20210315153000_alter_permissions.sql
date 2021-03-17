--US114981

-- Insert "unified_tax_view" and "unified_tax_modify" into Permissions
INSERT INTO permissions(name, created_at, created_by)
    VALUES ('unified_tax_view', now(), 'system');
INSERT INTO permissions(name, created_at, created_by)
    VALUES ('unified_tax_modify', now(), 'system');

-- Add "unified_tax_view" and "unified_tax_modify" permissions to Administrator role in Role_permission
INSERT INTO role_permission(role_id, permission_id, created_at, created_by)
    VALUES ((SELECT id FROM roles WHERE name = 'Administrator'), (SELECT id FROM permissions WHERE name = 'unified_tax_view'), now(), 'system');
INSERT INTO role_permission(role_id, permission_id, created_at, created_by)
    VALUES ((SELECT id FROM roles WHERE name = 'Administrator'), (SELECT id FROM permissions WHERE name = 'unified_tax_modify'), now(), 'system');
