alter table aircraft_type_exemptions add column approach bool not null default false;
alter table aircraft_type_exemptions add column aerodrome bool not null default false;
alter table aircraft_type_exemptions add column late_arrival bool not null default false;
alter table aircraft_type_exemptions add column late_departure bool not null default false;
alter table aircraft_type_exemptions add column aircraft_type varchar(5) unique not null references aircraft_types (aircraft_type);
alter table aircraft_type_exemptions alter column child set default(true);


alter table aircraft_type_exemptions drop column landing;
alter table aircraft_type_exemptions drop column departure;
alter table aircraft_type_exemptions drop column aircraft_type_id;

 