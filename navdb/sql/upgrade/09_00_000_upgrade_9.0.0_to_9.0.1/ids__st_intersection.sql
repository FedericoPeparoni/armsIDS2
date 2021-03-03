create or replace function ids__get_max_negative_x (geom geometry)
returns float language sql immutable as
$$
-- Returns a maximum negative longitude from points contained with the given geometry.
-- For example, given LINESTRING(-20 50, -10 50, 40 50), it will return -10.
	select max ( st_x((dp).geom) ) as x
	from (
		select ST_DumpPoints (geom) as dp
	) as zzz
	where st_x((dp).geom) < 0;
$$;

create or replace function _ids__wrap_2_geoms (geom_1 inout geometry, geom_2 inout geometry)
language plpgsql immutable as
$$
--
-- This function modifies 2 geometries to avoid crossing the 180 meridian
-- (i.e., make them continuous on flat surface), making them suitable for
-- finding intersections etc. It shifts the negative bits of the geometries
-- by +360 degrees as necessary.
--
-- This function is used internally by ids__st_intersection() & similar.
--
-- It assumes that for each geometry, its points' longitudes don't span more
-- than 180 degrees, and will not work correctly otherwise.
--
declare
	xmin_1 float;
	xmax_1 float;
	xmin_2 float;
	xmax_2 float;
begin
	-- get bounding longitude extents
	xmin_1 := st_xmin (geom_1);
	xmax_1 := st_xmax (geom_1);
	xmin_2 := st_xmin (geom_2);
	xmax_2 := st_xmax (geom_2);
	
	-- if geom_1 crosses dateline
	if (xmax_1 - xmin_1) > 180 then
		-- CASE 1: both geometries cross the dateline:
		--   Shift their negative parts into positive range by +360 degrees; this
		--   will avoid the discontinuous -180 <=> +180 "jump" in the polygon by
		--   making the neighbouring points close to each other in the positive
		--   plane (but outside the 180 range).
		if (xmax_2 - xmin_2) > 180 then
			geom_1 := ST_Shift_Longitude (geom_1);
			geom_2 := ST_Shift_Longitude (geom_2);
		-- CASE 2: geom_1 crosses dateline, while geom_2 crosses geom_1's negative
		-- longitude boundary:
		--   Shift the nagative part of geom_1 into positive range by +360 degrees,
		--   as above; but also shift geom_2 entirely by 360 degrees. We need to do
		--   this because we just shifted the negative end of geom_1 into the
		--   positive plane, so any negative bits of geom_2 that might intersect
		--   with geom_1 have to be shifted as well.
		elsif xmin_2 < ids__get_max_negative_x (geom_1) then
			geom_1 := ST_Shift_Longitude (geom_1);
			geom_2 := ST_Translate (geom_2, 360, 0);
		-- CASE 3: geom_1 crosses dateline, but geom_2 is "far away" from geom_1's
		-- negative edge:
		--   shift geom_1, but leave geom_2 as is
		else
			geom_1 := ST_Shift_Longitude (geom_1);
		end if;

	-- CASE 4: geom_2 crosses dateline, geom_1 crosses geom_2's negative longitude boundary:
	--   same ase CASE 2, but with geometries reversed
	elsif xmin_1 < ids__get_max_negative_x (geom_2) then
		geom_1 := ST_Translate (geom_1, 360, 0);
		geom_2 := ST_Shift_Longitude (geom_2);
	end if;
	-- CASE 5: neither geometry crosses dateline:
	--   leave them unchanged
end;
$$;

create or replace function ids__st_intersection (geom_1 geometry, geom_2 geometry)
returns geometry language plpgsql immutable as
$$
--
-- This function finds an intersection between 2 geometries.
--
-- Each geometry must not (separately) span more than 180 degrees of longitude, this
-- function will not work correctly otherwise.
--
-- The geometries must not contain long line segments -- you may need to modify the input
-- geometries by calling ST_Segmentize (geography) to insert additional points using
-- spheroid/great circle logic before using calling ids__st_intersection() and friends.
--
begin
	-- both arguments are null
	if geom_1 is null or geom_2 is null then
		return null;
	end if;
	-- same geometry passed twice
	if geom_1 = geom_2 then
		return geom_1;
	end if;
	-- shift point longitudes by +360 degrees if necessary
	select * from _ids__wrap_2_geoms (geom_1, geom_2) into geom_1, geom_2;
	-- Call ST_Intersection (geometry, geometry) -- this uses flat/planar logic;
	-- then convert the result to geography to force all longitudes to +/- 180 range
	-- then convert it back to geometry
	return ST_Intersection (geom_1, geom_2)::geography::geometry;
end;
$$;

create or replace function ids__st_intersects (geom_1 geometry, geom_2 geometry)
returns boolean language plpgsql immutable as
$$
--
-- This function finds an intersection between 2 geometries.
--
-- Each geometry must not (separately) span more than 180 degrees of longitude, this
-- function will not work correctly otherwise.
--
-- The geometries must not contain long line segments -- you may need to modify the input
-- geometries by calling ST_Segmentize (geography) to insert additional points using
-- spheroid/great circle logic before using calling ids__st_intersection() and friends.
--
begin
	-- both arguments are null
	if geom_1 is null or geom_2 is null then
		return null;
	end if;
	-- same geometry passed twice
	if geom_1 = geom_2 then
		return geom_1;
	end if;
	-- shift geometries by +360 degrees if necessary
	select * from _ids__wrap_2_geoms (geom_1, geom_2) into geom_1, geom_2;
	-- Call ST_Intersects (geometry, geometry) -- this uses flat/planar logic
	return ST_Intersects (geom_1, geom_2);
end;
$$;


create or replace function ids__st_difference (geom_1 geometry, geom_2 geometry)
returns geometry language plpgsql immutable as
$$
--
-- This function finds an intersection between 2 geometries.
--
-- Each geometry must not (separately) span more than 180 degrees of longitude, this
-- function will not work correctly otherwise.
--
-- The geometries must not contain long line segments -- you may need to modify the input
-- geometries by calling ST_Segmentize (geography) to insert additional points using
-- spheroid/great circle logic before using calling ids__st_intersection() and friends.
--
begin
	-- both arguments are null
	if geom_1 is null or geom_2 is null or geom_1 = geom_2 then
		return null;
	end if;
	-- shift geometries by +360 degrees if necessary
	select * from _ids__wrap_2_geoms (geom_1, geom_2) into geom_1, geom_2;
	-- Call ST_Difference (geometry, geometry) -- this uses flat/planar logic;
	-- then convert the result to geography to force all coordinates to +/- 180 range
	-- then convert it back to geometry
	return ST_Difference (geom_1, geom_2)::geography::geometry;
end;
$$;


