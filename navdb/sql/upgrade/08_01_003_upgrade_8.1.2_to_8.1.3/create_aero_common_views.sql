-- vim:ts=4:sts=4:sw=4:et

set search_path to navdb_common,navdb,public,pg_catalog;

--------------------------------------------------
-- V_AIRPORT
--------------------------------------------------
CREATE VIEW V_AIRPORT (
    AIRPORT_PK,
    AIRSPACE_PK,
    IDENT,
    TYP,
    NAM,
    MAGVARIATION,
    MAGVARDATE,
    ELEVATION,
    CODEWRKHR,
    UOMDISTVER,
    GEOM
) AS
	SELECT  airport.airport_pk,
            airport.airspace_pk,
            airport.ident,
            airport.typ, 
            airport.nam,
            airport.magvariation,
            airport.magvardate,
            airport.elevation, 
            airport.codewrkhr,
            airport.UOMDISTVER,
            airport.geom
       FROM navdb_staging.V_AIRPORT airport
	UNION
	(
		SELECT  airport_navdb.airport_pk,
            airport_navdb.airspace_pk,
            airport_navdb.ident,
            airport_navdb.typ, 
            airport_navdb.nam,
            airport_navdb.magvariation,
            airport_navdb.magvardate,
            airport_navdb.elevation, 
            airport_navdb.codewrkhr,
            airport_navdb.UOMDISTVER,
            airport_navdb.geom
       FROM navdb.V_AIRPORT airport_navdb WHERE airport_navdb.IDENT NOT IN (
			SELECT ident FROM navdb_staging.V_AIRPORT
	   )
	)
;

--------------------------------------------------
-- V_AIRSPACE
--------------------------------------------------
CREATE VIEW v_airspace AS 
         SELECT a.airspace_pk,
            a.typ,
            a.ident,
            a.codeclass,
            a.icaocode,
            a.nam,
            a.areacode,
            a.upperlimit,
            a.uplimitunit,
            a.absupperlimit,
            a.lowerlimit,
            a.lowerlimitunit,
            a.abslowerlimit,
            a.codeactivity,
            a.geom
           FROM navdb_staging.v_airspace a
UNION
         SELECT a_navdb.airspace_pk,
            a_navdb.typ,
            a_navdb.ident,
            a_navdb.codeclass,
            a_navdb.icaocode,
            a_navdb.nam,
            a_navdb.areacode,
            a_navdb.upperlimit,
            a_navdb.uplimitunit,
            a_navdb.absupperlimit,
            a_navdb.lowerlimit,
            a_navdb.lowerlimitunit,
            a_navdb.abslowerlimit,
            a_navdb.codeactivity,
            a_navdb.geom
           FROM navdb.v_airspace a_navdb
          WHERE NOT (a_navdb.ident::text IN ( SELECT a_1.ident
                   FROM navdb_staging.v_airspace a_1));
	
--------------------------------------------------
-- V_AIRSPACE_ALL
--------------------------------------------------
CREATE VIEW v_airspace_all AS 
         SELECT a.airspace_pk,
            a.typ,
            a.ident,
            a.codeclass,
            a.icaocode,
            a.nam,
            a.areacode,
            a.upperlimit,
            a.uplimitunit,
            a.absupperlimit,
            a.lowerlimit,
            a.lowerlimitunit,
            a.abslowerlimit,
            a.codeactivity,
            a.geom
           FROM navdb_staging.v_airspace_all a
UNION
         SELECT a_navdb.airspace_pk,
            a_navdb.typ,
            a_navdb.ident,
            a_navdb.codeclass,
            a_navdb.icaocode,
            a_navdb.nam,
            a_navdb.areacode,
            a_navdb.upperlimit,
            a_navdb.uplimitunit,
            a_navdb.absupperlimit,
            a_navdb.lowerlimit,
            a_navdb.lowerlimitunit,
            a_navdb.abslowerlimit,
            a_navdb.codeactivity,
            a_navdb.geom
           FROM navdb.v_airspace_all a_navdb
          WHERE NOT (a_navdb.ident::text IN ( SELECT a_1.ident
                   FROM navdb_staging.v_airspace_all a_1));
