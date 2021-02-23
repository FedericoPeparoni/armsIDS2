alter table exempt_flight_routes add approach_fees_exempt bool not null default(false);
alter table exempt_flight_routes add aerodrome_fees_exempt bool not null default(false);
alter table exempt_flight_routes add adult_passenger_fees_exempt bool not null default(false);
alter table exempt_flight_routes add child_passenger_fees_exempt bool not null default(false);
alter table exempt_flight_routes add special_handling_indicator varchar(15);
