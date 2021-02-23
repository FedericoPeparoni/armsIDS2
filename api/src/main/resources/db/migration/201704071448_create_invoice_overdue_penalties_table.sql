create table invoice_overdue_penalties (
    id                    	 	serial primary key,
    penalized_invoice_id     	int references billing_ledgers (id) not null,
    penalty_added_to_invoice_id int references billing_ledgers (id) not null,    
	penalty_applied_date        timestamp with time zone not null,
	penalty_period_end_date     timestamp with time zone not null,
	penalty_amount              double precision not null,
	penalty_rate                double precision not null,
	penalty_number_of_months    int not null,	
    created_at            	    timestamp with time zone not null default now(),
    created_by            		varchar(50) not null,
    updated_at            		timestamp,
    updated_by            		varchar(50)
);

alter table invoice_overdue_penalties add constraint invoice_overdue_penalties_chk1
    check (penalized_invoice_id <> penalty_added_to_invoice_id);