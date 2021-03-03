-- vim:ts=4:sts=4:sw=4:et



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
WITH const as (select navdb__get_effective_date() as effective_date)
    SELECT  airport.airport_pk,
            airport.airspace_pk,
            airport.ident,
            airport.typ, 
            airport.nam,
            airport.magvariation,
            airport.magvardate,
            airport.elevation, 
            airport.codewrkhr,
            airport.fieldelevationuom AS uomdistver,
            airport.geom
       FROM airport, const
      WHERE "overlaps"(const.effective_date, const.effective_date, airport.validfrom::timestamp with time zone, airport.validto::timestamp with time zone)
;

--------------------------------------------------
-- V_AIRSPACE
--------------------------------------------------
CREATE OR REPLACE VIEW v_airspace (
    airspace_pk,
    typ,
    ident,
    codeclass,
    icaocode,
    nam,
    areacode,
    upperlimit,
    uplimitunit,
    absupperlimit,
    lowerlimit,
    lowerlimitunit,
    abslowerlimit,
    codeactivity,
    geom
) AS
WITH const as (select navdb__get_effective_date() as effective_date)
    SELECT  a.airspace_pk,
            a.typ, a.ident,
            a.codeclass,
            a.icaocode,
            a.nam, 
            a.areacode,
            a.upperlimit,
            a.uplimitunit, 
            CASE
                WHEN a.uplimitunit::text = 'FT'::text THEN a.upperlimit / 100::double precision
                WHEN a.uplimitunit::text = 'M'::text THEN a.upperlimit * 3.2808::double precision / 100::double precision
                WHEN a.uplimitunit::text = 'FL'::text THEN a.upperlimit
                ELSE a.upperlimit
            END AS absupperlimit, 
            a.lowerlimit,
            a.lowerlimitunit, 
            CASE
                WHEN a.lowerlimitunit::text = 'FT'::text THEN a.lowerlimit / 100::double precision
                WHEN a.lowerlimitunit::text = 'M'::text THEN a.lowerlimit * 3.2808::double precision / 100::double precision
                WHEN a.lowerlimitunit::text = 'FL'::text THEN a.lowerlimit
                ELSE a.upperlimit
            END AS abslowerlimit, 
            a.codeactivity,
            a.geom
      FROM  airspace a, const
     WHERE  a.typ::text <> 'FIR'::text
       AND  a.typ::text <> 'UIR'::text
       AND  a.geom IS NOT NULL
       and "overlaps"(const.effective_date, const.effective_date, a.validfrom::timestamp with time zone, a.validto::timestamp with time zone)
;
	
--------------------------------------------------
-- V_AIRSPACE_ALL
--------------------------------------------------
CREATE OR REPLACE VIEW v_airspace_all (
    airspace_pk,
    typ,
    ident,
    codeclass,
    icaocode,
    nam,
    areacode,
    upperlimit,
    uplimitunit,
    absupperlimit,
    lowerlimit,
    lowerlimitunit,
    abslowerlimit,
    codeactivity,
    geom
) AS
WITH const as (select navdb__get_effective_date() as effective_date)
    SELECT  a.airspace_pk,
            a.typ, a.ident,
            a.codeclass,
            a.icaocode,
            a.nam, 
            a.areacode,
            a.upperlimit,
            a.uplimitunit, 
            CASE
                WHEN a.uplimitunit::text = 'FT'::text THEN a.upperlimit / 100::double precision
                WHEN a.uplimitunit::text = 'M'::text THEN a.upperlimit * 3.2808::double precision / 100::double precision
                WHEN a.uplimitunit::text = 'FL'::text THEN a.upperlimit
                ELSE a.upperlimit
            END AS absupperlimit, 
            a.lowerlimit,
            a.lowerlimitunit, 
            CASE
                WHEN a.lowerlimitunit::text = 'FT'::text THEN a.lowerlimit / 100::double precision
                WHEN a.lowerlimitunit::text = 'M'::text THEN a.lowerlimit * 3.2808::double precision / 100::double precision
                WHEN a.lowerlimitunit::text = 'FL'::text THEN a.lowerlimit
                ELSE a.upperlimit
            END AS abslowerlimit, 
            a.codeactivity,
            a.geom
      FROM  airspace a, const
     WHERE  a.geom IS NOT NULL
       and "overlaps"(const.effective_date, const.effective_date, a.validfrom::timestamp with time zone, a.validto::timestamp with time zone)
;

