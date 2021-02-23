-- Alter table flight_movements
alter table flight_movements drop column if exists manually_changed_fields;
alter table flight_movements drop column if exists resolution_errors;

alter table flight_movements add column flight_level varchar(5);
alter table flight_movements add column manually_changed_fields text;
alter table flight_movements add column resolution_errors text;
