-- Alter table flight_movements
alter table flight_movements add column radar_route_text varchar(100);
alter table flight_movements add column atc_log_route_text varchar(100);
alter table flight_movements add column tower_log_route_text varchar(100);
alter table flight_movements add column flight_category varchar(6);