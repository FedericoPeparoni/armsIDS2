INSERT INTO roles (name, max_credit_note_amount_approval_limit, max_debit_note_amount_approval_limit, created_by) VALUES
    ('Self-care operators', 0, 0, 'system');

-- Add "self_care_access" permission to Self-care operators role in Role_permission
INSERT INTO role_permission(role_id, permission_id, created_at, created_by)
    VALUES ((SELECT id FROM roles WHERE name = 'Self-care operators'), (SELECT id FROM permissions WHERE name = 'self_care_access'), now(), 'system');
