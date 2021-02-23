create table fpl_schedulers (
    id                                     serial primary key,
    status                                 varchar(30) not null,
    process_id                             varchar,
    last_check                             timestamp with time zone,
    created_at                             timestamp with time zone not null default now(),
    created_by                             varchar(50) not null,
    updated_at                             timestamp with time zone,
    updated_by                             varchar(50)
);

insert into fpl_schedulers (id, status, created_at, created_by)
    values (1, 'WAITING', now(), 'system');