--------------------------------------------------
-- V_AIRWAIY
--------------------------------------------------
CREATE OR REPLACE VIEW V_AIRWAY (
    AIRWAY_PK,
    TXTDESIG
) AS
WITH const as (select navdb__get_effective_date() as effective_date)
    SELECT  AIRWAY_PK,
            TXTDESIG
      FROM  AIRWAY, const
     WHERE  "overlaps"(const.effective_date, const.effective_date, AIRWAY.validfrom::timestamp with time zone, AIRWAY.validto::timestamp with time zone)
;

--------------------------------------------------
-- V_DME
--------------------------------------------------
CREATE VIEW V_DME (
    DME_PK,
    CODEID,
    GEOLAT,
    GEOLONG,
    VALELEV,
    UOMDISTVER,
    TXTRMK,
    CODECHANNEL,
    VALCHANNEL,
    TXTNAME,
    GEOM
) AS 
WITH const as (select navdb__get_effective_date() as effective_date)
    SELECT  DME_PK,
            CODEID,
            GEOLAT,
            GEOLONG, 
			VALELEV,
            elevationuom,
            TXTRMK,
            CODECHANNEL,
            VALCHANNEL,
            TXTNAME,
            GEOM
      FROM  DME, const
     WHERE  "overlaps"(const.effective_date, const.effective_date, DME.validfrom::timestamp with time zone, DME.validto::timestamp with time zone)
;

--------------------------------------------------
-- V_FIR
--------------------------------------------------
CREATE VIEW V_FIR (
    AIRSPACE_PK,
    TYP,
    IDENT,
    ICAOCODE,
    NAM,
    AREACODE,
    UPPERLIMIT,
    UPLIMITUNIT,
    LOWERLIMIT,
    LOWERLIMITUNIT,
    GEOM
) AS 
WITH const as (select navdb__get_effective_date() as effective_date)
    SELECT  AIRSPACE_PK,
            TYP,
            IDENT,
            ICAOCODE,
            NAM,
            AREACODE,
            UPPERLIMIT,
            UPLIMITUNIT,
            LOWERLIMIT,
            LOWERLIMITUNIT,
            GEOM
      FROM  airspace a, const
     WHERE  a.typ='FIR'
       AND  a.geom is not null
       and  "overlaps"(const.effective_date, const.effective_date,  a.validfrom::timestamp with time zone,  a.validto::timestamp with time zone)
;
	
--------------------------------------------------
-- V_NAVAIDS
--------------------------------------------------
CREATE VIEW V_NAVAIDS (
    NAVAIDS_PK,
    NAME,
    LATITUDE,
    LONGITUDE,
    DESIGNATOR,
    TYPE,
    GEOM
) AS
WITH const as (select navdb__get_effective_date() as effective_date)
    SELECT  NAVAIDS_PK,
            NAME,
            LATITUDE,
            LONGITUDE,
            DESIGNATOR,
            TYPE,
            GEOM
      FROM  NAVAIDS, const
     WHERE  "overlaps"(const.effective_date, const.effective_date, NAVAIDS.validfrom::timestamp with time zone, NAVAIDS.validto::timestamp with time zone)
;
  
--------------------------------------------------
-- V_NDB
--------------------------------------------------
CREATE VIEW V_NDB (
    NDB_PK,
    CODEID,
    GEOLAT,
    GEOLONG,
    CODECLASS,
    VALFREQ,
    UOMFREQ,
    VALMAGVAR,
    VALELEV,
    UOMDISTVER,
    TXTRMK,
    TXTNAME,
    GEOM
) AS
WITH const as (select navdb__get_effective_date() as effective_date)
    SELECT  NDB_PK,
            CODEID,
            GEOLAT,
            GEOLONG,
            CODECLASS,
            VALFREQ,
            UOMFREQ,
            VALMAGVAR,
            VALELEV,
            elevationuom,
            TXTRMK,
            TXTNAME,
            GEOM
      FROM  NDB, const
     WHERE  "overlaps"(const.effective_date, const.effective_date, NDB.validfrom::timestamp with time zone, NDB.validto::timestamp with time zone)
;

--------------------------------------------------
-- V_OBSAREA
--------------------------------------------------
CREATE VIEW V_OBSAREA (
    OBSAREA_PK,
    TYPE,
    AIRPORT_PK,
    RWYDIRECTION_PK,
    GEOM
) AS
WITH const as (select navdb__get_effective_date() as effective_date)
    SELECT  OBSAREA_PK,
            TYPE,
            AIRPORT_PK,
            RWYDIRECTION_PK,
            GEOM
      FROM  OBSAREA, const
     WHERE  "overlaps"(const.effective_date, const.effective_date, OBSAREA.validfrom::timestamp with time zone, OBSAREA.validto::timestamp with time zone)
;
  
