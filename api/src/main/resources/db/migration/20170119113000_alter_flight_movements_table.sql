-- Alter table flight_movements
alter table flight_movements add column billable_entry_point varchar(20);
alter table flight_movements add column billable_exit_point varchar(20);

alter table flight_movements drop column entry_point;
alter table flight_movements drop column exit_point;

