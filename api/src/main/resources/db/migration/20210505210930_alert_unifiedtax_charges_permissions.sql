

INSERT INTO permissions(name, created_at, created_by)
    VALUES ('unified_tax_charge_view', now(), 'system');



INSERT INTO role_permission(role_id, permission_id, created_at, created_by)
    VALUES ((SELECT id FROM roles WHERE name = 'Administrator'), (SELECT id FROM permissions WHERE name = 'unified_tax_charge_view'), now(), 'system');