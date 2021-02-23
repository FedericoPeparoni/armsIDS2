drop function if exists st_difference( geography, geography );

create function st_difference( _geography_a geography,  _geography_b geography)
    returns geography as
$$
declare
    _geography_invert geography;
begin
    -- get invert of _geography_b
    _geography_invert := ST_INVERT(_geography_b::geometry)::geography;

    -- return intersection of _geography_a with invert of b
	return ST_Intersection(_geography_a, _geography_invert);
end;
$$ language 'plpgsql';
