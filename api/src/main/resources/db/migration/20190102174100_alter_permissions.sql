-- Insert "manage_service_outages_view" & "manage_service_outages_modify" into Permissions
INSERT INTO permissions(name, created_at, created_by)
    VALUES ('aerodrome_service_outage_view', now(), 'system');

INSERT INTO permissions(name, created_at, created_by)
    VALUES ('aerodrome_service_outage_modify', now(), 'system');


-- Add "manage_service_outages_view" & "manage_service_outages_modify" permissions to Administrator role in Role_permission
INSERT INTO role_permission(role_id, permission_id, created_at, created_by)
    VALUES ((SELECT id FROM roles WHERE name = 'Administrator'), (SELECT id FROM permissions WHERE name = 'aerodrome_service_outage_view'), now(), 'system');

INSERT INTO role_permission(role_id, permission_id, created_at, created_by)
    VALUES ((SELECT id FROM roles WHERE name = 'Administrator'), (SELECT id FROM permissions WHERE name = 'aerodrome_service_outage_modify'), now(), 'system');
