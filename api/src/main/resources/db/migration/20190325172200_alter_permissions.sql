DELETE FROM role_permission WHERE permission_id = (SELECT id FROM permissions WHERE name = 'aviation_invoice_resolve');
DELETE FROM permissions WHERE name = 'aviation_invoice_resolve';

DELETE FROM role_permission WHERE permission_id = (SELECT id FROM permissions WHERE name = 'nonaviation_invoice_prepare');
DELETE FROM permissions WHERE name = 'nonaviation_invoice_prepare';
