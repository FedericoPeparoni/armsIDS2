create table route_caches
(
    id           serial primary key,
    key_string   varchar(200) not null unique,
    route_object bytea not null,
    created_at   timestamp with time zone not null default now()
);