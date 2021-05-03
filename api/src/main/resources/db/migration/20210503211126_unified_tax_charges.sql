create table unified_tax_charges
(
    id   serial primary key,
    billing_ledger_id integer references billing_ledgers (id) , 
    aircraft_registration_id integer references aircraft_registrations (id),
    amount float ,
    percentage float,
    created_at timestamp with time zone  not null,
    created_by varchar (50)  not null,
    updated_at timestamp with time zone ,
    updated_by varchar (50),
    version bigint,
   FOREIGN KEY (billing_ledger_id) REFERENCES billing_ledgers (id),
   FOREIGN KEY (aircraft_registration_id) REFERENCES aircraft_registrations (id)
);


