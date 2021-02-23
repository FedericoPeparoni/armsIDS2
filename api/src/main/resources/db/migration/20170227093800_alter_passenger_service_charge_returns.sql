alter table passenger_service_charge_returns alter column day_of_flight type timestamp with time zone;
alter table passenger_service_charge_returns alter column day_of_flight set not null;
alter table passenger_service_charge_returns drop column departure_time;
alter table passenger_service_charge_returns add column departure_time varchar(4);
