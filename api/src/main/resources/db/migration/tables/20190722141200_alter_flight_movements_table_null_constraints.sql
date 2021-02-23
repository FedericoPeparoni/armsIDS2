alter table flight_movements alter column aerodrome_charges_currency_id drop not null;
alter table flight_movements alter column approach_charges_currency_id drop not null;
alter table flight_movements alter column late_arrival_departure_charges_currency_id drop not null;
alter table flight_movements alter column flight_category_id drop not null;
alter table flight_movements alter column extended_hours_surcharge_currency_id drop not null;
