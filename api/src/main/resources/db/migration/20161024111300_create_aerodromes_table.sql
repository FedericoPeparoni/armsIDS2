create table aerodromes (
    id                      serial primary key,
    aerodrome_name          varchar(100) unique not null,
    aixm_flag               bool not null,
    latitude                double precision not null,
    longitude               double precision not null,
    created_at timestamp    not null default now(),
    created_by varchar(50)  not null,
    updated_at timestamp,
    updated_by varchar(50)
);

create table aerodromecategories (
    id                                 serial primary key,
    category_name                      varchar(100) unique not null,
    international_passenger_fee_adult  double precision not null,
    international_passenger_fee_child  double precision not null,
    domestic_passenger_fee_adult       double precision not null,
    domestic_passenger_fee_child       double precision not null,
    created_at timestamp               not null default now(),
    created_by varchar(50)             not null,
    updated_at timestamp,
    updated_by varchar(50)
);

create table aerodrome_aerodromecategories (
    id                     serial primary key,
    aerodrome_id           int not null references aerodromes (id),
    aerodromecategory_id   int not null references aerodromecategories (id),
    created_at timestamp   not null default now(),
    created_by varchar(50) not null,
    updated_at timestamp,
    updated_by varchar(50),
    unique (aerodrome_id, aerodromecategory_id)
);