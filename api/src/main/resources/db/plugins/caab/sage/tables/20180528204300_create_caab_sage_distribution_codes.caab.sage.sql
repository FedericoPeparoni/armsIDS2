create table caab_sage_distribution_codes (
    id                               serial primary key,
    code                             character varying(50) not null unique,
    abbrev                           character varying(20) not null,
    description                      character varying(100),
    category                         character varying(20) not null,
    created_at                       timestamp with time zone not null default now(),
    created_by                       character varying(50) not null default 'system',
    updated_at                       timestamp with time zone,
    updated_by                       character varying(50),
    version                          bigint NOT NULL DEFAULT 0
);
