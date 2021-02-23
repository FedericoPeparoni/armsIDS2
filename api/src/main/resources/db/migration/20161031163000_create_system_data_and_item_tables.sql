create table system_item_types (
    id   serial primary key,
    name varchar(30) not null unique
);

create table system_data_types (
    id   serial primary key,
    name varchar(30) not null unique
);
