create table countries (
    id                      serial primary key,
    country_code            varchar(3) unique not null,
    country_name            varchar(25) unique not null,
    created_at timestamp    not null default now(),
    created_by varchar(50)  not null,
    updated_at timestamp,
    updated_by varchar(50)
);

insert into countries (country_code, country_name, created_by) values ('CAN', 'Canada', 'system');
insert into countries (country_code, country_name, created_by) values ('USA', 'United States of America', 'system');
