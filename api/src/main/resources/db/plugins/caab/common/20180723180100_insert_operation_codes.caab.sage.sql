--------------------------------------------------------------------
-- DO NOT ADD TO MIGRATION CHANGELOG, THIS WILL BE RUN MANUALLY!! --
--------------------------------------------------------------------

-- NOTE: billing centers names must exists

-- insert operation codes into billing centers' external accounting system identifier
UPDATE billing_centers SET external_accounting_system_identifier = '1000', updated_at = now(), updated_by = 'system', version = version + 1 WHERE name = 'Head Office';
UPDATE billing_centers SET external_accounting_system_identifier = '1100', updated_at = now(), updated_by = 'system', version = version + 1 WHERE name = 'SSKIA';
UPDATE billing_centers SET external_accounting_system_identifier = '1200', updated_at = now(), updated_by = 'system', version = version + 1 WHERE name = 'Maun';
UPDATE billing_centers SET external_accounting_system_identifier = '1300', updated_at = now(), updated_by = 'system', version = version + 1 WHERE name = 'Francistown';
UPDATE billing_centers SET external_accounting_system_identifier = '1400', updated_at = now(), updated_by = 'system', version = version + 1 WHERE name = 'Kasane';
UPDATE billing_centers SET external_accounting_system_identifier = '1500', updated_at = now(), updated_by = 'system', version = version + 1 WHERE name = 'Selebi-Phikwe';
UPDATE billing_centers SET external_accounting_system_identifier = '1600', updated_at = now(), updated_by = 'system', version = version + 1 WHERE name = 'Ghanzi';
