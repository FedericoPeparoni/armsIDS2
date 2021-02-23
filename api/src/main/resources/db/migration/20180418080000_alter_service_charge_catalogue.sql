-- alter service charge catalogue to add external accounting system identifier
ALTER TABLE service_charge_catalogues ADD COLUMN external_accounting_system_identifier varchar(20);
