INSERT INTO roles (name, max_credit_note_amount_approval_limit, max_debit_note_amount_approval_limit, created_by) VALUES
    ('Change the password', 0, 0, 'system');
INSERT INTO permissions (name, created_by) VALUES
    ('change_password', 'system');

-- Add "self_care_access" permission to Self-care operators role in Role_permission
INSERT INTO role_permission(role_id, permission_id, created_at, created_by)
    VALUES ((SELECT id FROM roles WHERE name = 'Change the password'), (SELECT id FROM permissions WHERE name = 'change_password'), now(), 'system');
