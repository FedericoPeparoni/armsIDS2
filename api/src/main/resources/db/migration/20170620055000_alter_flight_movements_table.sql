create index if not exists date_of_flight_flight_movements_i1 on flight_movements (date_of_flight);
create index if not exists movement_type_flight_movements_i1 on flight_movements (movement_type);
create index if not exists status_flight_movements_i1 on flight_movements (status);
create index if not exists resolution_errors_flight_movements_i1 on flight_movements (resolution_errors);
