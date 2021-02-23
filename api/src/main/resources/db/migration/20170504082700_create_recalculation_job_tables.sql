create table job_instances (
    id                                     serial primary key,
    job_key                                varchar(128) not null unique,
    job_name                               varchar(128) not null,
    job_type                               varchar(30) not null,
    job_status                             varchar(30) not null,
    created_at                             timestamp with time zone not null default now(),
    created_by                             varchar(50) not null,
    updated_at                             timestamp with time zone,
    updated_by                             varchar(50)
);

create table job_executions (
    id                                     serial primary key,
    start_guid                             varchar(128) not null unique,
    start_time                             timestamp with time zone,
    stop_time                              timestamp with time zone,
    total_steps                            int,
    steps_to_process                       int,
    steps_completed                        int,
    steps_aborted                          int,
    parameters                             varchar(256),
    job_execution_status                   varchar(15) not null,
    job_instance_id                        int not null references job_instances(id),
    created_at                             timestamp with time zone not null default now(),
    created_by                             varchar(50) not null,
    updated_at                             timestamp with time zone,
    updated_by                             varchar(50)
);

create unique index job_instances_job_key_i1 on job_instances (job_key);
-- create unique index job_executions_start_time_i1 on job_executions (start_time);
