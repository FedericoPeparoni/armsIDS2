drop table if exists fpl_schedulers;

create table job_locking (
    service_id                             text primary key,
    status                                 text not null,
    process_id                             text,
    last_check                             timestamp with time zone,
    created_at                             timestamp with time zone not null default now(),
    created_by                             varchar(50) not null,
    updated_at                             timestamp with time zone,
    updated_by                             varchar(50)
);
