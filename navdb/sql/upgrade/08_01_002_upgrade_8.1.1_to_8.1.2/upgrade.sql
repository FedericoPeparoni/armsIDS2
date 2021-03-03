-- vim:ts=2:sts=2:sw=2:et

-- include the local copy of config.sql
\i config.sql

begin;

DROP VIEW navdb_common.aixm_airportheliport_mapserv_v;
DROP VIEW navdb_common.aixm_airspace_mapserv_v;
DROP VIEW navdb_common.aixm_designatedpoint_mapserv_v;
DROP VIEW navdb_common.aixm_dme_mapserv_v;
DROP VIEW navdb_common.aixm_fir_mapserv_v;
DROP VIEW navdb_common.aixm_markerbeacon_mapserv_v;
DROP VIEW navdb_common.aixm_ndb_mapserv_v;
DROP VIEW navdb_common.aixm_route_mapserv_v;
DROP VIEW navdb_common.aixm_tacan_mapserv_v;
DROP VIEW navdb_common.aixm_tma_mapserv_v;
DROP VIEW navdb_common.aixm_vor_mapserv_v;
DROP VIEW navdb_common.v_published_ats;
DROP VIEW navdb_common.v_special_act_area;
DROP VIEW navdb_common.v_airport;
DROP VIEW navdb_common.v_airspace;
DROP VIEW navdb_common.v_airspace_all;
DROP VIEW navdb_common.v_airway;
DROP VIEW navdb_common.v_airway_seg;
DROP VIEW navdb_common.v_fixes;
DROP VIEW navdb_common.v_dme;
DROP VIEW navdb_common.v_fir;
DROP VIEW navdb_common.v_navaids;
DROP VIEW navdb_common.v_ndb;
DROP VIEW navdb_common.v_obsarea;
DROP VIEW navdb_common.v_obstacle;
DROP VIEW navdb_common.v_runway;
DROP VIEW navdb_common.v_rwydirection;
DROP VIEW navdb_common.v_sm_fea;
DROP VIEW navdb_common.v_tacan;
DROP VIEW navdb_common.v_twy;
DROP VIEW navdb_common.v_vor;
DROP VIEW navdb_common.v_waypoint;

set search_path to navdb_staging,navdb_common,public,pg_catalog;
\i create_aero_views.sql

set search_path to navdb,navdb_common,public,pg_catalog;
\i create_aero_views.sql

-- set data model version
update navdb_common.navdb_admin set dm_version = :'navdb_version';

commit;

