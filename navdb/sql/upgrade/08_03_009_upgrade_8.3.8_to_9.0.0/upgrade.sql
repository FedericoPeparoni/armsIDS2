-- vim:ts=2:sts=2:sw=2:et

-- include the local copy of config.sql
\i upgrade_config.sql 

begin;

-- drop all aero views
drop view if exists navdb_staging.V_AIRWAY_SEG cascade;
drop view if exists navdb_staging.V_WAYPOINT cascade;
drop view if exists navdb_staging.V_VOR cascade;
drop view if exists navdb_staging.V_TWY cascade;
drop view if exists navdb_staging.V_TACAN cascade;
drop view if exists navdb_staging.V_SPECIAL_ACT_AREA cascade;
drop view if exists navdb_staging.V_SM_FEA cascade;
drop view if exists navdb_staging.V_RWYDIRECTION cascade;
drop view if exists navdb_staging.V_RUNWAY cascade;
drop view if exists navdb_staging.V_PUBLISHED_ATS cascade;
drop view if exists navdb_staging.V_OBSTACLE cascade;
drop view if exists navdb_staging.V_OBSAREA cascade;
drop view if exists navdb_staging.V_NDB cascade;
drop view if exists navdb_staging.V_NAVAIDS cascade;
drop view if exists navdb_staging.V_FIXES cascade;
drop view if exists navdb_staging.V_FIR cascade;
drop view if exists navdb_staging.V_DME cascade;
drop view if exists navdb_staging.V_AIRWAY cascade;
drop view if exists navdb_staging.V_AIRSPACE_ALL cascade;
drop view if exists navdb_staging.V_AIRSPACE cascade;
drop view if exists navdb_staging.V_AIRPORT cascade;
drop view if exists navdb_staging.V_RUNWAYS_BY_AIRPORT cascade;
drop view if exists navdb_staging.V_SERVICES_BY_AIRPORT cascade;
drop view if exists navdb_staging.aixm_airportheliport_mapserv_v cascade;
drop view if exists navdb_staging.aixm_airspace_mapserv_v cascade;
drop view if exists navdb_staging.aixm_designatedpoint_mapserv_v cascade;
drop view if exists navdb_staging.aixm_dme_mapserv_v cascade;
drop view if exists navdb_staging.aixm_fir_mapserv_v cascade;
drop view if exists navdb_staging.aixm_markerbeacon_mapserv_v cascade;
drop view if exists navdb_staging.aixm_ndb_mapserv_v cascade;
drop view if exists navdb_staging.aixm_route_mapserv_v cascade;
drop view if exists navdb_staging.aixm_tacan_mapserv_v cascade;
drop view if exists navdb_staging.aixm_tma_mapserv_v cascade;
drop view if exists navdb_staging.aixm_vor_mapserv_v cascade;

drop view if exists navdb.V_AIRWAY_SEG cascade;
drop view if exists navdb.V_WAYPOINT cascade;
drop view if exists navdb.V_VOR cascade;
drop view if exists navdb.V_TWY cascade;
drop view if exists navdb.V_TACAN cascade;
drop view if exists navdb.V_SPECIAL_ACT_AREA cascade;
drop view if exists navdb.V_SM_FEA cascade;
drop view if exists navdb.V_RWYDIRECTION cascade;
drop view if exists navdb.V_RUNWAY cascade;
drop view if exists navdb.V_PUBLISHED_ATS cascade;
drop view if exists navdb.V_OBSTACLE cascade;
drop view if exists navdb.V_OBSAREA cascade;
drop view if exists navdb.V_NDB cascade;
drop view if exists navdb.V_NAVAIDS cascade;
drop view if exists navdb.V_FIXES cascade;
drop view if exists navdb.V_FIR cascade;
drop view if exists navdb.V_DME cascade;
drop view if exists navdb.V_AIRWAY cascade;
drop view if exists navdb.V_AIRSPACE_ALL cascade;
drop view if exists navdb.V_AIRSPACE cascade;
drop view if exists navdb.V_AIRPORT cascade;
drop view if exists navdb.V_RUNWAYS_BY_AIRPORT cascade;
drop view if exists navdb.V_SERVICES_BY_AIRPORT cascade;
drop view if exists navdb.aixm_airportheliport_mapserv_v cascade;
drop view if exists navdb.aixm_airspace_mapserv_v cascade;
drop view if exists navdb.aixm_designatedpoint_mapserv_v cascade;
drop view if exists navdb.aixm_dme_mapserv_v cascade;
drop view if exists navdb.aixm_fir_mapserv_v cascade;
drop view if exists navdb.aixm_markerbeacon_mapserv_v cascade;
drop view if exists navdb.aixm_ndb_mapserv_v cascade;
drop view if exists navdb.aixm_route_mapserv_v cascade;
drop view if exists navdb.aixm_tacan_mapserv_v cascade;
drop view if exists navdb.aixm_tma_mapserv_v cascade;
drop view if exists navdb.aixm_vor_mapserv_v cascade;

-- re-create them in navdb
set search_path to navdb,navdb_common,public,pg_catalog;
\i create_aero_views.sql

-- re-create them in navdb_staging
set search_path to navdb_staging,navdb_common,public,pg_catalog;
\i create_aero_views.sql

-- set data model version
update navdb_common.navdb_admin set dm_version = :'navdb_version'; 

commit;

