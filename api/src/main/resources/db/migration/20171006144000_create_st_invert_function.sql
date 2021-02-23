drop function if exists st_invert( geometry );

create function st_invert( _geometry geometry )
    returns geometry as
$$
declare
    _geometry_entire geometry;
begin

    -- define entire space to invert on
    _geometry_entire := ST_GEOMFROMTEXT('POLYGON((-20037508.3427892 147730758.194568,
                                                20037508.3427892 147730758.194568,
                                                20037508.3427892 -147730762.669922,
                                                -20037508.3427892 -147730762.669922,
                                                -20037508.3427892 147730758.194568))', 4326);

    -- return difference of _geometry from entire space
    return ST_DIFFERENCE(_geometry_entire, _geometry);

end;
$$ language 'plpgsql';