--------------------------------------------------
-- V_OBSTACLE
--------------------------------------------------
CREATE VIEW V_OBSTACLE (
    OBSTACLE_PK,
    TYP,
    LATITUDE,
    LONGITUDE,
    LIGHTED,
    DESCRIPT,
    LOCATION,
    ELEVATION,
    IDENT,
    DESCRMRK,
    MULTIOCCUR,
    GEOM
) AS 
WITH const as (select navdb__get_effective_date() as effective_date)
    SELECT  o.OBSTACLE_PK,
            o.TYP,
            o.LATITUDE,
            o.LONGITUDE,
            o.LIGHTED,
            o.DESCRIPT,
            o.LOCATION,
            op.ELEVATION,
            o.IDENT,
            o.DESCRMRK,
            o.MULTIOCCUR,
            o.GEOM 
      FROM  OBSTACLE o, obstaclepart op, const
     WHERE  o.obstacle_pk = op.obstacle_pk
       and  "overlaps"(const.effective_date, const.effective_date,  o.validfrom::timestamp with time zone,  o.validto::timestamp with time zone)
       and  "overlaps"(const.effective_date, const.effective_date,  op.validfrom::timestamp with time zone,  op.validto::timestamp with time zone)
;

--------------------------------------------------
-- V_PUBLISHED_ATS
--------------------------------------------------
CREATE VIEW V_PUBLISHED_ATS (
    AIRSPACE_PK,
    TYP,
    IDENT,
    CODECLASS,
    ICAOCODE,
    NAM,
    AREACODE,
    UPPERLIMIT,
    UPLIMITUNIT,
    ABSUPPERLIMIT,
    LOWERLIMIT,
    LOWERLIMITUNIT,
    ABSLOWERLIMIT,
    CODEACTIVITY,
    GEOM
) AS
    SELECT  a.AIRSPACE_PK,
            a.TYP,
            a.IDENT,
            a.CODECLASS,
            a.ICAOCODE,
            a.NAM,
            a.AREACODE,
            a.UPPERLIMIT,
            a.UPLIMITUNIT,
            a.ABSUPPERLIMIT,
            a.LOWERLIMIT,
            a.LOWERLIMITUNIT,
            a.ABSLOWERLIMIT,
            a.CODEACTIVITY,
            a.GEOM
    FROM    V_AIRSPACE a
   WHERE    a.typ IN(
                'FIR_P',
                'UIR',
                'UIR_P',
                'CTA',
                'CTA_P',
                'OCA',
                'OCA_P',
                'UTA',
                'UTA_P',
                'TMA',
                'TMA_P', 
                'CTR',
                'CTR_P',
                'OTA',
                'SECTOR',
                'SECTOR_C',
                'RAS',
                'ADIZ',
                'CLASS',
                'ADV',
                'UADV',
                'ATZ',
                'ATZ_P',
                'HTZ',
                'OTHER:TIA',
                'OTHER:TIZ',
                'OTHER:FIZ'
            )
;


--------------------------------------------------
-- V_RWYDIRECTION
--------------------------------------------------
CREATE  VIEW V_RWYDIRECTION (
    RWYDIRECTION_PK,
    RUNWAY_PK,
    RWYENDID,
    GEOM
) AS
WITH const as (select navdb__get_effective_date() as effective_date)
    SELECT  RWYDIRECTION_PK,
            RUNWAY_PK,
            RWYENDID,
            GEOM
    FROM    RWYDIRECTION, const
    WHERE  "overlaps"(const.effective_date, const.effective_date,RWYDIRECTION.validfrom::timestamp with time zone, RWYDIRECTION.validto::timestamp with time zone)
;
	 

--------------------------------------------------
-- V_SM_FEA
--------------------------------------------------
CREATE VIEW V_SM_FEA (
    FEA_PK,
    FEA_NAME,
    DB_NAME
) AS
    SELECT  FEA_PK,
            FEA_NAME,
            DB_NAME
     FROM   SM_FEA
;
  
--------------------------------------------------
-- V_SPECIAL_ACT_AREA
--------------------------------------------------
CREATE VIEW V_SPECIAL_ACT_AREA (
    AIRSPACE_PK,
    TYP,
    IDENT,
    CODECLASS,
    ICAOCODE,
    NAM,
    AREACODE,
    UPPERLIMIT,
    UPLIMITUNIT,
    ABSUPPERLIMIT,
    LOWERLIMIT,
    LOWERLIMITUNIT,
    ABSLOWERLIMIT,
    CODEACTIVITY,
    GEOM
) AS
    SELECT  a.AIRSPACE_PK,
            a.TYP,
            a.IDENT,
            a.CODECLASS,
		    a.ICAOCODE,
            a.NAM,
            a.AREACODE,
            a.UPPERLIMIT,
            a.UPLIMITUNIT,
            a.ABSUPPERLIMIT,
            a.LOWERLIMIT,
            a.LOWERLIMITUNIT,
            a.ABSLOWERLIMIT,
            a.CODEACTIVITY,
            a.GEOM
    FROM    V_AIRSPACE a
   WHERE    a.typ IN ('TSA','P','D','R','TRA','D_OTHER','W','PROTECT','A','OTHER')
