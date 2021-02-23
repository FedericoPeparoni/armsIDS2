-- US#101959
UPDATE permissions
SET name = 'flight_movement_modify'
WHERE name = 'flight_modify';

UPDATE permissions
SET name = 'flight_movement_view'
WHERE name = 'flight_view';

-- US#101963
DELETE FROM role_permission WHERE permission_id = (SELECT id FROM permissions WHERE name = 'landing_charges_view');
DELETE FROM permissions WHERE name = 'landing_charges_view';

DELETE FROM role_permission WHERE permission_id = (SELECT id FROM permissions WHERE name = 'landing_charges_modify');
DELETE FROM permissions WHERE name = 'landing_charges_modify';

DELETE FROM role_permission WHERE permission_id = (SELECT id FROM permissions WHERE name = 'registration_view');
DELETE FROM permissions WHERE name = 'registration_view';

DELETE FROM role_permission WHERE permission_id = (SELECT id FROM permissions WHERE name = 'registration_modify');
DELETE FROM permissions WHERE name = 'registration_modify';
