-- vim:ts=2:sts=2:sw=2:et

-- include the local copy of config.sql
\i upgrade_config.sql

begin;

ALTER table navdb_staging.airport alter column standmarking TYPE character varying;
ALTER table navdb_staging.airport alter column standmarking_lcl TYPE character varying;
ALTER table navdb_staging.airport alter column twyguideline TYPE character varying;
ALTER table navdb_staging.airport alter column twyguideline_lcl TYPE character varying;
ALTER table navdb_staging.airport alter column visualparking TYPE character varying;
ALTER table navdb_staging.airport alter column visualparking_lcl TYPE character varying;
ALTER table navdb_staging.airport alter column stop_bar_desc TYPE character varying;
ALTER table navdb_staging.airport alter column stop_bar_dec_lcl TYPE character varying;

ALTER table navdb.airport alter column standmarking TYPE character varying;
ALTER table navdb.airport alter column standmarking_lcl TYPE character varying;
ALTER table navdb.airport alter column twyguideline TYPE character varying;
ALTER table navdb.airport alter column twyguideline_lcl TYPE character varying;
ALTER table navdb.airport alter column visualparking TYPE character varying;
ALTER table navdb.airport alter column visualparking_lcl TYPE character varying;
ALTER table navdb.airport alter column stop_bar_desc TYPE character varying;
ALTER table navdb.airport alter column stop_bar_dec_lcl TYPE character varying;

-- set data model version
update navdb_common.navdb_admin set dm_version = :'navdb_version';

commit;

