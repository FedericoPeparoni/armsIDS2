-- vim:ts=2:sts=2:sw=2:et

-- include the local copy of config.sql
\i upgrade_config.sql 

begin;

----------------------------------------------------------
-- atfm schema
----------------------------------------------------------
\echo creating schema atfm
drop schema if exists atfm cascade;
create schema atfm;
set search_path to atfm,public,pg_catalog;

----------------------------------------------------------
-- fpl_object
----------------------------------------------------------

create table atfm.fpl_object (
    catalogue_fpl_object_id 					numeric(10,0) not null,
    catalogue_date 								timestamp(0) without time zone default clock_timestamp() not null,
    catalogue_status 							character varying(1) not null,
    catalogue_tx_status 						character varying(1) not null,
    catalogue_prc_status 						character varying(1) not null,
    catalogue_expiry_date 						timestamp(0) without time zone default null::timestamp without time zone,
    com_priority 								character varying(2) default 'gg'::character varying not null,
    com_originator 								character varying(8) not null,
    com_dst_addr_list 							character varying(2000),
    com_optional_field 							character varying(54),
    flight_id 									character varying(7) not null,
    ssr_mode 									character varying(1),
    ssr_code 									character varying(4),
    flight_rules 								character varying(1),
    flight_type 								character varying(2),
    aircraft_number 							character varying(2),
    aircraft_type 								character varying(4),
    wake_turb 									character varying(1),
    equipment 									character varying(100),
    departure_ad 								character varying(4) not null,
    departure_time 								character varying(4) not null,
    msg_departure_ad 							character varying(4) default null::character varying,
    msg_departure_time 							character varying(4) default null::character varying,
    speed 										character varying(5),
    flight_level 								character varying(5),
    route 										character varying(1800),
    destination_ad 								character varying(4) not null,
    total_eet 									character varying(4),
    alternate_ad 								character varying(4),
    alternate_ad_2 								character varying(4),
    arrival_ad 									character varying(4) default null::character varying,
    arrival_time 								character varying(1800) default null::character varying,
    arr_aerodrome_17 							character varying(30) default null::character varying,
    other_info 									character varying(1800),
    departure_date 								timestamp(0) without time zone default null::timestamp without time zone,
    arrival_date 								timestamp(0) without time zone default null::timestamp without time zone,
    cancelation_date 							timestamp(0) without time zone default null::timestamp without time zone,
    last_change_date 							timestamp(0) without time zone default null::timestamp without time zone,
    delay_date 									timestamp(0) without time zone default null::timestamp without time zone,
    day_of_flight 								timestamp(0) without time zone not null,
    user_id 									numeric(6,0),
    gufi 										character varying(30) default null::character varying,
    processed 									character varying(1) default 'n'::character varying not null,
	route_geom 									public.geometry,
	fpl_source 									character varying(10) not null
);

-- sequences
create sequence atfm.fpl_object_id_seq
    start with 1
    increment by 1
    no minvalue
    no maxvalue
    cache 1;

-- primary key
alter table only atfm.fpl_object
    add constraint fpl_object_pk primary key (catalogue_fpl_object_id);
	
alter table only atfm.fpl_object alter column catalogue_fpl_object_id set default nextval('fpl_object_id_seq'::regclass);

-- indexes
create index fpl_object_i1 on atfm.fpl_object using btree (catalogue_status, catalogue_tx_status);
create index fpl_object_i2 on atfm.fpl_object using btree (processed);
create index fpl_object_i3 on atfm.fpl_object using btree (fpl_source);

-- check constraints
alter table atfm.fpl_object add
    constraint fpl_object_c1
        check (catalogue_status in ('a', 'e', 'i'))
;
alter table atfm.fpl_object add
    constraint fpl_object_c2
        check (catalogue_tx_status in ('p', 't', 'n', 'x', 's','u','w'))
;
alter table atfm.fpl_object add
    constraint fpl_object_c3
        check (catalogue_prc_status in ('p', 'c', 'a', 'd', 'l','t'))
;
alter table atfm.fpl_object add
    constraint fpl_object_c4
        check (processed in ('y', 'n', 'x'))
;

-- set data model version
update navdb_common.navdb_admin set dm_version = :'navdb_version'; 

commit;

