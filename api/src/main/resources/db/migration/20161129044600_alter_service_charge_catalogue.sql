create table service_charge_types (
    id   serial primary key,
    name varchar(30) unique not null
);

insert into service_charge_types (name) values ('fixed price');
insert into service_charge_types (name) values ('price per unit');
insert into service_charge_types (name) values ('user entered price');
insert into service_charge_types (name) values ('electricity meter');
insert into service_charge_types (name) values ('water meter');

alter table service_charge_catalogues add column charge_type int not null references service_charge_types (id);
alter table service_charge_catalogues add column charge_amount double precision not null;
alter table service_charge_catalogues add column charge_town int references utilities_towns_and_villages (id);