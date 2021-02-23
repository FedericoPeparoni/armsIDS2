-- delete foreign key usage in role_permission  table
DELETE FROM role_permission WHERE permission_id IN (
    SELECT id FROM permissions WHERE name IN ('role_view', 'role_modify'));

-- delete from permissions table
DELETE FROM permissions WHERE name IN ('role_view', 'role_modify');
