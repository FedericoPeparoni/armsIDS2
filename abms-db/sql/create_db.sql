------------------------------------------------------------
-- create schema
------------------------------------------------------------
create schema if not exists authorization abms;

------------------------------------------------------------
-- create extensions
------------------------------------------------------------
create extension if not exists postgis schema public;

------------------------------------------------------------
-- create tables, etc
------------------------------------------------------------

-- Currently tables are created/altered by Liquibase in the abms-api project
-- FIXME: move all table creation scripts to here

