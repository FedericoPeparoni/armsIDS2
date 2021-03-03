-- vim:ts=4:sts=4:sw=4:et

set search_path to navdb_common,navdb,public,pg_catalog;

-- Function: navdb_common.navdb__lon_wrap_line(geometry)

-- DROP FUNCTION navdb_common.navdb__lon_wrap_line(geometry);

CREATE OR REPLACE FUNCTION navdb_common.navdb__lon_wrap_line(geometry)
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

     
  ELSE 
   end_geom := $1; 
  END IF; 

  RETURN end_geom;  
  
 END; 
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;

-- Function: navdb_common.navdb__lon_wrap(geometry)

-- DROP FUNCTION navdb_common.navdb__lon_wrap(geometry);

CREATE OR REPLACE FUNCTION navdb_common.navdb__lon_wrap(geometry)
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
     
  ELSE 
   end_geom := $1; 
  END IF; 

  RETURN end_geom;  
  
 END; 
$BODY$
  LANGUAGE plpgsql VOLATILE;

-- Function: navdb_common.navdb__get_next_airspace_vertex_point(character varying, double precision, double precision)

-- DROP FUNCTION navdb_common.navdb__get_next_airspace_vertex_point(character varying, double precision, double precision);

CREATE OR REPLACE FUNCTION navdb_common.navdb__get_next_airspace_vertex_point(
    boundayvia character varying,
    noseqvalue double precision,
    airspacebdrpk double precision)
  RETURNS geometry AS
$BODY$
	DECLARE
		finalGeom geometry;
		segLat double precision;
		segLon double precision;
	BEGIN
		IF boundayVia <> 'END' THEN
			SELECT thisVertex.segmentLat, thisVertex.segmentLon INTO segLat, segLon FROM AIRSPACEVTX thisVertex where "overlaps"(navdb_common.navdb__get_effective_date(), navdb_common.navdb__get_effective_date(), thisVertex.validfrom::timestamp with time zone, thisVertex.validto::timestamp with time zone) AND thisVertex.AIRSPACEVTX_pk = (SELECT min(next.airspacevtx_pk) from airspacevtx next where next.airspacebdr_pk = airspacebdrPk AND "overlaps"(navdb_common.navdb__get_effective_date(), navdb_common.navdb__get_effective_date(), next.validfrom::timestamp with time zone, next.validto::timestamp with time zone) AND next.noseq > noseqValue);
		--ELSE  
			--SELECT thisVertex.segmentLat, thisVertex.segmentLon INTO segLat, segLon FROM AIRSPACEVTX thisVertex where "overlaps"(navdb_common.navdb__get_effective_date(), navdb_common.navdb__get_effective_date(), thisVertex.validfrom::timestamp with time zone, thisVertex.validto::timestamp with time zone) AND thisVertex.AIRSPACEVTX_pk = (SELECT min(next.airspacevtx_pk) from airspacevtx next where next.airspacebdr_pk = airspacebdrPk AND "overlaps"(navdb_common.navdb__get_effective_date(), navdb_common.navdb__get_effective_date(), next.validfrom::timestamp with time zone, next.validto::timestamp with time zone));
		ELSE
			return NULL;
		END IF;
		IF (segLat IS NULL AND segLon IS NULL) THEN
			SELECT thisVertex.segmentLat, thisVertex.segmentLon INTO segLat, segLon FROM AIRSPACEVTX thisVertex where "overlaps"(navdb_common.navdb__get_effective_date(), navdb_common.navdb__get_effective_date(), thisVertex.validfrom::timestamp with time zone, thisVertex.validto::timestamp with time zone) AND thisVertex.AIRSPACEVTX_pk = (SELECT min(next.airspacevtx_pk) from airspacevtx next where next.airspacebdr_pk = airspacebdrPk AND "overlaps"(navdb_common.navdb__get_effective_date(), navdb_common.navdb__get_effective_date(), next.validfrom::timestamp with time zone, next.validto::timestamp with time zone));
		END IF;
		return ST_SetSRID(ST_MakePoint(segLon, segLat), 4326);
	END;
$BODY$
  LANGUAGE plpgsql VOLATILE;


-- Function: navdb_common.navdb__to_meters(double precision, text)

-- DROP FUNCTION navdb_common.navdb__to_meters(double precision, text);

CREATE OR REPLACE FUNCTION navdb_common.navdb__to_meters(
    inputvalue double precision,
    valueuom text)
  RETURNS double precision AS
$BODY$
	DECLARE
		multiplier float;
	BEGIN
	
		IF valueuom = 'NM' THEN
			multiplier := 1852;
		ELSIF  valueuom = 'M' THEN
			multiplier := 1;
		ELSIF  valueuom = 'KM' THEN
			multiplier := 1000;
		ELSIF  valueuom = 'FT' THEN
			multiplier := 0.3048;
		END IF;
		return inputValue * multiplier;
	END;
$BODY$
  LANGUAGE plpgsql VOLATILE;


-- Function: navdb_common.navdb__get_geoborder_line(double precision, geometry, geometry)

-- DROP FUNCTION navdb_common.navdb__get_geoborder_line(double precision, geometry, geometry);

CREATE OR REPLACE FUNCTION navdb_common.navdb__get_geoborder_line(
    geoborderpk double precision,
    pointa geometry,
    pointb geometry)
  RETURNS geometry AS
