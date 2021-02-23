drop table if exists service_charge_catalogues;
drop table if exists service_charge_types;

create table service_charge_catalogues (
    id                  serial primary key,
    charge_class        varchar(100) not null,
    category            varchar(100) not null,
    type                varchar(100) not null,
    subtype             varchar(100) not null,
    description         varchar(100) not null unique,
    charge_basis        varchar(15)  not null,
    minimum_amount      double precision not null,
    maximum_amount      double precision not null,
    amount              double precision,
    invoice_category    character varying(15) not null,

    created_at      timestamp not null default now(),
    created_by      varchar(50) not null,
    updated_at      timestamp,
    updated_by      varchar(50)
);
