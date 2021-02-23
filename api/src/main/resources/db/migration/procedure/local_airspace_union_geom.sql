--liquibase formatted sql
--changeset d.panech:local_airspace_union_geom splitStatements:false stripComments:false runOnChange:true

create or replace function local_airspace_union_geom()
returns geometry language sql stable as
$$
-- Returns all local airspaces as a single geometry; used to find intersections
-- with routes. It must return a single polygon (not a collection) -- code elsewhere
-- relies on that.
select (ST_Dump (ST_Union (ST_SnapToGrid (airspace_boundary, 1e-6)))).geom as geom
  from airspaces
 where airspace_boundary is not null and airspace_type like 'FIR%'
$$;

create or replace function route_inside_local_airspace(route_geom geometry)
returns geometry language sql stable as
$$
-- Returns the geometry of the given route that lies inside of the local airspace.
-- Used by geoserver.
select ids__st_intersection (
           ST_Segmentize (route_geom::geography, 2000)::geometry,
           ST_Segmentize (local_airspace_union_geom()::geography, 2000)::geometry
       )
$$;

create or replace function route_outside_local_airspace(route_geom geometry)
returns geometry language sql stable as
$$
-- Returns the geometry of the given route that lies outside of the local airspace.
-- Used by geoserver.
select ids__st_difference (
           ST_Segmentize (route_geom::geography, 2000)::geometry,
           ST_Segmentize (local_airspace_union_geom()::geography, 2000)::geometry
       )
$$;
