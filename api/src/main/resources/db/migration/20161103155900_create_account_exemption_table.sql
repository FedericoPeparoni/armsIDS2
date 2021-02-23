create table account_exemptions (
    id                 serial primary key,
    account_id         int not null references accounts (id),
    enroute            bool not null,
    landing            bool not null,
    departure          bool not null,
    parking            bool not null,
    adult              bool not null,
    child              bool not null,
    flight_notes        varchar(255),
    created_at         timestamp    not null default now(),
    created_by         varchar(50)  not null,
    updated_at         timestamp,
    updated_by         varchar(50)
);
