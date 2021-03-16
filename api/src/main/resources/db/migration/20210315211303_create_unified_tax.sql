create table unified_tax
(
    id   serial primary key,
    from_manufacture_year timestamp with time zone,
    to_manufacture_year timestamp with time zone,
    quantity_in_ars integer not null,
    validity_id int not null references account_types (id),
    created_at timestamp with time zone  not null,
    created_by varchar (50)  not null,
    updated_at timestamp with time zone ,
    updated_by varchar (50),
    version bigint 
    
);

