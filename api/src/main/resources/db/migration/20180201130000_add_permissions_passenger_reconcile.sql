-- Insert "passenger_revenue_reconcile" into Permissions
INSERT INTO permissions(name, created_at, created_by)
    VALUES ('passenger_revenue_reconcile', now(), 'system');

-- Add "passenger_revenue_reconcile" permissions to Administrator role in Role_permission
INSERT INTO role_permission(role_id, permission_id, created_at, created_by)
    VALUES ((SELECT id FROM roles WHERE name = 'Administrator'), (SELECT id FROM permissions WHERE name = 'passenger_revenue_reconcile'), now(), 'system');
