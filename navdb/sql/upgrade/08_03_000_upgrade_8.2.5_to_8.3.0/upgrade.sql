-- vim:ts=2:sts=2:sw=2:et

-- include the local copy of config.sql
\i upgrade_config.sql

begin;

-- set data model version
update navdb_common.navdb_admin set dm_version = :'navdb_version';

commit;

