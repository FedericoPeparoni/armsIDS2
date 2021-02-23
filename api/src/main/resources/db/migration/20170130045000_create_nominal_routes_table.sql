create table nominal_routes (
        id               serial primary key,
        type             varchar(100) not null,
        pointa           varchar(50) not null,
        pointb           varchar(50) not null,
        status           varchar(50) not null,
        nominal_distance double precision not null,
        created_at       timestamp not null default now(),
        created_by       varchar(50) not null,
        updated_at       timestamp,
        updated_by       varchar(50)
)