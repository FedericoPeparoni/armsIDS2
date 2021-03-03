CREATE OR REPLACE FUNCTION navdb_common.lon_wrap(geometry)
  RETURNS geometry AS
$BODY$ 
    
 DECLARE 
  end_geom GEOMETRY; 
  line_geom GEOMETRY;
  shifted GEOMETRY;
  wkt Text;
  xp FLOAT; 
  yp FLOAT; 
  nn INTEGER; 
  
 BEGIN 
  IF ((st_xmax($1)-st_xmin($1)) > 180) THEN 
	  SELECT INTO shifted $1;
	  IF (ST_NumGeometries(st_boundary(shifted)) = 1) THEN
		  SELECT INTO line_geom st_geometryn(st_boundary(shifted),1);

		  --RAISE NOTICE 'SIMPLE GEOM %', ST_AsText(line_geom);  -- Prints 30

		  FOR nn IN 1 .. st_npoints(line_geom) LOOP 
		   SELECT INTO xp  st_x(st_pointn(line_geom,nn)); 
		   SELECT INTO yp  st_y(st_pointn(line_geom,nn)); 
		   IF (xp < 0) THEN
		    --RAISE NOTICE 'Before %', xp;
		    xp := xp + 360;
		    --RAISE NOTICE 'After %', xp;
		   END IF; 
		   --RAISE NOTICE 'Step 1 %', ST_AsText(st_MakePoint(xp,yp));
		   --RAISE NOTICE 'Step 2 %', ST_AsText(st_setpoint(line_geom,nn-1,st_MakePoint(xp,yp))); 
		   --RAISE NOTICE 'Step 3 %', ST_AsText(ST_BUFFER(st_setpoint(line_geom,nn-1,st_MakePoint(xp,yp)), 0)); 
		   --RAISE NOTICE 'Step 4 %', ST_AsText(ST_SetSRID(st_setpoint(line_geom,nn-1,st_MakePoint(xp,yp)), 4326)); 
		   SELECT INTO line_geom (ST_SetSRID((st_setpoint(line_geom,nn-1,st_MakePoint(xp,yp))), 4326));
		   --RAISE NOTICE 'Line GEOM2 %', ST_AsText(line_geom); 
		  END LOOP;

		  --RAISE NOTICE 'Line GEOM END %', ST_AsText(line_geom); 

		  SELECT INTO end_geom ST_Buffer(ST_SetSRID((st_multi(st_MakePolygon(line_geom))), 4326), 0); 

		  SELECT INTO end_geom st_union( 
			    ST_SetSRID(st_multi(st_intersection( ST_SetSRID(st_multi(box2d('BOX(  0 -90, 360 
		90)')), 4326),end_geom )), 4326), 
		  ST_SetSRID(st_translate(st_multi(st_intersection( ST_SetSRID(st_multi(box2d('BOX(360 -90, 720 
		90)')), 4326),end_geom )),-360,0,0), 4326) 
		  );
	ELSE
		--RAISE NOTICE 'COMPLEX GEOM %', ST_NumGeometries(st_boundary(shifted));  -- Prints 30
		end_geom := ST_Shift_Longitude(shifted);
	END IF;
	SELECT INTO end_geom (ST_Union( ST_SetSRID(st_translate((st_intersection( ST_SetSRID(st_multi(box2d('BOX(180.00000001 -90, 360 90)')), 4326),st_buffer(end_geom, 0))),-360,0,0), 4326) , ST_SetSRID((st_intersection( ST_SetSRID(st_multi(box2d('BOX(-180 -90, 179.99999999 90)')), 4326),st_buffer(end_geom, 0) )), 4326)));
  ELSE 
   end_geom := $1; 
  END IF; 

  RETURN end_geom;  
  
 END;
$BODY$


CREATE OR REPLACE FUNCTION navdb_common.lon_wrap_line(geometry)
  RETURNS geometry AS
$BODY$ 
  
 DECLARE 
  end_geom GEOMETRY; 
  line_geom GEOMETRY;
  shifted GEOMETRY;
  wkt Text;
  xp FLOAT; 
  yp FLOAT; 
  nn INTEGER; 
  
 BEGIN 
  IF ((st_xmax($1)-st_xmin($1)) > 180) THEN 
	  SELECT INTO shifted $1;
	  SELECT INTO line_geom st_geometryn(shifted,1);

		  --RAISE NOTICE 'SIMPLE GEOM %', ST_AsText(line_geom);  -- Prints 30

		  FOR nn IN 1 .. st_npoints(line_geom) LOOP 
		   SELECT INTO xp  st_x(st_pointn(line_geom,nn)); 
		   SELECT INTO yp  st_y(st_pointn(line_geom,nn)); 
		   IF (xp < 0) THEN
		    --RAISE NOTICE 'Before %', xp;
		    xp := xp + 360;
		    --RAISE NOTICE 'After %', xp;
		   END IF; 
		   --RAISE NOTICE 'Step 1 %', ST_AsText(st_MakePoint(xp,yp));
		   --RAISE NOTICE 'Step 2 %', ST_AsText(st_setpoint(line_geom,nn-1,st_MakePoint(xp,yp))); 
		   --RAISE NOTICE 'Step 3 %', ST_AsText(ST_BUFFER(st_setpoint(line_geom,nn-1,st_MakePoint(xp,yp)), 0)); 
		   --RAISE NOTICE 'Step 4 %', ST_AsText(ST_SetSRID(st_setpoint(line_geom,nn-1,st_MakePoint(xp,yp)), 4326)); 
		   SELECT INTO line_geom (ST_SetSRID((st_setpoint(line_geom,nn-1,st_MakePoint(xp,yp))), 4326));
		   --RAISE NOTICE 'Line GEOM2 %', ST_AsText(line_geom); 
		  END LOOP;

		  --RAISE NOTICE 'Line GEOM END %', ST_AsText(line_geom); 

		  SELECT INTO end_geom ST_SetSRID(st_multi(line_geom), 4326); 

		  SELECT INTO end_geom st_multi(ST_Union( ST_SetSRID(st_translate((st_intersection( ST_SetSRID(st_multi(box2d('BOX(180.00000001 -90, 360 90)')), 4326),end_geom)),-360,0,0), 4326) , ST_SetSRID((st_intersection( ST_SetSRID(st_multi(box2d('BOX(-180 -90, 179.99999999 90)')), 4326),end_geom )), 4326)));
  ELSE 
   end_geom := $1; 
  END IF; 

  RETURN end_geom;  
  
 END; 
$BODY$

-- Update statement run on top of the 1.0.6 dataset
UPDATE airspace a1 set GEOM = (select (ST_Union( ST_SetSRID(st_translate((st_intersection( ST_SetSRID(st_multi(box2d('BOX(180.00000001 -90, 360 90)')), 4326),st_buffer(GEOM, 0))),-360,0,0), 4326) , ST_SetSRID((st_intersection( ST_SetSRID(st_multi(box2d('BOX(-180 -90, 179.99999999 90)')), 4326),st_buffer(GEOM, 0) )), 4326))))
where airspace_pk in (SELECT distinct airspace_pk FROM (select airspace_pk, st_buffer(GEOM, 0) as GEOM, (st_intersection(st_buffer(GEOM, 0), ST_SetSRID(st_multi(box2d('BOX(180.00000001 -90, 360 90)')), 4326))) as GEOM1 from airspace) a WHERE ST_isEmpty(GEOM1) = false)


