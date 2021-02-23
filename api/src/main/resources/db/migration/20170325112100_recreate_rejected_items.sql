drop table rejected_items;

create table rejected_items
(
    id                      serial primary key,
    record_type             VARCHAR(50) NOT NULL,
    rejected_date_time      TIMESTAMP WITH TIME ZONE DEFAULT now() NOT NULL,
    rejected_reason         VARCHAR(100) NOT NULL,
    status                  VARCHAR(12) check (status in ('uncorrected','corrected')),
    originator              VARCHAR(128),
    file_name               VARCHAR(128),
    raw_text                VARCHAR(1024),
    json_text               bytea,
    header                  VARCHAR(1024),
    error_message           VARCHAR(512),
    error_details           VARCHAR(512),

    created_at 		    	timestamp with time zone not null default now(),
    updated_at 		    	timestamp with time zone,
    created_by              varchar(50),
    updated_by              varchar(50)
);
