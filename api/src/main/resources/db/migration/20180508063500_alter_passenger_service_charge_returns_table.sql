alter table passenger_service_charge_returns add column document_contents  bytea;
alter table passenger_service_charge_returns add column document_mime_type varchar(128);
alter table passenger_service_charge_returns add column document_filename  varchar(128);