;

--------------------------------------------------
-- V_TACAN
--------------------------------------------------
CREATE VIEW V_TACAN (
    TACAN_PK,
    CODEID,
    GEOLAT,
    GEOLONG,
    VALCHANNEL,
    CODECHANNEL,
    VALMAGVAR,
    VALELEV,
    UOMDISTVER,
    TXTRMK,
    TXTNAME,
    GEOM
) AS
WITH const as (select navdb__get_effective_date() as effective_date)
    SELECT  TACAN_PK,
            CODEID,
            GEOLAT,
            GEOLONG,
            VALCHANNEL,
            CODECHANNEL,
            VALMAGVAR,
            VALELEV,
            elevationuom,
            TXTRMK,
            TXTNAME,
            GEOM
    FROM    tacan, const
    WHERE  "overlaps"(const.effective_date, const.effective_date,tacan.validfrom::timestamp with time zone, tacan.validto::timestamp with time zone)
;
	
--------------------------------------------------
-- V_VOR
--------------------------------------------------

CREATE VIEW V_VOR (
    VOR_PK,
    IDENT,
    LATITUDE,
    LONGITUDE,
    MAGVARIATION,
    ELEVATION,
    UOMDISTVER,
    FREQUENCY,
    FREQUNITS,
    TXTRMK,
    TXTNAME,
    GEOM
) AS
WITH const as (select navdb__get_effective_date() as effective_date)
    SELECT  VOR_PK,
            IDENT,
            LATITUDE,
            LONGITUDE,
            MAGVARIATION,
            ELEVATION,
            elevationuom,
            FREQUENCY,
            FREQUNITS,
            TXTRMK,
            TXTNAME,
            GEOM
    FROM    VOR, const
    WHERE   "overlaps"(const.effective_date, const.effective_date, VOR.validfrom::timestamp with time zone, VOR.validto::timestamp with time zone)
;
	

--------------------------------------------------
-- V_WAYPOINT
--------------------------------------------------
CREATE VIEW V_WAYPOINT (
    WAYPOINT_PK,
    IDENT,
    LATITUDE,
    LONGITUDE,
    GEOM
) AS
WITH const as (select navdb__get_effective_date() as effective_date)
    SELECT  WAYPOINT_PK,
            IDENT,
            LATITUDE,
            LONGITUDE,
            GEOM
    FROM    WAYPOINT, const
    WHERE   "overlaps"(const.effective_date, const.effective_date, WAYPOINT.validfrom::timestamp with time zone, WAYPOINT.validto::timestamp with time zone)
;

--------------------------------------------------
-- V_RUNWAY
--------------------------------------------------
CREATE VIEW V_RUNWAY (
    RUNWAY_PK,
    AIRPORT_PK,
    IDENT,
    HORIZUNITS,
	RUNWAYLENGTH,
	WIDTH,
    GEOM
) AS
WITH const as (select navdb__get_effective_date() as effective_date)
    SELECT  RUNWAY_PK,
			AIRPORT_PK,
			IDENT,
			NOMINALLENGTHUOM,
			RUNWAYLENGTH,
			WIDTH,
			GEOM
    FROM    RUNWAY, const
    WHERE   "overlaps"(const.effective_date, const.effective_date, RUNWAY.validfrom::timestamp with time zone, RUNWAY.validto::timestamp with time zone)
;

--------------------------------------------------
-- V_TAXIWAY
--------------------------------------------------
CREATE VIEW V_TWY (
    TWY_PK,
    AIRPORT_PK,
    TXTDESIG,
    CODETYPE,
	VALWID,
	UOMWID,
    GEOM
) AS
WITH const as (select navdb__get_effective_date() as effective_date)
    SELECT  TWY_PK,
			AIRPORT_PK,
			TXTDESIG,
			CODETYPE,
			VALWID,
			UOMWID,
			ST_GeomFromText('MULTIPOLYGON EMPTY')
    FROM    TWY, const
    WHERE   "overlaps"(const.effective_date, const.effective_date, TWY.validfrom::timestamp with time zone, TWY.validto::timestamp with time zone)