$BODY$
	DECLARE
		geoborderLine geometry;
		outputLine geometry;
		locateA float;
		locateB float;
		tempLocate float;
		startPoint geometry;
		endPoint geometry;
	BEGIN
		SELECT ST_GeometryN(geom, 1) INTO geoborderLine from geoborder WHERE geoborder.geoborder_pk = geoborderPk AND "overlaps"(navdb_common.navdb__get_effective_date(), navdb_common.navdb__get_effective_date(), geoborder.validfrom::timestamp with time zone, geoborder.validto::timestamp with time zone);
		--RAISE NOTICE '% % %', ST_Line_Locate_Point(geoborderLine, pointA), ST_Line_Locate_Point(geoborderLine, pointB), ST_AsText(geoborderLine);
		locateA := ST_Line_Locate_Point(geoborderLine, pointA);
		locateB := ST_Line_Locate_Point(geoborderLine, pointB);
		IF (locateA > locateB) THEN
			tempLocate := locateA;
			locateA := locateB;
			locateB := tempLocate;
		END IF;
		--RAISE NOTICE '% % ', locateA, locateB;
		outputLine := ST_Line_Substring(geoborderLine, locateA, locateB);
		IF (tempLocate IS NOT NULL) THEN
			--RAISE NOTICE 'REVERSING';
			outputLine := ST_Reverse(outputLine);
		END IF;
		
		startPoint := ST_StartPoint(outputLine);
		endPoint := ST_EndPoint(outputLine);

		--RAISE NOTICE 'CIAOOOOO            % % % %', ST_X(startPoint),ST_X(pointA), ST_Y(startPoint), ST_Y(pointA) ;
		
		IF ( ST_X(startPoint) <> ST_X(pointA) OR ST_Y(startPoint) <> ST_Y(pointA)) THEN
			outputLine := ST_AddPoint(outputLine, pointA, 0);
			--RAISE NOTICE 'FIRST';
		END IF;

		IF ( ST_X(endPoint) <> ST_X(pointB) OR ST_Y(endPoint) <> ST_Y(pointB)) THEN
			outputLine := ST_AddPoint(outputLine, pointB);
			--RAISE NOTICE 'LAST';
		END IF;
		--RAISE NOTICE 'WKT: % \n POINT A: %\nPOINT B: %', ST_AsText(outputLine), ST_AsText(pointA), ST_AsText(pointB);
		return outputLine;
	END;
$BODY$
  LANGUAGE plpgsql VOLATILE;


-- Function: navdb_common.navdb__delete_all_data(text)

-- DROP FUNCTION navdb_common.navdb__delete_all_data(text);

CREATE OR REPLACE FUNCTION navdb_common.navdb__delete_all_data(workspace text)
  RETURNS void AS
$BODY$
	DECLARE
		r record;
	BEGIN
		PERFORM navdb_common.navdb__set_workspace(workspace);
		FOR r IN SELECT db_name FROM sm_fea where fea_pk > 0 order by db_name ASC
			LOOP
				EXECUTE 'DELETE FROM ' || r.db_name::regclass;
			END LOOP;
	END;
$BODY$
  LANGUAGE plpgsql VOLATILE;

-- Function: navdb_common.navdb__arc_by_center(geometry, geometry, geometry, boolean)

-- DROP FUNCTION navdb_common.navdb__arc_by_center(geometry, geometry, geometry, boolean);

CREATE OR REPLACE FUNCTION navdb_common.navdb__arc_by_center(
    center geometry,
    pointa geometry,
    pointb geometry,
    clockwise boolean)
  RETURNS geometry AS
$BODY$
	DECLARE
		firstAzimuth float;
		secondAzimuth float;
		computedAzimuth double precision;
		thirdPoint geometry;
		circString text;
	BEGIN
	
		firstAzimuth := ST_Azimuth(center,  pointA);
		secondAzimuth := ST_Azimuth(center,  pointB);
		--RAISE NOTICE '% %', firstAzimuth, secondAzimuth;
		IF clockWise THEN
			IF (secondAzimuth > firstAzimuth) THEN
				computedAzimuth := firstAzimuth + ((secondAzimuth - firstAzimuth) / 2);
			ELSE
				computedAzimuth := firstAzimuth + ((firstAzimuth - secondAzimuth) / 2);
			END IF;				
		ELSE
			IF (secondAzimuth > firstAzimuth) THEN
				computedAzimuth := secondAzimuth + ( (secondAzimuth -  firstAzimuth) / 2);
			ELSE
				computedAzimuth := secondAzimuth + ( (firstAzimuth -  secondAzimuth) / 2);
			END IF;				
		END IF;
		IF (computedAzimuth > 2*pi()) THEN
			computedAzimuth := computedAzimuth - 2*pi();
		END IF;
		--RAISE NOTICE '% %', computedAzimuth, computedAzimuth;
		thirdPoint := ST_Project(center::geography, ST_Distance_Sphere(center, pointA), computedAzimuth);
		circString := 'CIRCULARSTRING(' || ST_X(pointA) || ' ' || ST_Y(pointA) || ',' || ST_X(thirdPoint) || ' ' || ST_Y(thirdPoint) || ',' || ST_X(pointB) || ' ' || ST_Y(pointB) || ')';

		--RAISE NOTICE '%', circString;
		return ST_RemoveRepeatedPoints(ST_CurveToLine(ST_GeomFromText(circString)));
		
	END;
$BODY$
  LANGUAGE plpgsql VOLATILE;


-- Function: navdb_common.navdb__airspace_vertex_to_line(double precision, double precision, text, double precision, geometry, double precision, double precision, double precision, text, double precision)

