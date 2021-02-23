-- Add "system_summary_view" permission to Administrator role in Role_permission
INSERT INTO role_permission(role_id, permission_id, created_at, created_by)
    VALUES ((SELECT id FROM roles WHERE name = 'Administrator'), (SELECT id FROM permissions WHERE name = 'system_summary_view'), now(), 'system');
