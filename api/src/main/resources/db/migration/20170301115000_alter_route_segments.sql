-- Alter table route_segment
alter table route_segments drop constraint if exists route_segments_flight_movement_fkey, add constraint route_segments_flight_movement_fkey foreign key (flight_movement) references flight_movements(id) on delete cascade;