-- DROP FUNCTION navdb_common.navdb__airspace_vertex_to_line(double precision, double precision, text, double precision, geometry, double precision, double precision, double precision, text, double precision);

CREATE OR REPLACE FUNCTION navdb_common.navdb__airspace_vertex_to_line(
    airspacevtxpk double precision,
    airspacebdrpk double precision,
    boundayvia text,
    noseqvalue double precision,
    startpointa geometry,
    centerpointlat double precision,
    centerpointlon double precision,
    radius double precision,
    radiusuom text,
    geoborderpk double precision)
  RETURNS geometry AS
$BODY$
	DECLARE
		finalGeom geometry;
		vertexType text;
		nextPoint geometry;
	BEGIN

		IF boundayVia <> 'CIR' THEN
			nextPoint := navdb_common.navdb__get_next_airspace_vertex_point(boundayVia, noseqValue, airspaceBdrPk);
		END IF;
		IF (boundayVia = 'GRC' OR boundayVia = 'RHL') THEN
			finalGeom := ST_MakeLine(startPointA, nextPoint);
		--ELSIF  boundayVia = 'RHL' THEN
		--	multiplier := 1;
		ELSIF  boundayVia = 'CWA' THEN
			finalGeom := navdb_common.navdb__arc_by_center(ST_SetSRID(ST_MakePoint(centerPointLon, centerPointLat), 4326), startPointA, nextPoint, true);
		ELSIF  boundayVia = 'CCA' THEN
			finalGeom := navdb_common.navdb__arc_by_center(ST_SetSRID(ST_MakePoint(centerPointLon, centerPointLat), 4326), startPointA, nextPoint, false);
		ELSIF  boundayVia = 'CIR' THEN
			finalGeom := ST_ExteriorRing(ST_Buffer(ST_SetSRID(ST_MakePoint(centerPointLon, centerPointLat), 4326)::geography, (navdb_common.navdb__to_meters(radius, radiusUom)))::geometry);
		ELSIF  boundayVia = 'FNT' THEN
			finalGeom := navdb_common.navdb__get_geoborder_line(geoborderPk, startPointA, nextPoint);
		END IF;
		return ST_SetSRID(finalGeom, 4326);
	END;
$BODY$
  LANGUAGE plpgsql VOLATILE;



-- Function: navdb_common.navdb__airspace_vertex_to_line2(double precision, double precision, text, double precision, geometry, double precision, double precision, double precision, text, double precision, geometry)

-- DROP FUNCTION navdb_common.navdb__airspace_vertex_to_line2(double precision, double precision, text, double precision, geometry, double precision, double precision, double precision, text, double precision, geometry);

CREATE OR REPLACE FUNCTION navdb_common.navdb__airspace_vertex_to_line2(
    airspacevtxpk double precision,
    airspacebdrpk double precision,
    boundayvia text,
    noseqvalue double precision,
    startpointa geometry,
    centerpointlat double precision,
    centerpointlon double precision,
    radius double precision,
    radiusuom text,
    geoborderpk double precision,
    nextpoint geometry)
  RETURNS geometry AS
$BODY$
	DECLARE
		finalGeom geometry;
		vertexType text;
	BEGIN
		IF (boundayVia = 'GRC' OR boundayVia = 'RHL') THEN
			finalGeom := ST_MakeLine(startPointA, nextPoint);
		--ELSIF  boundayVia = 'RHL' THEN
		--	multiplier := 1;
		ELSIF  boundayVia = 'CWA' THEN
			finalGeom := navdb_common.navdb__arc_by_center(ST_SetSRID(ST_MakePoint(centerPointLon, centerPointLat), 4326), startPointA, nextPoint, true);
		ELSIF  boundayVia = 'CCA' THEN
			finalGeom := navdb_common.navdb__arc_by_center(ST_SetSRID(ST_MakePoint(centerPointLon, centerPointLat), 4326), startPointA, nextPoint, false);
		ELSIF  boundayVia = 'CIR' THEN
			finalGeom := ST_ExteriorRing(ST_Buffer(ST_SetSRID(ST_MakePoint(centerPointLon, centerPointLat), 4326)::geography, (navdb_common.navdb__to_meters(radius, radiusUom)))::geometry);
		ELSIF  boundayVia = 'FNT' THEN
			finalGeom := navdb_common.navdb__get_geoborder_line(geoborderPk, startPointA, nextPoint);
		END IF;
		return ST_SetSRID(finalGeom, 4326);
	END;
$BODY$
  LANGUAGE plpgsql VOLATILE;


-- Function: navdb_common.navdb__compute_airspace_association_geometry(double precision, boolean)

-- DROP FUNCTION navdb_common.navdb__compute_airspace_association_geometry(double precision, boolean);

CREATE OR REPLACE FUNCTION navdb_common.navdb__compute_airspace_association_geometry(
    airspacepkvalue double precision,
    storeintermediateassociation boolean)
  RETURNS geometry AS
