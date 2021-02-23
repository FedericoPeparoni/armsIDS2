alter table flight_movements drop column passengers_joining_child;
alter table flight_movements drop column passengers_transit_child;
alter table flight_movements add column passengers_child int;
