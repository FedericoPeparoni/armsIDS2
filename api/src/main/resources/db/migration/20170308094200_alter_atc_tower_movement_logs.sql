alter table atc_movement_logs alter column operator_icao_code drop not null;

alter table tower_movement_logs alter column operator_icao_code drop not null;
alter table tower_movement_logs alter column departure_aerodrome drop not null;
alter table tower_movement_logs alter column departure_contact_time drop not null;
alter table tower_movement_logs alter column destination_aerodrome drop not null;
alter table tower_movement_logs alter column destination_contact_time drop not null;
