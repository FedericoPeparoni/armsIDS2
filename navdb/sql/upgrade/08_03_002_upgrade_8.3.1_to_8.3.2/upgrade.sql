-- vim:ts=2:sts=2:sw=2:et

-- include the local copy of config.sql
\i upgrade_config.sql 

begin;

alter table atfm.fpl_object 
	alter column catalogue_status drop not null,
	alter column catalogue_tx_status drop not null,
	alter column catalogue_prc_status drop not null,	
	alter column com_originator drop not null,
	alter column com_priority drop not null,
	alter column com_priority drop default,
	alter column processed drop not null,
	alter column processed drop default,	
	alter column flight_id type character varying(10),
	drop constraint fpl_object_c1,
	drop constraint fpl_object_c2,
	drop constraint fpl_object_c3,
	drop constraint fpl_object_c4;

-- set data model version
update navdb_common.navdb_admin set dm_version = :'navdb_version'; 

commit;

