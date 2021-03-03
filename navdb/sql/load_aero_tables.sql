-- vim:ts=2:sts=2:sw=2:et

\i config.sql

\echo loading initial data to schema navdb
set search_path to navdb,navdb_common,public;
\i -

