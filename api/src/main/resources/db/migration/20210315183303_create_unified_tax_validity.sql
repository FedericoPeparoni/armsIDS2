create table unified_tax_validity
(
    id   serial primary key,
    from_validity_year timestamp with time zone not null,
    to_validity_year timestamp with time zone not null,
    created_at timestamp with time zone  not null,
    created_by varchar (50)  not null,
    updated_at timestamp with time zone ,
    updated_by varchar (50),
    version bigint 
    
);