$BODY$
	DECLARE
		finalGeom geometry;
		lineGeometry geometry;
		objectGeometry geometry;
		madeValid geometry;
		r record;
		selfGeomCheck geometry;
	BEGIN
		finalGeom := NULL;
		SELECT geom INTO selfGeomCheck FROM airspace where airspace_pk = airspacePkValue;
		IF (selfGeomCheck IS NULL) THEN
			FOR r IN SELECT airspace_pk2, codeopr FROM airspcassoc where airspace_pk = airspacePkValue AND airspace_pk <> airspace_pk2 order by noseqopr ASC
			LOOP  
				--RAISE NOTICE 'ASSOC_PK: % %', airspacePkValue, r.airspace_pk2;
				SELECT geom INTO objectGeometry FROM airspace where airspace_pk = r.airspace_pk2;
				IF (objectGeometry IS NULL) THEN
					--RAISE NOTICE 'CURRENT GEOM: %', ST_AsText(objectGeometry);
					return NULL;
				END IF;
				IF (r.codeopr = 'BASE') THEN
					--RAISE NOTICE 'BASE';
					finalGeom := objectGeometry;
				ELSIF (r.codeopr = 'SUBTR') THEN
					--RAISE NOTICE 'SUBTR';
					finalGeom := ST_Difference(ST_MakeValid(finalGeom), objectGeometry);
				ELSIF (r.codeopr = 'INTERS') THEN
					--RAISE NOTICE 'INTERS';
					finalGeom := ST_Intersection(ST_MakeValid(finalGeom), objectGeometry);
				ELSIF (r.codeopr = 'UNION') THEN
					--RAISE NOTICE 'UNION';
					finalGeom := ST_Union(ST_MakeValid(finalGeom), objectGeometry);
				END IF;
				finalGeom := ST_CollectionHomogenize(ST_Polygonize(finalGeom));
				madeValid := ST_MakeValid(finalGeom);
				IF (GeometryType(madeValid) <> 'GEOMETRYCOLLECTION') THEN
					finalGeom := (madeValid);
				END IF;
				--RAISE NOTICE 'CURRENT GEOM: %', ST_AsText(finalGeom);
			END LOOP; 
			finalGeom := ST_Multi(finalGeom);
			IF (ST_IsEmpty(finalGeom) AND GeometryType(finalGeom) = 'GEOMETRYCOLLECTION') THEN
				finalGeom := ST_SetSRID(ST_GeomFromText('MULTIPOLYGON EMPTY'), 4326);
			END IF;
		ELSE
			--RAISE NOTICE 'GEOM ALREADY COMPUTED';
		END IF;
		return finalGeom;
	END;
$BODY$
  LANGUAGE plpgsql VOLATILE;



-- Function: navdb_common.navdb__compute_airspace_geometry(double precision)

-- DROP FUNCTION navdb_common.navdb__compute_airspace_geometry(double precision);

CREATE OR REPLACE FUNCTION navdb_common.navdb__compute_airspace_geometry(airspacepkvalue double precision)
  RETURNS geometry AS
$BODY$
	DECLARE
		finalGeom geometry;
		lineGeometry geometry;
		borderWidth double precision;
		borderWidthUom character varying(40);
		borderType character varying(4);
		borderWidthInMeters double precision;
		airspaceBdrPk double precision;
		madeValid geometry;
	BEGIN
		finalGeom := NULL;
		SELECT airspacebdr_pk, airspacebdr.bordertype, valwidth, uomwidth INTO airspaceBdrPk, borderType, borderWidth, borderWidthUom FROM airspacebdr where airspace_pk = airspacePkValue;
		RAISE NOTICE '% AREA TYPE: %', airspacePkValue, borderType;
		IF (airspaceBdrPk IS NOT NULL) THEN
			SELECT ST_LineMerge(ST_GeomFromText(ST_AsText(ST_Collect(ST_RemoveRepeatedPoints(navdb_common.navdb__airspace_vertex_to_line(airspacevtx_pk, airspacevtx.airspacebdr_pk, boundaryvia::text, noseq:: double precision, ST_SetSRID(ST_MakePoint(segmentLon, segmentLat), 4326), arcoriglat, arcoriglon, arcradius, radiusunits::text, geoborder_pk)))))) INTO lineGeometry FROM airspacevtx where airspacevtx.airspacebdr_pk = airspaceBdrPk AND airspacevtx.boundaryvia <> 'END';
			--RAISE NOTICE 'FINAL GEOM: %', ST_AsText(finalGeom);
			IF (borderType = 'A') THEN
				SELECT ST_Multi(ST_MakePolygon(lineGeometry)) INTO finalGeom;
			ELSE 
				borderWidthInMeters := navdb_common.navdb__to_meters(borderWidth, borderWidthUom);
				SELECT ST_Multi(ST_Buffer(lineGeometry::geography, borderWidthInMeters)::geometry) INTO finalGeom;
			END IF;
			finalGeom := navdb_common.navdb__lon_wrap(finalGeom);
			madeValid := ST_MakeValid(finalGeom);
			IF (GeometryType(madeValid) <> 'GEOMETRYCOLLECTION') THEN
				finalGeom := madeValid;
			END IF;
			return ST_SetSRID(finalGeom, 4326);
		ELSE
			RAISE NOTICE 'NO BORDER';
			--finalGeom := navdb_common.computeAirspaceAssociationGeometry(airspacePkValue, true);
			return finalGeom;
		END IF;
	END;
$BODY$
  LANGUAGE plpgsql VOLATILE;


-- Function: navdb_common.navdb__compute_airspace_geometry2(double precision)

-- DROP FUNCTION navdb_common.navdb__compute_airspace_geometry2(double precision);

CREATE OR REPLACE FUNCTION navdb_common.navdb__compute_airspace_geometry2(airspacepkvalue double precision)
  RETURNS geometry AS
