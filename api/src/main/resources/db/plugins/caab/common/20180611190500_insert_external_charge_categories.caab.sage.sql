--------------------------------------------------------------------
-- DO NOT ADD TO MIGRATION CHANGELOG, THIS WILL BE RUN MANUALLY!! --
--------------------------------------------------------------------

-- delete default external charge categories
DELETE FROM account_external_charge_categories;
DELETE FROM external_charge_categories;
UPDATE service_charge_catalogues SET external_charge_category_id = NULL;

-- insert external charge categories for CAAB Sage
INSERT INTO external_charge_categories (name, "unique") VALUES
    ('ENROUTE', true),
    ('IATA', true),
    ('RENT', false),
    ('TELEPHONE', true),
    ('ELECTRICITY', true),
    ('WATER', true),
    ('UTILITIES', true),
    ('AERONAUTICAL', true);

-- each service charge catalogue record will need to be manually updated with an external charge category
