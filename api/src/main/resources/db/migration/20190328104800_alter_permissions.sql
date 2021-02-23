-- US#104452
UPDATE permissions
SET name = 'group_modify'
WHERE name = 'user_profile_modify';

UPDATE permissions
SET name = 'group_view'
WHERE name = 'user_profile_view';

-- US#101962
-- Insert new permissions into Permissions
INSERT INTO permissions(name, created_at, created_by)
    VALUES ('invoices_approve', now(), 'system');

INSERT INTO permissions(name, created_at, created_by)
    VALUES ('invoices_publish', now(), 'system');

INSERT INTO permissions(name, created_at, created_by)
    VALUES ('invoices_void', now(), 'system');

INSERT INTO permissions(name, created_at, created_by)
    VALUES ('transaction_adjustment_create', now(), 'system');


-- Add new permissions to Administrator role in Role_permission
INSERT INTO role_permission(role_id, permission_id, created_at, created_by)
    VALUES ((SELECT id FROM roles WHERE name = 'Administrator'), (SELECT id FROM permissions WHERE name = 'invoices_approve'), now(), 'system');

INSERT INTO role_permission(role_id, permission_id, created_at, created_by)
    VALUES ((SELECT id FROM roles WHERE name = 'Administrator'), (SELECT id FROM permissions WHERE name = 'invoices_publish'), now(), 'system');

INSERT INTO role_permission(role_id, permission_id, created_at, created_by)
    VALUES ((SELECT id FROM roles WHERE name = 'Administrator'), (SELECT id FROM permissions WHERE name = 'invoices_void'), now(), 'system');

INSERT INTO role_permission(role_id, permission_id, created_at, created_by)
    VALUES ((SELECT id FROM roles WHERE name = 'Administrator'), (SELECT id FROM permissions WHERE name = 'transaction_adjustment_create'), now(), 'system');

-- Delete unused permission
DELETE FROM role_permission WHERE permission_id = (SELECT id FROM permissions WHERE name = 'invoices_modify');
DELETE FROM permissions WHERE name = 'invoices_modify';