;

--------------------------------------------------
-- V_FIXES
--------------------------------------------------
CREATE OR REPLACE VIEW v_fixes
AS
WITH const as (select navdb__get_effective_date() as effective_date)
    SELECT  sf.fea_pk,
            sf.doc_name || ', ' || ident AS IDENT,
            waypoint_pk AS OCC_PK
      FROM  waypoint, sm_fea sf, const
     WHERE  sf.db_name = 'waypoint'
       AND  "overlaps"(const.effective_date, const.effective_date, validfrom::timestamp with time zone, validto::timestamp with time zone)
UNION
    SELECT  sf.fea_pk,
            sf.doc_name || ', ' || codeid,
            tacan_pk
      FROM  tacan, sm_fea sf, const
     WHERE  sf.db_name = 'tacan'
       AND  "overlaps"(const.effective_date, const.effective_date, validfrom::timestamp with time zone, validto::timestamp with time zone)
UNION
    SELECT  sf.fea_pk,
            sf.doc_name || ', ' || ident,
            vor_pk
      FROM  vor, sm_fea sf, const
     WHERE  sf.db_name = 'vor'
       AND  "overlaps"(const.effective_date, const.effective_date, validfrom::timestamp with time zone, validto::timestamp with time zone)
UNION
    SELECT  sf.fea_pk,
            sf.doc_name || ', ' || codeid,
            ndb_pk
      FROM  ndb, sm_fea sf, const
     WHERE  sf.db_name = 'ndb'
       AND  "overlaps"(const.effective_date, const.effective_date, validfrom::timestamp with time zone, validto::timestamp with time zone)
UNION
    SELECT  sf.fea_pk,
            sf.doc_name || ', ' || designator,
            navaids_pk
      FROM  navaids, sm_fea sf, const
     WHERE  sf.db_name = 'navaids'
       AND  "overlaps"(const.effective_date, const.effective_date, validfrom::timestamp with time zone, validto::timestamp with time zone)
UNION
    SELECT  sf.fea_pk,
            sf.doc_name || ', ' || codeid,
            marker_pk
      FROM  marker, sm_fea sf, const
     WHERE  sf.db_name = 'marker'
       AND  "overlaps"(const.effective_date, const.effective_date, validfrom::timestamp with time zone, validto::timestamp with time zone)
UNION
    SELECT  sf.fea_pk,
            sf.doc_name || ', ' || ident,
            airport_pk
      FROM  airport, sm_fea sf, const
     WHERE  sf.db_name = 'airport'
       AND "overlaps"(const.effective_date, const.effective_date, validfrom::timestamp with time zone, validto::timestamp with time zone)
UNION
    SELECT  sf.fea_pk,
            sf.doc_name || ', ' || designator,
            rwyclinept_pk
      FROM  rwyclinept, sm_fea sf, const
     WHERE  sf.db_name = 'rwyclinept'
       AND  "overlaps"(const.effective_date, const.effective_date, validfrom::timestamp with time zone, validto::timestamp with time zone)
UNION
    SELECT  sf.fea_pk, sf.doc_name || ', ' || rwyendid,
            rwydirection_pk
      FROM  rwydirection, sm_fea sf, const
     WHERE  sf.db_name = 'rwydirection'
       AND  "overlaps"(const.effective_date, const.effective_date, validfrom::timestamp with time zone, validto::timestamp with time zone)
UNION
    SELECT  sf.fea_pk,
            sf.doc_name || ', ' || codeid,
            dme_pk
      FROM  dme, sm_fea sf, const
     WHERE  sf.db_name = 'dme'
       AND  "overlaps"(const.effective_date, const.effective_date, validfrom::timestamp with time zone, validto::timestamp with time zone)
;


