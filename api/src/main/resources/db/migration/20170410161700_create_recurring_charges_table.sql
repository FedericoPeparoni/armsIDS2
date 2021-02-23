create table recurring_charges (
    id serial not null,
    service_charge_catalogue_id integer not null,
    account_id integer not null,
    start_date timestamp with time zone not null,
    end_date timestamp with time zone not null,
    created_at timestamp with time zone not null default now(),
    created_by varchar(50) not null,
    updated_at timestamp with time zone,
    updated_by varchar(50) not null
);

alter table recurring_charges add constraint
    recurring_charges_service_charge_catalogue_fk
        foreign key (service_charge_catalogue_id) references service_charge_catalogues (id)
;

alter table recurring_charges add constraint
    recurring_charges_account_fk
        foreign key (account_id) references accounts (id)
;

create index recurring_charges_account_i1 on invoice_line_items (account_id);
