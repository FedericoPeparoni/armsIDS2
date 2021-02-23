create table user_event_types
(
    id   serial primary key,
    name varchar(30) not null unique
);

insert into user_event_types (name) values ('login');
insert into user_event_types (name) values ('logout');
insert into user_event_types (name) values ('add');
insert into user_event_types (name) values ('update');
insert into user_event_types (name) values ('delete');
insert into user_event_types (name) values ('invoice creation');
insert into user_event_types (name) values ('debit note creation');
insert into user_event_types (name) values ('credit note creation');

create table user_event_logs
  (
     id   serial primary key,
     user_name varchar(45) not null,
     date_time timestamp with time zone not null default now(),
     ip_address varchar(45) not null,
     event_type varchar(30) not null references user_event_types (name),
     record_primary_key varchar(45),
     unique_record_id varchar(45),
     modified_column_names_values varchar(100),
     created_at timestamp     not null default now(),
     created_by varchar(50)   not null,
     updated_at timestamp,
     updated_by varchar(50)
  );
