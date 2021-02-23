create table wake_turbulence_categories
(
    id   serial primary key,
    name varchar(2) unique not null
);

insert into wake_turbulence_categories (name) values ('L');
insert into wake_turbulence_categories (name) values ('M');
insert into wake_turbulence_categories (name) values ('V');
insert into wake_turbulence_categories (name) values ('VH');

create table aircraft_types
(
    id                       serial primary key,
    aircraft_type            varchar(5) not null unique,
    aircraft_name            varchar(100) not null,
    aircraft_image           bytea,
    manufacturer             varchar(100) not null,
    wake_turbulence_category int not null references wake_turbulence_categories (id),
    maximum_takeoff_weight   double precision not null,
    created_at timestamp     not null default now(),
    created_by varchar(50)   not null,
    updated_at timestamp,
    updated_by varchar(50)
);

insert into aircraft_types (aircraft_type, aircraft_name, manufacturer, wake_turbulence_category, maximum_takeoff_weight, created_by )
                    values ('A124', 'Antonov AN-124 Ruslan', 'Antonov', 1, 2.4, 'system');
insert into aircraft_types (aircraft_type, aircraft_name, manufacturer, wake_turbulence_category, maximum_takeoff_weight, created_by )
                    values ('A320', 'Airbus A320', 'Airbus', 2, 1.8, 'system');
insert into aircraft_types (aircraft_type, aircraft_name, manufacturer, wake_turbulence_category, maximum_takeoff_weight, created_by )
                    values ('B703', 'Boeing 707', 'Boeing', 3, 2.2, 'system');

create table aircraft_registrations (
    id                       serial primary key,
    registration_number      varchar(10) not null unique,
    registration_start_date  timestamp    not null,
    registration_expiry_date timestamp    not null,
    aircraft_type_id         int not null references aircraft_types (id),
    account_id               int not null references accounts (id),
    mtow_override            double precision not null,
    country_of_registration  int not null references countries (id),
    created_at timestamp     not null default now(),
    created_by varchar(50)   not null,
    updated_at timestamp,
    updated_by varchar(50)
);
