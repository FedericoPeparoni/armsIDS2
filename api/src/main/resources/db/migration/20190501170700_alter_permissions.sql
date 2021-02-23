-- US#105883
UPDATE permissions
SET name = 'countries_view'
WHERE name = 'country_view';

--US#105884
-- Delete unused permission
DELETE FROM role_permission WHERE permission_id = (SELECT id FROM permissions WHERE name = 'remote_updates_modify');
DELETE FROM permissions WHERE name = 'remote_updates_modify';

DELETE FROM role_permission WHERE permission_id = (SELECT id FROM permissions WHERE name = 'remote_updates_view');
DELETE FROM permissions WHERE name = 'remote_updates_view';
