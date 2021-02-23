create table billing_centers
(
    id                          serial primary key,
    name                        varchar(100) not null,
    prefix_invoice_number       varchar(50),
    invoice_sequence_number     int,
    created_at 		    	    timestamp with time zone not null default now(),
    updated_at 		    	    timestamp with time zone,
    created_by                  varchar(50),
    updated_by                  varchar(50)
);
