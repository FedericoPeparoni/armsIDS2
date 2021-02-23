alter table flight_movements add column approach_charges numeric(10,2);
alter table flight_movements add column aerodrome_charges numeric(10,2);
alter table flight_movements add column late_arrival_charges numeric(10,2);
alter table flight_movements add column late_departure_charges numeric(10,2);
alter table flight_movements add column exempt_aerodrome_charges numeric(10,2);
alter table flight_movements add column exempt_late_arrival_charges numeric(10,2);
alter table flight_movements add column exempt_late_departure_charges numeric(10,2);
alter table flight_movements add column delta_dest_ad  character varying(20);

