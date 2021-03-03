-- vim:ts=2:sts=2:sw=2:et

\i config.sql
\echo updating permissions

-- :navdb_owner
grant select on public.geometry_columns to :navdb_owner;
grant select on public.spatial_ref_sys to :navdb_owner;
grant select on public.geography_columns to :navdb_owner;

-- :navdb_ro_role
grant select on all tables in schema navdb_common, navdb, navdb_staging to :navdb_ro_role;
grant usage, select on all sequences in schema navdb_common, navdb, navdb_staging to :navdb_ro_role;
grant execute on all functions in schema navdb_common, navdb, navdb_staging to :navdb_ro_role;
grant connect, temp on database :navdb_name to :navdb_ro_role;
-- grant usage on language plpgsql, plperl, plpythonu to :navdb_ro_role;
grant usage on schema navdb_common, navdb, navdb_staging to :navdb_ro_role;

grant select on public.geometry_columns to :navdb_ro_role;
grant select on public.spatial_ref_sys to :navdb_ro_role;
grant select on public.geography_columns to :navdb_ro_role;

-- :navdb_rw_role
grant insert, update, delete, truncate on all tables in schema navdb, navdb_staging to :navdb_rw_role;
grant update on all sequences in schema navdb, navdb_staging to :navdb_rw_role;

grant select on public.geometry_columns to :navdb_rw_role;
grant select on public.spatial_ref_sys to :navdb_rw_role;
grant select on public.geography_columns to :navdb_rw_role;

-- set default search path
\echo setting default search path
alter database :"navdb_name" set search_path to navdb_common,navdb,public,pg_catalog;

