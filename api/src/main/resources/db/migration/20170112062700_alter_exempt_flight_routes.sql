alter table exempt_flight_routes alter column enroute_fees_are_exempt set default(false);
alter table exempt_flight_routes alter column parking_fees_are_exempt set default(false);
alter table exempt_flight_routes alter column child_passenger_fees_exempt drop default;
alter table exempt_flight_routes alter column child_passenger_fees_exempt set default(true);
alter table exempt_flight_routes rename column landing_fees_are_exempt TO late_arrival_fees_exempt;
alter table exempt_flight_routes rename column departure_fees_are_exempt TO late_departure_fees_exempt;
alter table exempt_flight_routes alter column late_arrival_fees_exempt set default(true);
alter table exempt_flight_routes alter column late_departure_fees_exempt set default(true);
