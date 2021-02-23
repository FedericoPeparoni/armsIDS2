
create table system_configurations (
    id                     serial primary key,
    item_name              varchar(100) unique not null,
    item_class             int not null references system_item_types(id),
    data_type              int references system_data_types(id),
    units                  varchar(100),
    range                  varchar(100),
    default_value          varchar(100),
    current_value          varchar(100),
    created_at timestamp   not null default now(),
    created_by varchar(50) not null,
    updated_at timestamp,
    updated_by varchar(50)
);
