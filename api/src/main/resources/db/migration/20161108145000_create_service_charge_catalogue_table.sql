create table service_charge_catalogues (
    id               serial primary key,
    charge_class     varchar(100) not null,
    category         varchar(100) not null,
    type             varchar(100) not null,
    subtype          varchar(100) not null,
    description      varchar(100) not null,
    minimum_amount   double precision not null,
    maximum_amount   double precision not null,
    amount           double precision not null,
    invoice_template int not null references invoice_templates (id),
    created_at       timestamp not null default now(),
    created_by       varchar(50) not null,
    updated_at       timestamp,
    updated_by       varchar(50)
);
