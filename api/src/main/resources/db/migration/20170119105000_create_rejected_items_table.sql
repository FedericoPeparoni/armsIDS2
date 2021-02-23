create table rejected_items
(
    id                      serial primary key,
	record_type				varchar(50) not null,
	rejected_date_time		timestamp with time zone not null default now(),
	rejected_reason         varchar(100) not null,
	record_text             varchar(50) not null,
	status                  varchar(12) check (status in ('uncorrected','corrected')),
    created_at 		    	timestamp with time zone not null default now(),
    updated_at 		    	timestamp with time zone,
    created_by              varchar(50),
    updated_by              varchar(50)
);

