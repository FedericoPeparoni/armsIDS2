insert into abms.role_permission (role_id, permission_id, created_by) values
((SELECT id FROM roles WHERE name = 'Administrator'), (SELECT id FROM permissions WHERE name = 'language_view'), 'system');

insert into abms.role_permission (role_id, permission_id, created_by) values
((SELECT id FROM roles WHERE name = 'Administrator'), (SELECT id FROM permissions WHERE name = 'language_modify'), 'system');
