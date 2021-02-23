--liquibase formatted sql
--changeset d.panech:local_airspace_union_geom splitStatements:false stripComments:false runOnChange:true

create or replace function included_fir_union_geom_by_flight_level(flight_level float)
returns geometry language sql stable as
$$
-- Returns all local airspaces as a single geometry; used to find intersections
-- with routes. It must return a single polygon (not a collection) -- code elsewhere
-- relies on that.
-- airspaces must be included and airspace ceiling must be higher then the flight level
select (ST_Dump (ST_Union (ST_SnapToGrid (airspace_boundary, 1e-6)))).geom as geom
  from airspaces
 where airspace_boundary is not null and airspace_type like 'FIR%' and airspace_included = TRUE and airspace_ceiling::float >= flight_level::float
 
$$;

create or replace function excluded_tma_union_geom_by_flight_level(flight_level float)
returns geometry language sql stable as
$$
-- Returns all local airspaces as a single geometry; used to find intersections
-- with routes. It must return a single polygon (not a collection) -- code elsewhere
-- relies on that.
-- airspaces must be excluded and airspace ceiling must be higher then the flight level
select (ST_Dump (ST_Union (ST_SnapToGrid (airspace_boundary, 1e-6)))).geom as geom
  from airspaces
 where airspace_boundary is not null and airspace_type like 'TMA%' and airspace_included = false and airspace_ceiling >= flight_level
 
$$;