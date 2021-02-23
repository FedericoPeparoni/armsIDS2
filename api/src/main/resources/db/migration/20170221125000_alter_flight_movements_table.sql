-- Alter table flight_movements
alter table flight_movements alter column radar_route_text type varchar(255);
alter table flight_movements alter column atc_log_route_text type varchar(255);
alter table flight_movements alter column tower_log_route_text type varchar(255);
