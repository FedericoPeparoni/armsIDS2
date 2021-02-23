--Delete "self_care_access" permission from Administrator

DELETE FROM role_permission
    WHERE role_id = (SELECT id FROM roles WHERE name = 'Administrator') and permission_id = (SELECT id FROM permissions WHERE name = 'self_care_access');