--------------------------------------------------
-- V_AIRWAY_SEG
--------------------------------------------------
CREATE OR REPLACE VIEW V_AIRWAY_SEG (
    AIRWAYSEG_PK,
    AIRWAY_PK,
    AIRWAY,
    LOWERLIMIT,
    UOMLOWERLIMIT,
    UOMUPPERLIMIT,
    UPPERLIMIT,
    LEVL,
    WIDTH,
    UOMWIDTH,
    ROUTEDIS,
    UOMDIST,
    CODETYPE,
    SEQ,
    FROMFIXFEA_PK,
    FROMFIX_PK,
    FROMFIX,
    TOFIXFEA_PK,
    TOFIX_PK,
    TOFIX,
    DIRECTION,
    AVBL,
    FROM_FIX_TXT,
    TO_FIX_TXT,
    GEOM
) AS
    WITH const as (select navdb__get_effective_date() as effective_date)
    SELECT  A.AIRWAYSEG_PK,
            A.AIRWAY_PK,
            A.txtdesig AS AIRWAY,
            A.LOWERLIMIT,
            A.UOMLOWERLIMIT,
            A.UOMUPPERLIMIT,
            A.UPPERLIMIT,
            coalesce(A.LEVL, 'BOTH'),
            A.WIDTH,
            A.UOMWIDTH,
            A.ROUTEDIS,
            A.UOMDIST,
            A.CODETYPE,
            A.SEQ,
            A.FROMFIXFEA_PK,
            A.FROMFIX_PK,
            A.FROMFIX,
            A.TOFIXFEA_PK,
            A.TOFIX_PK,
            A.TOFIX,
            A.DIRECTION,
            A.AVBL,
            FROMFIX_IDENT,
            TOFIX_IDENT,
            B.GEOM
    FROM (
        SELECT  DISTINCT aseg.airwayseg_pk,
                aseg.airway_pk,
                ar.txtdesig,
                aseg.lowerlimit,
                aseg.uomlowerlimit,
                aseg.uomupperlimit,
                aseg.upperlimit,
                aseg.levl,
                aseg.widthleft + aseg.widthright as width,
                aseg.widthleftuom as UOMWIDTH,
                aseg.routedis,
                aseg.uomdist,
                aseg.codetype,
                aseg.seq,
                s.fromfixfea_pk,
                s.fromfix_pk,
                (s.fromfixfea_pk::varchar || '_' || s.fromfix_pk::varchar) fromfix,
                s.tofixfea_pk,
                s.tofix_pk,
                (s.tofixfea_pk::varchar || '_' || s.tofix_pk::varchar) tofix,
                coalesce((string_agg (DISTINCT usg.codedir, ',')), 'BACKWARD,FORWARD') direction,
                coalesce((string_agg (DISTINCT usg.CODERTEAVBL, ','))::varchar, 'OPEN') avbl,
                fromFix.IDENT AS FROMFIX_IDENT,
                toFix.IDENT  AS TOFIX_IDENT
         FROM   airwayseg aseg,
                segmnt s,
                airway ar,
                rteseguse usg,
                v_fixes fromFix,
                v_fixes toFix,
                const
         WHERE  aseg.segmnt_pk = s.segmnt_pk
           AND  aseg.airway_pk = ar.airway_pk
           AND  aseg.airwayseg_pk = usg.airwayseg_pk
           AND  aseg.geom IS NOT NULL
           AND  s.fromfixfea_pk = fromFix.fea_pk
           AND  s.tofixfea_pk = toFix.fea_pk
           AND  s.fromfix_pk = fromFix.occ_pk
           AND  s.tofix_pk = toFix.occ_pk
           AND  "overlaps"(const.effective_date, const.effective_date, aseg.validfrom::timestamp with time zone, aseg.validto::timestamp with time zone)
           AND  "overlaps"(const.effective_date, const.effective_date,  s.validfrom::timestamp with time zone,  s.validto::timestamp with time zone)
           AND  "overlaps"(const.effective_date, const.effective_date,  ar.validfrom::timestamp with time zone, ar.validto::timestamp with time zone)
           AND  "overlaps"(const.effective_date, const.effective_date, usg.validfrom::timestamp with time zone, usg.validto::timestamp with time zone)
         --AND usg.CODERTEAVBL  IS NOT NULL
      GROUP BY  aseg.airwayseg_pk,
                aseg.airway_pk,
                ar.txtdesig,
                aseg.lowerlimit,
                aseg.uomlowerlimit,
                aseg.uomupperlimit,
                aseg.upperlimit,
                aseg.levl,
                aseg.widthleft + aseg.widthright,
                aseg.widthleftuom,
                aseg.routedis,
                aseg.uomdist,
                aseg.codetype,
                aseg.seq,
                s.fromfixfea_pk,
                s.fromfix_pk,
                (s.fromfixfea_pk::varchar || '_' || s.fromfix_pk::varchar),
                s.tofixfea_pk,
                s.tofix_pk,
                (s.tofixfea_pk::varchar || '_' || s.tofix_pk::varchar),
                fromFix.IDENT,
                toFix.IDENT
     ORDER BY   aseg.seq
    ) A,
    airwayseg B,
    const
  WHERE A.airwayseg_pk = B.airwayseg_pk
   and "overlaps"(const.effective_date, const.effective_date, B.validfrom::timestamp with time zone, B.validto::timestamp with time zone)
;


