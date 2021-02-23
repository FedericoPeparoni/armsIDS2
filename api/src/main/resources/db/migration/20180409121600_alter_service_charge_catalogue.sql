-- alter service charge catalogue to add external database type
ALTER TABLE service_charge_catalogues ADD COLUMN external_database_for_charge varchar(30);

-- alter service charge catalogue to add external database type
ALTER TABLE service_charge_catalogues ADD COLUMN external_database_identifier varchar(100);

