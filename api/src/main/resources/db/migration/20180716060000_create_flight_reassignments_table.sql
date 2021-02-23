create table flight_reassignments
(
    id                              serial primary key,
    applies_to_type_arrival         bool not null,
    applies_to_type_departure       bool not null,
    applies_to_type_domestic        bool not null,
    applies_to_type_overflight      bool not null,
    applies_to_scope_domestic       bool not null,
    applies_to_scope_regional       bool not null,
    applies_to_scope_international  bool not null,
    applies_to_nationality_national bool not null,
    applies_to_nationality_foreign  bool not null,
    identification_type             varchar not null check (identification_type in ('ICAO code','Flight Id','Registration')),
    identifier_text                 varchar not null,
    aerodrome                       varchar,
    reassignment_start_date         timestamp with time zone not null,
    reassignment_end_date           timestamp with time zone not null,
    version                         bigint not null default 0,
    account_id                      int not null references accounts (id),
    created_at                      timestamp with time zone not null default now(),
    created_by                      varchar(50) not null,
    updated_at                      timestamp with time zone,
    updated_by                      varchar(50)
)