--------------------------------------------------
-- aixm_airportheliport_mapserv_v
--------------------------------------------------
CREATE VIEW aixm_airportheliport_mapserv_v AS
WITH const as (select navdb__get_effective_date() as effective_date)
    SELECT  'AirportHeliport'::character varying AS feature_class_id,
            airport.typ AS feature_type,
            airport.airport_pk AS feature_id,
            ''::character varying AS feature_gml_identifier_codespace,
            'AHP'::text || airport.airport_pk AS feature_gml_identifier,
            airport.nam AS name,
            airport.ident AS designator,
            airport.geom AS geo_shape
   FROM     airport, const
   WHERE    "overlaps"(const.effective_date, const.effective_date, airport.validfrom::timestamp with time zone, airport.validto::timestamp with time zone)
;


--------------------------------------------------
-- aixm_airspace_mapserv_v
--------------------------------------------------
CREATE OR REPLACE VIEW aixm_airspace_mapserv_v AS
WITH const as (select navdb__get_effective_date() as effective_date)
    SELECT  'Airspace'::character varying AS feature_class_id,
            airspace.typ AS feature_type,
            airspace.airspace_pk AS feature_id,
            ''::character varying AS feature_gml_identifier_codespace,
            'ASE'::text || airspace.airspace_pk AS feature_gml_identifier,
            airspace.nam AS name,
            airspace.ident AS designator,
            airspace.geom AS geo_shape
     FROM   airspace, const
    WHERE   airspace.geom IS NOT NULL
      AND   "overlaps"(const.effective_date, const.effective_date, airspace.validfrom::timestamp with time zone, airspace.validto::timestamp with time zone)
;

--------------------------------------------------
-- aixm_designatedpoint_mapserv_v
--------------------------------------------------
CREATE VIEW aixm_designatedpoint_mapserv_v AS
WITH const as (select navdb__get_effective_date() as effective_date)
    SELECT  'DesignatedPoint'::character varying AS feature_class_id,
            waypoint.codetype AS feature_type,
            waypoint.waypoint_pk AS feature_id,
            ''::character varying AS feature_gml_identifier_codespace,
            'DPN'::text || waypoint.waypoint_pk AS feature_gml_identifier,
            waypoint.nam AS name,
            waypoint.ident AS designator,
            waypoint.geom AS geo_shape
      FROM  waypoint, const
     WHERE  "overlaps"(const.effective_date, const.effective_date, waypoint.validfrom::timestamp with time zone, waypoint.validto::timestamp with time zone)
;

--------------------------------------------------
-- aixm_dme_mapserv_v
--------------------------------------------------
CREATE VIEW aixm_dme_mapserv_v AS
WITH const as (select navdb__get_effective_date() as effective_date)
    SELECT  'DME'::character varying AS feature_class_id,
            ''::character varying AS feature_type,
            dme.dme_pk AS feature_id,
            ''::character varying AS feature_gml_identifier_codespace,
            'DME'::text || dme.dme_pk AS feature_gml_identifier,
            dme.txtname AS name,
            dme.codeid AS designator,
            dme.geom AS geo_shape
      FROM  dme, const
     WHERE  "overlaps"(const.effective_date, const.effective_date, dme.validfrom::timestamp with time zone, dme.validto::timestamp with time zone)
;

--------------------------------------------------
-- aixm_fir_mapserv_v
--------------------------------------------------
CREATE VIEW aixm_fir_mapserv_v AS
WITH const as (select navdb__get_effective_date() as effective_date)
    SELECT  'Airspace'::character varying AS feature_class_id,
            airspace.typ AS feature_type,
            airspace.airspace_pk AS feature_id,
            ''::character varying AS feature_gml_identifier_codespace,
            'ASE'::text || airspace.airspace_pk AS feature_gml_identifier,
            airspace.nam AS name,
            airspace.ident AS designator,
            airspace.geom AS geo_shape
      FROM  airspace, const
      WHERE airspace.geom IS NOT NULL
        AND airspace.typ::text = 'FIR'::text
        AND "overlaps"(const.effective_date, const.effective_date, airspace.validfrom::timestamp with time zone, airspace.validto::timestamp with time zone)
;

--------------------------------------------------
-- aixm_markerbeacon_mapserv_v
--------------------------------------------------
CREATE VIEW aixm_markerbeacon_mapserv_v AS
WITH const as (select navdb__get_effective_date() as effective_date)
    SELECT  'MarkerBeacon'::character varying AS feature_class_id,
            ''::character varying AS feature_type,
            marker.marker_pk AS feature_id,
            ''::character varying AS feature_gml_identifier_codespace,
            'MRK'::text || marker.marker_pk AS feature_gml_identifier,
            marker.txtname AS name,
            marker.codeid AS designator,
            marker.geom AS geo_shape
      FROM  marker, const
     WHERE  "overlaps"(const.effective_date, const.effective_date, marker.validfrom::timestamp with time zone, marker.validto::timestamp with time zone)