$BODY$
	DECLARE
		finalGeom geometry;
		lineGeometry geometry;
		borderWidth double precision;
		borderWidthUom character varying(40);
		borderType character varying(4);
		borderWidthInMeters double precision;
		airspaceBdrPk double precision;
		madeValid geometry;
		tempLine geometry;
		r record;
		previousRecord record;
		firstRecord record;
		counter int;
	BEGIN
		finalGeom := NULL;
		previousRecord := NULL;
		lineGeometry := NULL;
		counter := 1;
		SELECT airspacebdr_pk, airspacebdr.bordertype, valwidth, uomwidth INTO airspaceBdrPk, borderType, borderWidth, borderWidthUom FROM airspacebdr where airspace_pk = airspacePkValue;
		RAISE NOTICE '% AREA TYPE: %', airspacePkValue, borderType;
		IF (airspaceBdrPk IS NOT NULL) THEN
			--SELECT ST_LineMerge(ST_GeomFromText(ST_AsText(ST_Collect(ST_RemoveRepeatedPoints(navdb_common.navdb__airspace_vertex_to_line(airspacevtx_pk, airspacevtx.airspacebdr_pk, boundaryvia::text, noseq:: double precision, ST_SetSRID(ST_MakePoint(segmentLon, segmentLat), 4326), arcoriglat, arcoriglon, arcradius, radiusunits::text, geoborder_pk)))))) INTO lineGeometry FROM airspacevtx where airspacevtx.airspacebdr_pk = airspaceBdrPk AND airspacevtx.boundaryvia <> 'END';

			FOR r IN SELECT * FROM airspacevtx where "overlaps"(navdb__get_effective_date(), navdb__get_effective_date(), validfrom::timestamp with time zone, validto::timestamp with time zone) AND airspacebdr_pk = airspaceBdrPk ORDER BY noseq ASC
			LOOP
				IF (counter > 1) THEN
					tempLine := navdb_common.navdb__airspace_vertex_to_line2(previousRecord.airspacevtx_pk, previousRecord.airspacebdr_pk, previousRecord.boundaryvia::text, previousRecord.noseq:: double precision, ST_SetSRID(ST_MakePoint(previousRecord.segmentLon, previousRecord.segmentLat), 4326), previousRecord.arcoriglat, previousRecord.arcoriglon, previousRecord.arcradius, previousRecord.radiusunits::text, previousRecord.geoborder_pk, ST_SetSRID(ST_MakePoint(r.segmentlon, r.segmentlat), 4326));
					IF (lineGeometry IS NULL) THEN
						lineGeometry := tempLine;
					ELSE
						lineGeometry := ST_SetSRID(ST_LineMerge(ST_GeomFromText(ST_AsText(ST_Collect(lineGeometry, tempLine)))), 4326);
					END IF;
				END IF;
				previousRecord := r;
				IF (counter = 1) THEN
					firstRecord := r;
				END IF;
				counter := counter + 1;
			END LOOP;

			IF (borderType = 'A') THEN
				--close the ring
				RAISE NOTICE '% ', ST_AsText(lineGeometry);
				tempLine := navdb_common.navdb__airspace_vertex_to_line2(previousRecord.airspacevtx_pk, previousRecord.airspacebdr_pk, previousRecord.boundaryvia::text, previousRecord.noseq:: double precision, ST_SetSRID(ST_MakePoint(previousRecord.segmentLon, previousRecord.segmentLat), 4326), previousRecord.arcoriglat, previousRecord.arcoriglon, previousRecord.arcradius, previousRecord.radiusunits::text, previousRecord.geoborder_pk, ST_SetSRID(ST_MakePoint(firstRecord.segmentlon, firstRecord.segmentlat), 4326));
				lineGeometry := ST_SetSRID(ST_LineMerge(ST_GeomFromText(ST_AsText(ST_Collect(lineGeometry, tempLine)))), 4326);
				RAISE NOTICE '% ', ST_AsText(lineGeometry);
				--build the polygon
				SELECT ST_Multi(ST_MakePolygon(lineGeometry)) INTO finalGeom;
			ELSE 
				borderWidthInMeters := navdb_common.navdb__to_meters(borderWidth, borderWidthUom);
				SELECT ST_Multi(ST_Buffer(lineGeometry::geography, borderWidthInMeters)::geometry) INTO finalGeom;
			END IF;
			finalGeom := navdb_common.navdb__lon_wrap(finalGeom);
			madeValid := ST_MakeValid(finalGeom);
			IF (GeometryType(madeValid) <> 'GEOMETRYCOLLECTION') THEN
				finalGeom := madeValid;
			END IF;
			return ST_SetSRID(finalGeom, 4326);
		ELSE
			RAISE NOTICE 'NO BORDER';
			--finalGeom := navdb_common.computeAirspaceAssociationGeometry(airspacePkValue, true);
			return finalGeom;
		END IF;
	END;
$BODY$
  LANGUAGE plpgsql VOLATILE;


-- Function: navdb_common.copy_from_to(text, text)

-- DROP FUNCTION navdb_common.copy_from_to(text, text);

CREATE OR REPLACE FUNCTION navdb_common.copy_from_to(
    workspacefrom text,
    workspaceto text)
  RETURNS void AS
$BODY$
	DECLARE
		r record;
		mystatement text;
	BEGIN
		FOR r IN SELECT db_name FROM sm_fea where fea_pk > 0 order by db_name ASC
			LOOP
				mystatement := 'INSERT INTO ' || workspaceTo || '.' || r.db_name::regclass || ' (SELECT * FROM ' || workspaceFrom || '.' || r.db_name::regclass  || ')';
				RAISE NOTICE '%', mystatement;
				EXECUTE mystatement;
			END LOOP;
	END;
