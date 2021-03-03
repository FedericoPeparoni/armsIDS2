-- vim:ts=2:sts=2:sw=2:et
\set navdb_version             1.0.1

\set navdb_min_pg_version      1.9
\set navdb_min_postgis_version 1.5

\set navdb_os_user             navdb
\set navdb_os_user_homedir     /var/lib/ :navdb_os_user

\set navdb_name                navdb
\set navdb_owner               navdb
\set navdb_ro_role             :navdb_owner _ro_role
\set navdb_rw_role             :navdb_owner _rw_role
\set navdb_ro_user             :navdb_owner _ro
\set navdb_rw_user             :navdb_owner _rw
\set navdb_live_schema         :navdb_name
\set navdb_staging_schema      :navdb_name _staging
\set navdb_common_schema       :navdb_name _common
\set navdb_encoding            UTF8
\set navdb_locale              en_US.UTF-8

