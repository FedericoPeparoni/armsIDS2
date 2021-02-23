--liquibase formatted sql
--changeset derek:20161004100622_create_oauth_tables dbms:postgresql splitStatements:false

-- These tables are created to adhere to the default settings of the Spring Security OAuth 2 package.
-- http://projects.spring.io/spring-security-oauth/docs/oauth2.html

create table oauth_client_details (
    client_id               varchar(255) primary key,
    resource_ids            varchar(255),
    client_secret           varchar(255),
    scope                   varchar(255),
    authorized_grant_types  varchar(255),
    web_server_redirect_uri varchar(255),
    authorities             varchar(255),
    access_token_validity   int,
    refresh_token_validity  int,
    additional_information  varchar(4096),
    autoapprove             varchar(4096)
);

create table oauth_client_token (
    token_id          varchar(255),
    token             bytea,
    authentication_id varchar(255),
    user_name         varchar(50) references users (login),
    client_id         varchar(255)
);

create table oauth_access_token (
    authentication_id varchar(255) primary key,
    token_id          varchar(255),
    token             bytea,
    user_name         varchar(50) references users (login),
    client_id         varchar(255),
    authentication    bytea,
    refresh_token     varchar(255)
);

create table oauth_refresh_token (
    token_id       varchar(255),
    token          bytea,
    authentication bytea
);

create table oauth_code (
    code           varchar(255),
    authentication bytea
);

create table oauth_approvals (
    userId         varchar(255),
    clientId       varchar(255),
    scope          varchar(255),
    status         varchar(255),
    expiresAt      timestamp,
    lastModifiedAt timestamp
);
