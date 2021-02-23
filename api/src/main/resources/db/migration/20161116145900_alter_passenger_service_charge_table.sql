-- Add default values to prevent a fault because null values
alter table passenger_service_charge_returns alter column flight_id set default 0;
alter table passenger_service_charge_returns alter column day_of_flight set default now();
alter table passenger_service_charge_returns alter column departure_time set default now();

-- Add the not null and unique constraints
alter table passenger_service_charge_returns alter column flight_id set not null;
alter table passenger_service_charge_returns alter column day_of_flight set not null;
alter table passenger_service_charge_returns alter column departure_time set not null;
alter table passenger_service_charge_returns add constraint unique_fields unique (flight_id, day_of_flight, departure_time);

-- Remove the default values
alter table passenger_service_charge_returns alter column flight_id drop default;
alter table passenger_service_charge_returns alter column day_of_flight drop default;
alter table passenger_service_charge_returns alter column departure_time drop default;
