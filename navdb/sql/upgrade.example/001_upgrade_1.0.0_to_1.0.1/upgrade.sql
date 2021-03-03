-- vim:ts=2:sts=2:sw=2:et

-- include the local copy of config.sql
\i config.sql

-- create/alter tables in a transaction
begin;

-- create a new table in the "navdb_common" schema
create table navdb_common.some_table (id, value);

-- upgrade aero schemas (navdb and navdb_staging) by running the same
-- script twice with different search paths
select navdb__set_workspace ('live');
\i aero.sql

select navdb__set_workspace ('staging');
\i aero.sql

-- set data model version
update navdb_common.navdb_admin set dm_version = :'navdb_version';

commit;

