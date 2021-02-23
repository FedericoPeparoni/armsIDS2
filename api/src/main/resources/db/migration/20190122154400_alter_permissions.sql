-- Insert "interest_rate_view" & "interest_rate_modify" into Permissions
INSERT INTO permissions(name, created_at, created_by)
    VALUES ('interest_rate_view', now(), 'system');

INSERT INTO permissions(name, created_at, created_by)
    VALUES ('interest_rate_modify', now(), 'system');


-- Add "interest_rate_view" & "interest_rate_modify" permissions to Administrator role in Role_permission
INSERT INTO role_permission(role_id, permission_id, created_at, created_by)
    VALUES ((SELECT id FROM roles WHERE name = 'Administrator'), (SELECT id FROM permissions WHERE name = 'interest_rate_view'), now(), 'system');

INSERT INTO role_permission(role_id, permission_id, created_at, created_by)
    VALUES ((SELECT id FROM roles WHERE name = 'Administrator'), (SELECT id FROM permissions WHERE name = 'interest_rate_modify'), now(), 'system');
