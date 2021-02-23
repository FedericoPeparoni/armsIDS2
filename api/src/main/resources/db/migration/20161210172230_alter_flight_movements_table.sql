-- Alter table flight_movements
alter table flight_movements add column account int references accounts (id);
alter table flight_movements drop column if exists associated_account restrict;
