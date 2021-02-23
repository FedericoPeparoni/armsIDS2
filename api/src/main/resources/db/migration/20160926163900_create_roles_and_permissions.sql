--liquibase formatted sql
--changeset derek:20160926163900_create_roles_and_permissions dbms:postgresql splitStatements:false

create table roles (
    id   serial primary key,
    name varchar(25) not null unique
);

create table permissions (
    id   serial primary key,
    name varchar(100) not null unique
);

create table role_permission (
    id            serial primary key,
    role_id       int       not null references roles (id),
    permission_id int       not null references permissions (id),
    unique (role_id, permission_id)
);

create table user_role (
    id         serial primary key,
    user_id    int       not null references users (id),
    role_id    int       not null references roles (id),
    unique (user_id, role_id)
);