;

--------------------------------------------------
-- aixm_ndb_mapserv_v
--------------------------------------------------
CREATE VIEW aixm_ndb_mapserv_v AS
WITH const as (select navdb__get_effective_date() as effective_date)
    SELECT  'NDB'::character varying AS feature_class_id,
            ''::character varying AS feature_type,
            ndb.ndb_pk AS feature_id,
            ''::character varying AS feature_gml_identifier_codespace,
            'NDB'::text || ndb.ndb_pk AS feature_gml_identifier, ndb.txtname AS name,
            ndb.codeid AS designator,
            ndb.geom AS geo_shape
      FROM  ndb, const
     WHERE  "overlaps"(const.effective_date, const.effective_date, ndb.validfrom::timestamp with time zone, ndb.validto::timestamp with time zone)
;

--------------------------------------------------
-- aixm_route_mapserv_v
--------------------------------------------------
CREATE VIEW aixm_route_mapserv_v AS
WITH const as (select navdb__get_effective_date() as effective_date)
    SELECT  'Route'::character varying AS feature_class_id,
            ''::character varying AS feature_type,
            airway.airway_pk AS feature_id,
            ''::character varying AS feature_gml_identifier_codespace,
            'RTE'::text || airway.airway_pk AS feature_gml_identifier,
            airway.txtlocdesig AS name,
            airway.txtdesig AS designator,
            airway.geom AS geo_shape
      FROM  airway, const
     WHERE  airway.geom IS NOT NULL
       AND  "overlaps"(const.effective_date, const.effective_date, airway.validfrom::timestamp with time zone, airway.validto::timestamp with time zone)
;

--------------------------------------------------
-- aixm_tacan_mapserv_v
--------------------------------------------------
CREATE VIEW aixm_tacan_mapserv_v AS
WITH const as (select navdb__get_effective_date() as effective_date)
    SELECT  'TACAN'::character varying AS feature_class_id,
            ''::character varying AS feature_type,
            tacan.tacan_pk AS feature_id,
            ''::character varying AS feature_gml_identifier_codespace,
            'TCN'::text || tacan.tacan_pk AS feature_gml_identifier,
            tacan.txtname AS name,
            tacan.codeid AS designator,
            tacan.geom AS geo_shape
      FROM  tacan, const
     WHERE  "overlaps"(const.effective_date, const.effective_date, tacan.validfrom::timestamp with time zone, tacan.validto::timestamp with time zone)
;

--------------------------------------------------
-- aixm_tma_mapserv_v
--------------------------------------------------
CREATE VIEW aixm_tma_mapserv_v AS
WITH const as (select navdb__get_effective_date() as effective_date)
    SELECT  'Airspace'::character varying AS feature_class_id,
            airspace.typ AS feature_type,
            airspace.airspace_pk AS feature_id,
            ''::character varying AS feature_gml_identifier_codespace,
            'ASE'::text || airspace.airspace_pk AS feature_gml_identifier,
            airspace.nam AS name,
            airspace.ident AS designator,
            airspace.geom AS geo_shape, airspace.upperlimit AS upper_limit,
            airspace.uplimitunit AS uplimit_unit, airspace.lowerlimit AS lower_limit,
            airspace.lowerlimitunit AS lowerlimit_unit, airspace.typ
      FROM  airspace, const
     WHERE  airspace.geom IS NOT NULL
       AND  airspace.typ::text = 'TMA'::text
       AND  "overlaps"(const.effective_date, const.effective_date, airspace.validfrom::timestamp with time zone, airspace.validto::timestamp with time zone)
;

--------------------------------------------------
-- aixm_vor_mapserv_v
--------------------------------------------------
CREATE VIEW aixm_vor_mapserv_v AS
WITH const as (select navdb__get_effective_date() as effective_date)
    SELECT  'VOR'::character varying AS feature_class_id,
            vor.vortype AS feature_type,
            vor.vor_pk AS feature_id,
            ''::character varying AS feature_gml_identifier_codespace,
            'VOR'::text || vor.vor_pk AS feature_gml_identifier,
            vor.txtname AS name,
            vor.ident AS designator,
            vor.geom AS geo_shape
      FROM  navdb.vor, const
     WHERE  "overlaps"(const.effective_date, const.effective_date, vor.validfrom::timestamp with time zone, vor.validto::timestamp with time zone)
;

