create table flight_reassignment_aerodromes (
        id                     serial primary key,
        flight_reassignment_id int not null references flight_reassignments (id),
        aerodrome_identifier   varchar(100) not null,                
        created_at             timestamp not null default now(),
        created_by             varchar(50) not null,
        updated_at             timestamp,
        updated_by             varchar(50)
)
