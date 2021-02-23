create table navigation_billing_formulas (
    id                                      serial primary key,
    upper_limit                             double precision unique not null,
    domestic_formula                        varchar(255),
    regional_departure_formula              varchar(255),
    regional_arrival_formula                varchar(255),
    regional_overflight_formula             varchar(255),
    international_departure_formula         varchar(255),
    international_arrival_formula           varchar(255),
    international_overflight_formula        varchar(255),
    created_at                              timestamp    not null default now(),
    created_by                              varchar(50)  not null,
    updated_at                              timestamp,
    updated_by                              varchar(50)
);