$BODY$
  LANGUAGE plpgsql VOLATILE;


-- Function: navdb_common.navdb__compute_airspace_associations()

-- DROP FUNCTION navdb_common.navdb__compute_airspace_associations();

CREATE OR REPLACE FUNCTION navdb_common.navdb__compute_airspace_associations()
  RETURNS void AS
$BODY$
	DECLARE
		someAssociationComputed boolean;
		numberOfAssoc int;
		computedGeom geometry;
		r record;
	BEGIN
		numberOfAssoc := 1;
		WHILE numberOfAssoc > 0 LOOP
			RAISE NOTICE 'COMPUTED ASSOC: %', numberOfAssoc;
			numberOfAssoc := 0;
			FOR r IN SELECT distinct ASSOC.airspace_pk FROM airspcassoc ASSOC, airspace A where ASSOC.airspace_pk =  A.airspace_pk AND A.GEOM IS NULL AND ASSOC.airspace_pk2 NOT IN (SELECT ASSOC_Int.airspace_pk FROM airspcassoc ASSOC_Int, airspace A_Int where ASSOC_Int.airspace_pk = A_Int.airspace_pk AND A_Int.GEOM IS NULL)
			LOOP
				computedGeom := navdb_common.navdb__compute_airspace_association_geometry(r.airspace_pk, false);
				IF (computedGeom IS NOT NULL AND (GeometryType(computedGeom) <> 'GEOMETRYCOLLECTION')) THEN
					numberOfAssoc := numberOfAssoc + 1;
					UPDATE airspace SET geom = computedGeom WHERE airspace_pk = r.airspace_pk;
				ELSE
					--pazienza
				END IF;
			END LOOP;
		END LOOP;
	END;
$BODY$
  LANGUAGE plpgsql VOLATILE;


-- Function: navdb_common.navdb__update_all_geometries(timestamp with time zone)

-- DROP FUNCTION navdb_common.navdb__update_all_geometries(timestamp with time zone);

CREATE OR REPLACE FUNCTION navdb_common.navdb__update_all_geometries(p_date timestamp with time zone)
  RETURNS void AS
