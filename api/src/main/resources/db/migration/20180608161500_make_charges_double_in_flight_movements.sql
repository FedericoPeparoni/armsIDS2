/*
 * Create a temporary table and save definition of "charge_view" in it.
 * We will re-create the view using this saved query after altering
 * flight_movements table.
 * 
 * This is to avoid hard-coding view definition in this script, which may cause
 * problems if another upgrade script that also drops/re-creates this
 * view is merged into Git at the same time.
 */
create temp table dpanech_upgrade_tmp_20180608 (view_def text);
insert into dpanech_upgrade_tmp_20180608 values (pg_get_viewdef ('charge_view'::regclass, true));

-- Drop the view because it depends on table flight_movements being altered below
drop view charge_view;

-- Change all money-related columnds to "double precision" from "numeric(10,2). This
-- is because we already use double everywhere else, and numeric(10,2) is too small
-- to hold very large values, such as the ones in Venezuela.
alter table flight_movements
    alter column aerodrome_charges type double precision,
    alter column approach_charges  type double precision,
    alter column atc_crossing_distance_cost type double precision,
    alter column billable_crossing_cost type double precision,
    alter column domestic_passenger_charges type double precision,
    alter column enroute_charges type double precision,
    alter column exempt_aerodrome_charges type double precision,
    alter column exempt_approch_charges type double precision,
    alter column exempt_dep_charges type double precision,
    alter column exempt_domestic_passenger_charges type double precision,
    alter column exempt_enroute_charges type double precision,
    alter column exempt_international_passenger_charges type double precision,
    alter column exempt_late_arrival_charges type double precision,
    alter column exempt_late_charges type double precision,
    alter column exempt_late_departure_charges type double precision,
    alter column exempt_parking_charges type double precision,
    alter column fpl_crossing_cost type double precision,
    alter column international_passenger_charges type double precision,
    alter column late_arrival_charges type double precision,
    alter column late_charges type double precision,
    alter column late_departure_charges type double precision,
    alter column nominal_crossing_cost type double precision,
    alter column parking_charges type double precision,
    alter column prepaid_amount type double precision,
    alter column radar_crossing_cost type double precision,
    alter column tasp_charge type double precision,
    alter column total_charges type double precision,
    alter column tower_crossing_distance_cost type double precision,
    alter column user_crossing_distance_cost type double precision
;

-- Re-create charge_view using the definition that we had saved
do $$
declare
    v_create_view varchar;
    v_query varchar;
begin
    select view_def into v_query from dpanech_upgrade_tmp_20180608;
    execute format ('create view charge_view as %s', v_query);
end;
$$
;

-- Drop the temp table
drop table dpanech_upgrade_tmp_20180608;

