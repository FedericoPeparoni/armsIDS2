-- insert default external charge categories
INSERT INTO external_charge_categories (name, "unique") VALUES
    ('ALL CHARGES', true);

-- update all service charge catalogues to ALL CHARGES
UPDATE service_charge_catalogues
    SET external_charge_category_id = (SELECT id FROM external_charge_categories WHERE name = 'ALL CHARGES' LIMIT 1);

-- migrate legacy external account system identifier data
INSERT INTO account_external_charge_categories(account_id, external_charge_category_id, external_system_identifier)
    SELECT id, (SELECT id FROM external_charge_categories WHERE name = 'ALL CHARGES' LIMIT 1), external_accounting_system_identifier
    FROM accounts
    WHERE external_accounting_system_identifier <> '';

-- remove legacy external accounting system identifier column
ALTER TABLE accounts DROP COLUMN external_accounting_system_identifier;
