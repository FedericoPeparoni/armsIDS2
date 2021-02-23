drop table if exists languages;

create table languages (
    id                    	 	serial primary key,
    code                        varchar(2) not null,
    token                       varchar(512) not null,
    val                         varchar(512) not null,
    part                        varchar(12),
    created_at            	    timestamp with time zone not null default now(),
    created_by            		varchar(50) not null default 'system',
    updated_at            		timestamp,
    updated_by            		varchar(50)
);

alter table languages ADD constraint distinct_language_values unique (token, code, part);
