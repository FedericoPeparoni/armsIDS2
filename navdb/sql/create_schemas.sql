-- vim:ts=2:sts=2:sw=2:et
\i config.sql

\echo creating schema navdb_common
drop schema if exists navdb_common cascade;
create schema navdb_common;
set search_path to navdb_common,public,pg_catalog;
\i create_common_tables.sql

\echo creating schema navdb
drop schema if exists navdb cascade;
create schema navdb;
set search_path to navdb,navdb_common,public,pg_catalog;
\i create_aero_tables.sql
\i create_aero_views.sql

\echo creating schema navdb_staging
drop schema if exists navdb_staging cascade;
create schema navdb_staging;
set search_path to navdb_staging,navdb_common,public,pg_catalog;
\i create_aero_tables.sql
\i create_aero_views.sql

\echo creating common views
\i create_aero_common_views.sql

\echo creating schema atfm
drop schema if exists atfm cascade;
create schema atfm;
set search_path to atfm,public,pg_catalog;
\i create_atfm_tables.sql
\i create_atfm_views.sql