$BODY$
	DECLARE
		
	BEGIN
		PERFORM navdb_common.navdb__set_effective_date(p_date);
		RAISE NOTICE 'Recalculating DMEs';
		update dme set geom = ST_SetSRID(ST_MakePoint(geolong, geolat), 4326) where "overlaps"(navdb__get_effective_date(), navdb__get_effective_date(), validfrom::timestamp with time zone, validto::timestamp with time zone);
		RAISE NOTICE 'Recalculating Airports';
		update airport set geom = ST_SetSRID(ST_MakePoint(longitude, latitude), 4326) where "overlaps"(navdb__get_effective_date(), navdb__get_effective_date(), validfrom::timestamp with time zone, validto::timestamp with time zone);
		RAISE NOTICE 'Recalculating Waypoints';
		update waypoint set geom = ST_SetSRID(ST_MakePoint(longitude, latitude), 4326) where "overlaps"(navdb__get_effective_date(), navdb__get_effective_date(), validfrom::timestamp with time zone, validto::timestamp with time zone);
		RAISE NOTICE 'Recalculating VORs';
		update vor set geom = ST_SetSRID(ST_MakePoint(longitude, latitude), 4326) where "overlaps"(navdb__get_effective_date(), navdb__get_effective_date(), validfrom::timestamp with time zone, validto::timestamp with time zone);
		RAISE NOTICE 'Recalculating Markers';
		update marker set geom = ST_SetSRID(ST_MakePoint(geolong, geolat), 4326) where"overlaps"(navdb__get_effective_date(), navdb__get_effective_date(), validfrom::timestamp with time zone, validto::timestamp with time zone);
		RAISE NOTICE 'Recalculating IlsGp';
		update ilsgp set geom = ST_SetSRID(ST_MakePoint(geolong, geolat), 4326) where "overlaps"(navdb__get_effective_date(), navdb__get_effective_date(), validfrom::timestamp with time zone, validto::timestamp with time zone);
		RAISE NOTICE 'Recalculating IlsLlz';
		update ilsllz set geom = ST_SetSRID(ST_MakePoint(geolong, geolat), 4326) where "overlaps"(navdb__get_effective_date(), navdb__get_effective_date(), validfrom::timestamp with time zone, validto::timestamp with time zone);
		RAISE NOTICE 'Recalculating NDBs';
		update ndb set geom = ST_SetSRID(ST_MakePoint(geolong, geolat), 4326) where "overlaps"(navdb__get_effective_date(), navdb__get_effective_date(), validfrom::timestamp with time zone, validto::timestamp with time zone);
		RAISE NOTICE 'Recalculating Tacans';
		update tacan  set geom = ST_SetSRID(ST_MakePoint(geolong, geolat), 4326) where "overlaps"(navdb__get_effective_date(), navdb__get_effective_date(), validfrom::timestamp with time zone, validto::timestamp with time zone);
		RAISE NOTICE 'Recalculating Runway directions';
		update rwydirection  set geom = ST_SetSRID(ST_MakePoint(threshlon, threshlat), 4326) where "overlaps"(navdb__get_effective_date(), navdb__get_effective_date(), validfrom::timestamp with time zone, validto::timestamp with time zone);
		RAISE NOTICE 'Recalculating Geoborders';
		update geoborder GB set geom = (SELECT ST_SetSRID(ST_Multi(ST_MakeLine(ST_MakePoint(B.segmentlon, B.segmentlat))), 4326) FROM (SELECT C.segmentlon, C.segmentlat FROM geobordervtx C where "overlaps"(navdb__get_effective_date(), navdb__get_effective_date(), C.validfrom::timestamp with time zone, C.validto::timestamp with time zone) AND C.geoborder_pk = GB.geoborder_pk order by C.noseq ASC) B) where "overlaps"(navdb__get_effective_date(), navdb__get_effective_date(), GB.validfrom::timestamp with time zone, GB.validto::timestamp with time zone);
		RAISE NOTICE 'Recalculating Obstacles';
		update obstacle O set geom = (SELECT ST_SetSRID(ST_GeomCollFromText('GEOMETRYCOLLECTION(' || ST_AsText(ST_MakePoint(OPG.longitude, OPG.latitude)) || ')'), 4326)) FROM obspart_geom OPG, obstaclepart OBP where "overlaps"(navdb__get_effective_date(), navdb__get_effective_date(), OPG.validfrom::timestamp with time zone, OPG.validto::timestamp with time zone) AND OBP.obstacle_pk = O.obstacle_pk AND "overlaps"(navdb__get_effective_date(), navdb__get_effective_date(), OBP.validfrom::timestamp with time zone, OBP.validto::timestamp with time zone) AND OPG.obstaclepart_pk = OBP.obstaclepart_pk AND "overlaps"(navdb__get_effective_date(), navdb__get_effective_date(), O.validfrom::timestamp with time zone, O.validto::timestamp with time zone);

		RAISE NOTICE 'Recalculating Segments';
		update segmnt set geom = (ST_SetSRID(ST_Multi(ST_Segmentize(ST_MakeLine(ST_MakePoint(fromfix.longitude, fromfix.latitude), ST_MakePoint(tofix.longitude, tofix.latitude))::geography, 10000)::geometry) , 4326))  FROM (
			 SELECT sf.fea_pk,
			    (sf.doc_name::text || ', '::text) || waypoint.ident::text AS ident,
			    waypoint.waypoint_pk AS occ_pk,
			    longitude,
			    latitude
			   FROM waypoint,
			    sm_fea sf
			  WHERE sf.db_name::text = 'waypoint'::text AND "overlaps"(navdb__get_effective_date(), navdb__get_effective_date(), validfrom::timestamp with time zone, validto::timestamp with time zone)
			UNION
			 SELECT sf.fea_pk,
			    (sf.doc_name::text || ', '::text) || tacan.codeid::text AS ident,
			    tacan.tacan_pk AS occ_pk,
			    geolong,
			    geolat
			   FROM tacan,
			    sm_fea sf
			  WHERE sf.db_name::text = 'tacan'::text AND "overlaps"(navdb__get_effective_date(), navdb__get_effective_date(), validfrom::timestamp with time zone, validto::timestamp with time zone)
			UNION
			 SELECT sf.fea_pk,
			    (sf.doc_name::text || ', '::text) || vor.ident::text AS ident,
			    vor.vor_pk AS occ_pk,
			    longitude,
			    latitude
			   FROM vor,
			    sm_fea sf
			  WHERE sf.db_name::text = 'vor'::text AND "overlaps"(navdb__get_effective_date(), navdb__get_effective_date(), validfrom::timestamp with time zone, validto::timestamp with time zone)
			UNION
			 SELECT sf.fea_pk,
			    (sf.doc_name::text || ', '::text) || ndb.codeid::text AS ident,
			    ndb.ndb_pk AS occ_pk,
			    geolong,
			    geolat
			   FROM ndb,
			    sm_fea sf
			  WHERE sf.db_name::text = 'ndb'::text AND "overlaps"(navdb__get_effective_date(), navdb__get_effective_date(), validfrom::timestamp with time zone, validto::timestamp with time zone)
			UNION
			 SELECT sf.fea_pk,
			    (sf.doc_name::text || ', '::text) || marker.codeid::text AS ident,
			    marker.marker_pk AS occ_pk,
			    geolong,
			    geolat
			   FROM marker,
			    sm_fea sf
			  WHERE sf.db_name::text = 'marker'::text AND "overlaps"(navdb__get_effective_date(), navdb__get_effective_date(), validfrom::timestamp with time zone, validto::timestamp with time zone)
			UNION
			 SELECT sf.fea_pk,
			    (sf.doc_name::text || ', '::text) || airport.ident::text AS ident,
			    airport.airport_pk AS occ_pk,
			    longitude,
			    latitude
			   FROM airport,
			    sm_fea sf
			  WHERE sf.db_name::text = 'airport'::text AND "overlaps"(navdb__get_effective_date(), navdb__get_effective_date(), validfrom::timestamp with time zone, validto::timestamp with time zone)
			UNION
			 SELECT sf.fea_pk,
			    (sf.doc_name::text || ', '::text) || dme.codeid::text AS ident,
			    dme.dme_pk AS occ_pk,
			    geolong,
			    geolat
			   FROM dme,
			    sm_fea sf
			  WHERE sf.db_name::text = 'dme'::text AND "overlaps"(navdb__get_effective_date(), navdb__get_effective_date(), validfrom::timestamp with time zone, validto::timestamp with time zone)) fromfix, (
			 SELECT sf.fea_pk,
			    (sf.doc_name::text || ', '::text) || waypoint.ident::text AS ident,
			    waypoint.waypoint_pk AS occ_pk,
			    longitude,
			    latitude
			   FROM waypoint,
			    sm_fea sf
			  WHERE sf.db_name::text = 'waypoint'::text AND "overlaps"(navdb__get_effective_date(), navdb__get_effective_date(), validfrom::timestamp with time zone, validto::timestamp with time zone)
			UNION
			 SELECT sf.fea_pk,
			    (sf.doc_name::text || ', '::text) || tacan.codeid::text AS ident,
			    tacan.tacan_pk AS occ_pk,
			    geolong,
			    geolat
			   FROM tacan,
			    sm_fea sf
			  WHERE sf.db_name::text = 'tacan'::text AND "overlaps"(navdb__get_effective_date(), navdb__get_effective_date(), validfrom::timestamp with time zone, validto::timestamp with time zone)
			UNION
			 SELECT sf.fea_pk,
			    (sf.doc_name::text || ', '::text) || vor.ident::text AS ident,
			    vor.vor_pk AS occ_pk,
			    longitude,
			    latitude
			   FROM vor,
			    sm_fea sf
			  WHERE sf.db_name::text = 'vor'::text AND "overlaps"(navdb__get_effective_date(), navdb__get_effective_date(), validfrom::timestamp with time zone, validto::timestamp with time zone)
			UNION
			 SELECT sf.fea_pk,
			    (sf.doc_name::text || ', '::text) || ndb.codeid::text AS ident,
			    ndb.ndb_pk AS occ_pk,
			    geolong,
			    geolat
			   FROM ndb,
			    sm_fea sf
			  WHERE sf.db_name::text = 'ndb'::text AND "overlaps"(navdb__get_effective_date(), navdb__get_effective_date(), validfrom::timestamp with time zone, validto::timestamp with time zone)
			UNION
			 SELECT sf.fea_pk,
			    (sf.doc_name::text || ', '::text) || marker.codeid::text AS ident,
			    marker.marker_pk AS occ_pk,
			    geolong,
			    geolat
			   FROM marker,
			    sm_fea sf
			  WHERE sf.db_name::text = 'marker'::text AND "overlaps"(navdb__get_effective_date(), navdb__get_effective_date(), validfrom::timestamp with time zone, validto::timestamp with time zone)
			UNION
			 SELECT sf.fea_pk,
			    (sf.doc_name::text || ', '::text) || airport.ident::text AS ident,
			    airport.airport_pk AS occ_pk,
			    longitude,
			    latitude
			   FROM airport,
			    sm_fea sf
			  WHERE sf.db_name::text = 'airport'::text AND "overlaps"(navdb__get_effective_date(), navdb__get_effective_date(), validfrom::timestamp with time zone, validto::timestamp with time zone)
			UNION
			 SELECT sf.fea_pk,
			    (sf.doc_name::text || ', '::text) || dme.codeid::text AS ident,
			    dme.dme_pk AS occ_pk,
			    geolong,
			    geolat
			   FROM dme,
			    sm_fea sf
			  WHERE sf.db_name::text = 'dme'::text AND "overlaps"(navdb__get_effective_date(), navdb__get_effective_date(), validfrom::timestamp with time zone, validto::timestamp with time zone)) tofix 
			  where "overlaps"(navdb__get_effective_date(), navdb__get_effective_date(), validfrom::timestamp with time zone, validto::timestamp with time zone) AND fromfixfea_pk = fromfix.fea_pk AND fromfix_pk = fromfix.occ_pk AND tofixfea_pk = tofix.fea_pk AND tofix_pk = tofix.occ_pk;

		UPDATE segmnt set GEOM =  navdb_common.navdb__lon_wrap_line(GEOM) WHERE GEOM IS NOT NULL  AND "overlaps"(navdb__get_effective_date(), navdb__get_effective_date(), validfrom::timestamp with time zone, validto::timestamp with time zone);

		RAISE NOTICE 'Recalculating Airway segments';
		update airwayseg ASEG set geom = (S.geom) FROM SEGMNT S where "overlaps"(navdb__get_effective_date(), navdb__get_effective_date(), ASEG.validfrom::timestamp with time zone, ASEG.validto::timestamp with time zone) AND ASEG.segmnt_pk = S.segmnt_pk AND "overlaps"(navdb__get_effective_date(), navdb__get_effective_date(), S.validfrom::timestamp with time zone, S.validto::timestamp with time zone);

		RAISE NOTICE 'Recalculating Airways';
		update airway route SET geom = (SELECT ST_SetSRID(ST_union(grouping.geom), 4326) FROM (SELECT geom FROM airwayseg ASEG WHERE ASEG.airway_pk = route.airway_pk AND "overlaps"(navdb__get_effective_date(), navdb__get_effective_date(), ASEG.validfrom::timestamp with time zone, ASEG.validto::timestamp with time zone) order by seq ASC) grouping) where "overlaps"(navdb__get_effective_date(), navdb__get_effective_date(), route.validfrom::timestamp with time zone, route.validto::timestamp with time zone);

		RAISE NOTICE 'Recalculating Airspaces';
		UPDATE AIRSPACE SET GEOM = navdb_common.navdb__compute_airspace_geometry2(airspace_pk) WHERE geom IS NULL AND "overlaps"(navdb__get_effective_date(), navdb__get_effective_date(), validfrom::timestamp with time zone, validto::timestamp with time zone);

		RAISE NOTICE 'Recalculating Airspace Associations';
		PERFORM navdb_common.navdb__compute_airspace_associations();
	END;
$BODY$
  LANGUAGE plpgsql VOLATILE;

