create table invoice_line_items (
    id serial not null,
    invoice_id integer not null,
    service_charge_catalogue_id integer not null,
    account_id integer not null,
    aerodrome_id integer not null,
    amount double precision not null,
    currency_id integer not null,
    exchange_rate_to_usd double precision not null,
    exchange_rate_to_ansp double precision not null,
    created_at timestamp with time zone not null default now(),
    created_by varchar(50) not null,
    updated_at timestamp with time zone,
    updated_by varchar(50) not null
);

alter table invoice_line_items add constraint
    invoice_line_items_invoice_fk
        foreign key (invoice_id) references billing_ledgers (id)
;
   
alter table invoice_line_items add constraint
    invoice_line_items_service_charge_catalogue_fk
        foreign key (service_charge_catalogue_id) references service_charge_catalogues (id)
;

alter table invoice_line_items add constraint
    invoice_line_items_account_fk
        foreign key (account_id) references accounts (id)
;

alter table invoice_line_items add constraint
    invoice_line_items_aerodrome_fk
        foreign key (aerodrome_id) references aerodromes (id)
;
    


create index invoice_line_items_invoice_i1 on invoice_line_items (invoice_id);
