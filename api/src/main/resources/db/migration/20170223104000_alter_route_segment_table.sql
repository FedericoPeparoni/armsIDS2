-- Alter table route_segment
alter table route_segments add column flight_movement int references flight_movements (id);
alter table route_segments drop column if exists flight_record_id restrict;
alter table route_segments rename column segment_lenght to segment_length;
