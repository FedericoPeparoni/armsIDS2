-- vim:ts=2:sts=2:sw=2:et

-- include the local copy of config.sql
\i upgrade_config.sql 

begin;

--adding uuid support for digital NOTAM

ALTER TABLE navdb.airport ADD COLUMN uuid character varying;
ALTER TABLE navdb.airspace ADD COLUMN uuid character varying;
ALTER TABLE navdb.airway ADD COLUMN uuid character varying;
ALTER TABLE navdb.dme ADD COLUMN uuid character varying;
ALTER TABLE navdb.ndb ADD COLUMN uuid character varying;
ALTER TABLE navdb.runway ADD COLUMN uuid character varying;
ALTER TABLE navdb.rwydirection ADD COLUMN uuid character varying;
ALTER TABLE navdb.tacan ADD COLUMN uuid character varying;
ALTER TABLE navdb.vor ADD COLUMN uuid character varying;
ALTER TABLE navdb.waypoint ADD COLUMN uuid character varying;
ALTER TABLE navdb.navaids ADD COLUMN uuid character varying;
ALTER TABLE navdb.marker ADD COLUMN uuid character varying;
ALTER TABLE navdb.airwayseg ADD COLUMN uuid character varying;

ALTER TABLE navdb_staging.airport ADD COLUMN uuid character varying;
ALTER TABLE navdb_staging.airspace ADD COLUMN uuid character varying;
ALTER TABLE navdb_staging.airway ADD COLUMN uuid character varying;
ALTER TABLE navdb_staging.dme ADD COLUMN uuid character varying;
ALTER TABLE navdb_staging.ndb ADD COLUMN uuid character varying;
ALTER TABLE navdb_staging.runway ADD COLUMN uuid character varying;
ALTER TABLE navdb_staging.rwydirection ADD COLUMN uuid character varying;
ALTER TABLE navdb_staging.tacan ADD COLUMN uuid character varying;
ALTER TABLE navdb_staging.vor ADD COLUMN uuid character varying;
ALTER TABLE navdb_staging.waypoint ADD COLUMN uuid character varying;
ALTER TABLE navdb_staging.navaids ADD COLUMN uuid character varying;
ALTER TABLE navdb_staging.marker ADD COLUMN uuid character varying;
ALTER TABLE navdb_staging.airwayseg ADD COLUMN uuid character varying;

CREATE OR REPLACE VIEW navdb.v_airport AS 
 WITH const AS (
         SELECT navdb__get_effective_date() AS effective_date
        )
 SELECT airport.airport_pk,
    airport.airspace_pk,
    airport.ident,
    airport.typ,
    airport.nam,
    airport.magvariation,
    airport.magvardate,
    airport.elevation,
    airport.codewrkhr,
    airport.fieldelevationuom AS uomdistver,
    airport.geom,
    airport.uuid
   FROM airport,
    const
  WHERE "overlaps"(const.effective_date, const.effective_date, airport.validfrom::timestamp with time zone, airport.validto::timestamp with time zone);

CREATE OR REPLACE VIEW navdb_staging.v_airport AS 
 WITH const AS (
         SELECT navdb__get_effective_date() AS effective_date
        )
 SELECT airport.airport_pk,
    airport.airspace_pk,
    airport.ident,
    airport.typ,
    airport.nam,
    airport.magvariation,
    airport.magvardate,
    airport.elevation,
    airport.codewrkhr,
    airport.fieldelevationuom AS uomdistver,
    airport.geom,
    airport.uuid
   FROM airport,
    const
  WHERE "overlaps"(const.effective_date, const.effective_date, airport.validfrom::timestamp with time zone, airport.validto::timestamp with time zone);

CREATE OR REPLACE VIEW navdb.v_airspace AS 
 WITH const AS (
         SELECT navdb__get_effective_date() AS effective_date
        )
 SELECT a.airspace_pk,
    a.typ,
    a.ident,
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
    a.geom,
    a.uuid
   FROM airspace a,
    const
  WHERE a.typ::text <> 'FIR'::text AND a.typ::text <> 'UIR'::text AND a.geom IS NOT NULL AND "overlaps"(const.effective_date, const.effective_date, a.validfrom::timestamp with time zone, a.validto::timestamp with time zone);

CREATE OR REPLACE VIEW navdb_staging.v_airspace AS 
 WITH const AS (
         SELECT navdb__get_effective_date() AS effective_date
        )
 SELECT a.airspace_pk,
    a.typ,
    a.ident,
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
    a.geom,
    a.uuid
   FROM airspace a,
    const
  WHERE a.typ::text <> 'FIR'::text AND a.typ::text <> 'UIR'::text AND a.geom IS NOT NULL AND "overlaps"(const.effective_date, const.effective_date, a.validfrom::timestamp with time zone, a.validto::timestamp with time zone);

CREATE OR REPLACE VIEW navdb.v_airspace_all AS 
 WITH const AS (
         SELECT navdb__get_effective_date() AS effective_date
        )
 SELECT a.airspace_pk,
    a.typ,
    a.ident,
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
    a.geom,
    a.codedistverup,
    a.codedistverlower,
    a.uuid
   FROM airspace a,
    const
  WHERE a.geom IS NOT NULL AND "overlaps"(const.effective_date, const.effective_date, a.validfrom::timestamp with time zone, a.validto::timestamp with time zone);

CREATE OR REPLACE VIEW navdb_staging.v_airspace_all AS 
 WITH const AS (
         SELECT navdb__get_effective_date() AS effective_date
        )
 SELECT a.airspace_pk,
    a.typ,
    a.ident,
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
    a.geom,
    a.codedistverup,
    a.codedistverlower,
    a.uuid
   FROM airspace a,
    const
  WHERE a.geom IS NOT NULL AND "overlaps"(const.effective_date, const.effective_date, a.validfrom::timestamp with time zone, a.validto::timestamp with time zone);

CREATE OR REPLACE VIEW navdb.v_airway AS 
 WITH const AS (
         SELECT navdb__get_effective_date() AS effective_date
        )
 SELECT airway.airway_pk,
    airway.txtdesig,
    airway.uuid
   FROM airway,
    const
  WHERE "overlaps"(const.effective_date, const.effective_date, airway.validfrom::timestamp with time zone, airway.validto::timestamp with time zone);

CREATE OR REPLACE VIEW navdb_staging.v_airway AS 
 WITH const AS (
         SELECT navdb__get_effective_date() AS effective_date
        )
 SELECT airway.airway_pk,
    airway.txtdesig,
    airway.uuid
   FROM airway,
    const
  WHERE "overlaps"(const.effective_date, const.effective_date, airway.validfrom::timestamp with time zone, airway.validto::timestamp with time zone);

CREATE OR REPLACE VIEW navdb.v_airway_seg AS 
 WITH const AS (
         SELECT navdb__get_effective_date() AS effective_date
        )
 SELECT a.airwayseg_pk,
    a.airway_pk,
    a.txtdesig AS airway,
    a.lowerlimit,
    a.uomlowerlimit,
    a.uomupperlimit,
    a.upperlimit,
    COALESCE(a.levl, 'BOTH'::character varying) AS levl,
    a.width,
    a.uomwidth,
    a.routedis,
    a.uomdist,
    a.codetype,
    a.seq,
    a.fromfixfea_pk,
    a.fromfix_pk,
    a.fromfix,
    a.tofixfea_pk,
    a.tofix_pk,
    a.tofix,
    a.direction,
    a.avbl,
    a.fromfix_ident AS from_fix_txt,
    a.tofix_ident AS to_fix_txt,
    b.geom,
    b.uuid
   FROM ( SELECT DISTINCT aseg.airwayseg_pk,
            aseg.airway_pk,
            ar.txtdesig,
            aseg.lowerlimit,
            aseg.uomlowerlimit,
            aseg.uomupperlimit,
            aseg.upperlimit,
            aseg.levl,
            aseg.widthleft + aseg.widthright AS width,
            aseg.widthleftuom AS uomwidth,
            aseg.routedis,
            aseg.uomdist,
            aseg.codetype,
            aseg.seq,
            s.fromfixfea_pk,
            s.fromfix_pk,
            (s.fromfixfea_pk::character varying::text || '_'::text) || s.fromfix_pk::character varying::text AS fromfix,
            s.tofixfea_pk,
            s.tofix_pk,
            (s.tofixfea_pk::character varying::text || '_'::text) || s.tofix_pk::character varying::text AS tofix,
            'BACKWARD,FORWARD'::text AS direction,
            'OPEN'::character varying AS avbl,
            fromfix.ident AS fromfix_ident,
            tofix.ident AS tofix_ident
           FROM airwayseg aseg,
            segmnt s,
            airway ar,
            v_fixes fromfix,
            v_fixes tofix,
            const const_1
          WHERE aseg.segmnt_pk = s.segmnt_pk AND aseg.airway_pk = ar.airway_pk AND aseg.geom IS NOT NULL AND s.fromfixfea_pk = fromfix.fea_pk::double precision AND s.tofixfea_pk = tofix.fea_pk::double precision AND s.fromfix_pk = fromfix.occ_pk AND s.tofix_pk = tofix.occ_pk AND "overlaps"(const_1.effective_date, const_1.effective_date, aseg.validfrom::timestamp with time zone, aseg.validto::timestamp with time zone) AND "overlaps"(const_1.effective_date, const_1.effective_date, s.validfrom::timestamp with time zone, s.validto::timestamp with time zone) AND "overlaps"(const_1.effective_date, const_1.effective_date, ar.validfrom::timestamp with time zone, ar.validto::timestamp with time zone)
          GROUP BY aseg.airwayseg_pk, aseg.airway_pk, ar.txtdesig, aseg.lowerlimit, aseg.uomlowerlimit, aseg.uomupperlimit, aseg.upperlimit, aseg.levl, aseg.widthleft + aseg.widthright, aseg.widthleftuom, aseg.routedis, aseg.uomdist, aseg.codetype, aseg.seq, s.fromfixfea_pk, s.fromfix_pk, (s.fromfixfea_pk::character varying::text || '_'::text) || s.fromfix_pk::character varying::text, s.tofixfea_pk, s.tofix_pk, (s.tofixfea_pk::character varying::text || '_'::text) || s.tofix_pk::character varying::text, fromfix.ident, tofix.ident
          ORDER BY aseg.seq) a,
    airwayseg b,
    const
  WHERE a.airwayseg_pk = b.airwayseg_pk AND "overlaps"(const.effective_date, const.effective_date, b.validfrom::timestamp with time zone, b.validto::timestamp with time zone);

CREATE OR REPLACE VIEW navdb_staging.v_airway_seg AS 
 WITH const AS (
         SELECT navdb__get_effective_date() AS effective_date
        )
 SELECT a.airwayseg_pk,
    a.airway_pk,
    a.txtdesig AS airway,
    a.lowerlimit,
    a.uomlowerlimit,
    a.uomupperlimit,
    a.upperlimit,
    COALESCE(a.levl, 'BOTH'::character varying) AS levl,
    a.width,
    a.uomwidth,
    a.routedis,
    a.uomdist,
    a.codetype,
    a.seq,
    a.fromfixfea_pk,
    a.fromfix_pk,
    a.fromfix,
    a.tofixfea_pk,
    a.tofix_pk,
    a.tofix,
    a.direction,
    a.avbl,
    a.fromfix_ident AS from_fix_txt,
    a.tofix_ident AS to_fix_txt,
    b.geom,
    b.uuid
   FROM ( SELECT DISTINCT aseg.airwayseg_pk,
            aseg.airway_pk,
            ar.txtdesig,
            aseg.lowerlimit,
            aseg.uomlowerlimit,
            aseg.uomupperlimit,
            aseg.upperlimit,
            aseg.levl,
            aseg.widthleft + aseg.widthright AS width,
            aseg.widthleftuom AS uomwidth,
            aseg.routedis,
            aseg.uomdist,
            aseg.codetype,
            aseg.seq,
            s.fromfixfea_pk,
            s.fromfix_pk,
            (s.fromfixfea_pk::character varying::text || '_'::text) || s.fromfix_pk::character varying::text AS fromfix,
            s.tofixfea_pk,
            s.tofix_pk,
            (s.tofixfea_pk::character varying::text || '_'::text) || s.tofix_pk::character varying::text AS tofix,
            'BACKWARD,FORWARD'::text AS direction,
            'OPEN'::character varying AS avbl,
            fromfix.ident AS fromfix_ident,
            tofix.ident AS tofix_ident
           FROM airwayseg aseg,
            segmnt s,
            airway ar,
            v_fixes fromfix,
            v_fixes tofix,
            const const_1
          WHERE aseg.segmnt_pk = s.segmnt_pk AND aseg.airway_pk = ar.airway_pk AND aseg.geom IS NOT NULL AND s.fromfixfea_pk = fromfix.fea_pk::double precision AND s.tofixfea_pk = tofix.fea_pk::double precision AND s.fromfix_pk = fromfix.occ_pk AND s.tofix_pk = tofix.occ_pk AND "overlaps"(const_1.effective_date, const_1.effective_date, aseg.validfrom::timestamp with time zone, aseg.validto::timestamp with time zone) AND "overlaps"(const_1.effective_date, const_1.effective_date, s.validfrom::timestamp with time zone, s.validto::timestamp with time zone) AND "overlaps"(const_1.effective_date, const_1.effective_date, ar.validfrom::timestamp with time zone, ar.validto::timestamp with time zone)
          GROUP BY aseg.airwayseg_pk, aseg.airway_pk, ar.txtdesig, aseg.lowerlimit, aseg.uomlowerlimit, aseg.uomupperlimit, aseg.upperlimit, aseg.levl, aseg.widthleft + aseg.widthright, aseg.widthleftuom, aseg.routedis, aseg.uomdist, aseg.codetype, aseg.seq, s.fromfixfea_pk, s.fromfix_pk, (s.fromfixfea_pk::character varying::text || '_'::text) || s.fromfix_pk::character varying::text, s.tofixfea_pk, s.tofix_pk, (s.tofixfea_pk::character varying::text || '_'::text) || s.tofix_pk::character varying::text, fromfix.ident, tofix.ident
          ORDER BY aseg.seq) a,
    airwayseg b,
    const
  WHERE a.airwayseg_pk = b.airwayseg_pk AND "overlaps"(const.effective_date, const.effective_date, b.validfrom::timestamp with time zone, b.validto::timestamp with time zone);

CREATE OR REPLACE VIEW navdb.v_dme AS 
 WITH const AS (
         SELECT navdb__get_effective_date() AS effective_date
        )
 SELECT dme.dme_pk,
    dme.codeid,
    dme.geolat,
    dme.geolong,
    dme.valelev,
    dme.elevationuom AS uomdistver,
    dme.txtrmk,
    dme.codechannel,
    dme.valchannel,
    dme.txtname,
    dme.geom,
    dme.uuid
   FROM dme,
    const
  WHERE "overlaps"(const.effective_date, const.effective_date, dme.validfrom::timestamp with time zone, dme.validto::timestamp with time zone);

CREATE OR REPLACE VIEW navdb_staging.v_dme AS 
 WITH const AS (
         SELECT navdb__get_effective_date() AS effective_date
        )
 SELECT dme.dme_pk,
    dme.codeid,
    dme.geolat,
    dme.geolong,
    dme.valelev,
    dme.elevationuom AS uomdistver,
    dme.txtrmk,
    dme.codechannel,
    dme.valchannel,
    dme.txtname,
    dme.geom,
    dme.uuid
   FROM dme,
    const
  WHERE "overlaps"(const.effective_date, const.effective_date, dme.validfrom::timestamp with time zone, dme.validto::timestamp with time zone);

CREATE OR REPLACE VIEW navdb.v_fir AS 
 WITH const AS (
         SELECT navdb__get_effective_date() AS effective_date
        )
 SELECT a.airspace_pk,
    a.typ,
    a.ident,
    a.icaocode,
    a.nam,
    a.areacode,
    a.upperlimit,
    a.uplimitunit,
    a.lowerlimit,
    a.lowerlimitunit,
    a.geom,
    a.uuid
   FROM airspace a,
    const
  WHERE a.typ::text = 'FIR'::text AND a.geom IS NOT NULL AND "overlaps"(const.effective_date, const.effective_date, a.validfrom::timestamp with time zone, a.validto::timestamp with time zone);

CREATE OR REPLACE VIEW navdb_staging.v_fir AS 
 WITH const AS (
         SELECT navdb__get_effective_date() AS effective_date
        )
 SELECT a.airspace_pk,
    a.typ,
    a.ident,
    a.icaocode,
    a.nam,
    a.areacode,
    a.upperlimit,
    a.uplimitunit,
    a.lowerlimit,
    a.lowerlimitunit,
    a.geom,
    a.uuid
   FROM airspace a,
    const
  WHERE a.typ::text = 'FIR'::text AND a.geom IS NOT NULL AND "overlaps"(const.effective_date, const.effective_date, a.validfrom::timestamp with time zone, a.validto::timestamp with time zone);

CREATE OR REPLACE VIEW navdb.v_ndb AS 
 WITH const AS (
         SELECT navdb__get_effective_date() AS effective_date
        )
 SELECT ndb.ndb_pk,
    ndb.codeid,
    ndb.geolat,
    ndb.geolong,
    ndb.codeclass,
    ndb.valfreq,
    ndb.uomfreq,
    ndb.valmagvar,
    ndb.valelev,
    ndb.elevationuom AS uomdistver,
    ndb.txtrmk,
    ndb.txtname,
    ndb.geom,
    ndb.uuid
   FROM ndb,
    const
  WHERE "overlaps"(const.effective_date, const.effective_date, ndb.validfrom::timestamp with time zone, ndb.validto::timestamp with time zone);

CREATE OR REPLACE VIEW navdb_staging.v_ndb AS 
 WITH const AS (
         SELECT navdb__get_effective_date() AS effective_date
        )
 SELECT ndb.ndb_pk,
    ndb.codeid,
    ndb.geolat,
    ndb.geolong,
    ndb.codeclass,
    ndb.valfreq,
    ndb.uomfreq,
    ndb.valmagvar,
    ndb.valelev,
    ndb.elevationuom AS uomdistver,
    ndb.txtrmk,
    ndb.txtname,
    ndb.geom,
    ndb.uuid
   FROM ndb,
    const
  WHERE "overlaps"(const.effective_date, const.effective_date, ndb.validfrom::timestamp with time zone, ndb.validto::timestamp with time zone);

CREATE OR REPLACE VIEW navdb.v_runway AS 
 WITH const AS (
         SELECT navdb__get_effective_date() AS effective_date
        )
 SELECT runway.runway_pk,
    runway.airport_pk,
    runway.ident,
    runway.nominallengthuom AS horizunits,
    runway.runwaylength,
    runway.width,
    runway.geom,
    runway.uuid
   FROM runway,
    const
  WHERE "overlaps"(const.effective_date, const.effective_date, runway.validfrom::timestamp with time zone, runway.validto::timestamp with time zone);

CREATE OR REPLACE VIEW navdb_staging.v_runway AS 
 WITH const AS (
         SELECT navdb__get_effective_date() AS effective_date
        )
 SELECT runway.runway_pk,
    runway.airport_pk,
    runway.ident,
    runway.nominallengthuom AS horizunits,
    runway.runwaylength,
    runway.width,
    runway.geom,
    runway.uuid
   FROM runway,
    const
  WHERE "overlaps"(const.effective_date, const.effective_date, runway.validfrom::timestamp with time zone, runway.validto::timestamp with time zone);

CREATE OR REPLACE VIEW navdb.v_rwydirection AS 
 WITH const AS (
         SELECT navdb__get_effective_date() AS effective_date
        )
 SELECT rwydirection.rwydirection_pk,
    rwydirection.runway_pk,
    rwydirection.rwyendid,
    rwydirection.geom,
    rwydirection.uuid
   FROM rwydirection,
    const
  WHERE "overlaps"(const.effective_date, const.effective_date, rwydirection.validfrom::timestamp with time zone, rwydirection.validto::timestamp with time zone);

CREATE OR REPLACE VIEW navdb_staging.v_rwydirection AS 
 WITH const AS (
         SELECT navdb__get_effective_date() AS effective_date
        )
 SELECT rwydirection.rwydirection_pk,
    rwydirection.runway_pk,
    rwydirection.rwyendid,
    rwydirection.geom,
    rwydirection.uuid
   FROM rwydirection,
    const
  WHERE "overlaps"(const.effective_date, const.effective_date, rwydirection.validfrom::timestamp with time zone, rwydirection.validto::timestamp with time zone);

CREATE OR REPLACE VIEW navdb.v_tacan AS 
 WITH const AS (
         SELECT navdb__get_effective_date() AS effective_date
        )
 SELECT tacan.tacan_pk,
    tacan.codeid,
    tacan.geolat,
    tacan.geolong,
    tacan.valchannel,
    tacan.codechannel,
    tacan.valmagvar,
    tacan.valelev,
    tacan.elevationuom AS uomdistver,
    tacan.txtrmk,
    tacan.txtname,
    tacan.geom,
    tacan.uuid
   FROM tacan,
    const
  WHERE "overlaps"(const.effective_date, const.effective_date, tacan.validfrom::timestamp with time zone, tacan.validto::timestamp with time zone);


CREATE OR REPLACE VIEW navdb_staging.v_tacan AS 
 WITH const AS (
         SELECT navdb__get_effective_date() AS effective_date
        )
 SELECT tacan.tacan_pk,
    tacan.codeid,
    tacan.geolat,
    tacan.geolong,
    tacan.valchannel,
    tacan.codechannel,
    tacan.valmagvar,
    tacan.valelev,
    tacan.elevationuom AS uomdistver,
    tacan.txtrmk,
    tacan.txtname,
    tacan.geom,
    tacan.uuid
   FROM tacan,
    const
  WHERE "overlaps"(const.effective_date, const.effective_date, tacan.validfrom::timestamp with time zone, tacan.validto::timestamp with time zone);

CREATE OR REPLACE VIEW navdb.v_vor AS 
 WITH const AS (
         SELECT navdb__get_effective_date() AS effective_date
        )
 SELECT vor.vor_pk,
    vor.ident,
    vor.latitude,
    vor.longitude,
    vor.magvariation,
    vor.elevation,
    vor.elevationuom AS uomdistver,
    vor.frequency,
    vor.frequnits,
    vor.txtrmk,
    vor.txtname,
    vor.geom,
    vor.uuid
   FROM vor,
    const
  WHERE "overlaps"(const.effective_date, const.effective_date, vor.validfrom::timestamp with time zone, vor.validto::timestamp with time zone);

CREATE OR REPLACE VIEW navdb_staging.v_vor AS 
 WITH const AS (
         SELECT navdb__get_effective_date() AS effective_date
        )
 SELECT vor.vor_pk,
    vor.ident,
    vor.latitude,
    vor.longitude,
    vor.magvariation,
    vor.elevation,
    vor.elevationuom AS uomdistver,
    vor.frequency,
    vor.frequnits,
    vor.txtrmk,
    vor.txtname,
    vor.geom,
    vor.uuid
   FROM vor,
    const
  WHERE "overlaps"(const.effective_date, const.effective_date, vor.validfrom::timestamp with time zone, vor.validto::timestamp with time zone);

CREATE OR REPLACE VIEW navdb.v_waypoint AS 
 WITH const AS (
         SELECT navdb__get_effective_date() AS effective_date
        )
 SELECT waypoint.waypoint_pk,
    waypoint.ident,
    waypoint.latitude,
    waypoint.longitude,
    waypoint.geom,
    waypoint.uuid
   FROM waypoint,
    const
  WHERE "overlaps"(const.effective_date, const.effective_date, waypoint.validfrom::timestamp with time zone, waypoint.validto::timestamp with time zone);

CREATE OR REPLACE VIEW navdb_staging.v_waypoint AS 
 WITH const AS (
         SELECT navdb__get_effective_date() AS effective_date
        )
 SELECT waypoint.waypoint_pk,
    waypoint.ident,
    waypoint.latitude,
    waypoint.longitude,
    waypoint.geom,
    waypoint.uuid
   FROM waypoint,
    const
  WHERE "overlaps"(const.effective_date, const.effective_date, waypoint.validfrom::timestamp with time zone, waypoint.validto::timestamp with time zone);

CREATE OR REPLACE VIEW navdb.v_fixes AS 
 WITH const AS (
         SELECT navdb__get_effective_date() AS effective_date
        )
 SELECT sf.fea_pk,
    (sf.doc_name::text || ', '::text) || waypoint.ident::text AS ident,
    waypoint.waypoint_pk AS occ_pk,
    waypoint.uuid
   FROM waypoint,
    sm_fea sf,
    const
  WHERE sf.db_name::text = 'waypoint'::text AND "overlaps"(const.effective_date, const.effective_date, waypoint.validfrom::timestamp with time zone, waypoint.validto::timestamp with time zone)
UNION
 SELECT sf.fea_pk,
    (sf.doc_name::text || ', '::text) || tacan.codeid::text AS ident,
    tacan.tacan_pk AS occ_pk,
    tacan.uuid
   FROM tacan,
    sm_fea sf,
    const
  WHERE sf.db_name::text = 'tacan'::text AND "overlaps"(const.effective_date, const.effective_date, tacan.validfrom::timestamp with time zone, tacan.validto::timestamp with time zone)
UNION
 SELECT sf.fea_pk,
    (sf.doc_name::text || ', '::text) || vor.ident::text AS ident,
    vor.vor_pk AS occ_pk,
    vor.uuid
   FROM vor,
    sm_fea sf,
    const
  WHERE sf.db_name::text = 'vor'::text AND "overlaps"(const.effective_date, const.effective_date, vor.validfrom::timestamp with time zone, vor.validto::timestamp with time zone)
UNION
 SELECT sf.fea_pk,
    (sf.doc_name::text || ', '::text) || ndb.codeid::text AS ident,
    ndb.ndb_pk AS occ_pk,
    ndb.uuid
   FROM ndb,
    sm_fea sf,
    const
  WHERE sf.db_name::text = 'ndb'::text AND "overlaps"(const.effective_date, const.effective_date, ndb.validfrom::timestamp with time zone, ndb.validto::timestamp with time zone)
UNION
 SELECT sf.fea_pk,
    (sf.doc_name::text || ', '::text) || navaids.designator::text AS ident,
    navaids.navaids_pk AS occ_pk,
    navaids.uuid
   FROM navaids,
    sm_fea sf,
    const
  WHERE sf.db_name::text = 'navaids'::text AND "overlaps"(const.effective_date, const.effective_date, navaids.validfrom::timestamp with time zone, navaids.validto::timestamp with time zone)
UNION
 SELECT sf.fea_pk,
    (sf.doc_name::text || ', '::text) || marker.codeid::text AS ident,
    marker.marker_pk AS occ_pk,
    marker.uuid
   FROM marker,
    sm_fea sf,
    const
  WHERE sf.db_name::text = 'marker'::text AND "overlaps"(const.effective_date, const.effective_date, marker.validfrom::timestamp with time zone, marker.validto::timestamp with time zone)
UNION
 SELECT sf.fea_pk,
    (sf.doc_name::text || ', '::text) || airport.ident::text AS ident,
    airport.airport_pk AS occ_pk,
    airport.uuid
   FROM airport,
    sm_fea sf,
    const
  WHERE sf.db_name::text = 'airport'::text AND "overlaps"(const.effective_date, const.effective_date, airport.validfrom::timestamp with time zone, airport.validto::timestamp with time zone)
UNION
 SELECT sf.fea_pk,
    (sf.doc_name::text || ', '::text) || rwyclinept.designator::text AS ident,
    rwyclinept.rwyclinept_pk AS occ_pk,
    NULL::character varying AS uuid
   FROM rwyclinept,
    sm_fea sf,
    const
  WHERE sf.db_name::text = 'rwyclinept'::text AND "overlaps"(const.effective_date, const.effective_date, rwyclinept.validfrom::timestamp with time zone, rwyclinept.validto::timestamp with time zone)
UNION
 SELECT sf.fea_pk,
    (sf.doc_name::text || ', '::text) || rwydirection.rwyendid::text AS ident,
    rwydirection.rwydirection_pk AS occ_pk,
    rwydirection.uuid
   FROM rwydirection,
    sm_fea sf,
    const
  WHERE sf.db_name::text = 'rwydirection'::text AND "overlaps"(const.effective_date, const.effective_date, rwydirection.validfrom::timestamp with time zone, rwydirection.validto::timestamp with time zone)
UNION
 SELECT sf.fea_pk,
    (sf.doc_name::text || ', '::text) || dme.codeid::text AS ident,
    dme.dme_pk AS occ_pk,
    dme.uuid
   FROM dme,
    sm_fea sf,
    const
  WHERE sf.db_name::text = 'dme'::text AND "overlaps"(const.effective_date, const.effective_date, dme.validfrom::timestamp with time zone, dme.validto::timestamp with time zone);

CREATE OR REPLACE VIEW navdb_staging.v_fixes AS 
 WITH const AS (
         SELECT navdb__get_effective_date() AS effective_date
        )
 SELECT sf.fea_pk,
    (sf.doc_name::text || ', '::text) || waypoint.ident::text AS ident,
    waypoint.waypoint_pk AS occ_pk,
    waypoint.uuid
   FROM waypoint,
    sm_fea sf,
    const
  WHERE sf.db_name::text = 'waypoint'::text AND "overlaps"(const.effective_date, const.effective_date, waypoint.validfrom::timestamp with time zone, waypoint.validto::timestamp with time zone)
UNION
 SELECT sf.fea_pk,
    (sf.doc_name::text || ', '::text) || tacan.codeid::text AS ident,
    tacan.tacan_pk AS occ_pk,
    tacan.uuid
   FROM tacan,
    sm_fea sf,
    const
  WHERE sf.db_name::text = 'tacan'::text AND "overlaps"(const.effective_date, const.effective_date, tacan.validfrom::timestamp with time zone, tacan.validto::timestamp with time zone)
UNION
 SELECT sf.fea_pk,
    (sf.doc_name::text || ', '::text) || vor.ident::text AS ident,
    vor.vor_pk AS occ_pk,
    vor.uuid
   FROM vor,
    sm_fea sf,
    const
  WHERE sf.db_name::text = 'vor'::text AND "overlaps"(const.effective_date, const.effective_date, vor.validfrom::timestamp with time zone, vor.validto::timestamp with time zone)
UNION
 SELECT sf.fea_pk,
    (sf.doc_name::text || ', '::text) || ndb.codeid::text AS ident,
    ndb.ndb_pk AS occ_pk,
    ndb.uuid
   FROM ndb,
    sm_fea sf,
    const
  WHERE sf.db_name::text = 'ndb'::text AND "overlaps"(const.effective_date, const.effective_date, ndb.validfrom::timestamp with time zone, ndb.validto::timestamp with time zone)
UNION
 SELECT sf.fea_pk,
    (sf.doc_name::text || ', '::text) || navaids.designator::text AS ident,
    navaids.navaids_pk AS occ_pk,
    navaids.uuid
   FROM navaids,
    sm_fea sf,
    const
  WHERE sf.db_name::text = 'navaids'::text AND "overlaps"(const.effective_date, const.effective_date, navaids.validfrom::timestamp with time zone, navaids.validto::timestamp with time zone)
UNION
 SELECT sf.fea_pk,
    (sf.doc_name::text || ', '::text) || marker.codeid::text AS ident,
    marker.marker_pk AS occ_pk,
    marker.uuid
   FROM marker,
    sm_fea sf,
    const
  WHERE sf.db_name::text = 'marker'::text AND "overlaps"(const.effective_date, const.effective_date, marker.validfrom::timestamp with time zone, marker.validto::timestamp with time zone)
UNION
 SELECT sf.fea_pk,
    (sf.doc_name::text || ', '::text) || airport.ident::text AS ident,
    airport.airport_pk AS occ_pk,
    airport.uuid
   FROM airport,
    sm_fea sf,
    const
  WHERE sf.db_name::text = 'airport'::text AND "overlaps"(const.effective_date, const.effective_date, airport.validfrom::timestamp with time zone, airport.validto::timestamp with time zone)
UNION
 SELECT sf.fea_pk,
    (sf.doc_name::text || ', '::text) || rwyclinept.designator::text AS ident,
    rwyclinept.rwyclinept_pk AS occ_pk,
    NULL::character varying AS uuid
   FROM rwyclinept,
    sm_fea sf,
    const
  WHERE sf.db_name::text = 'rwyclinept'::text AND "overlaps"(const.effective_date, const.effective_date, rwyclinept.validfrom::timestamp with time zone, rwyclinept.validto::timestamp with time zone)
UNION
 SELECT sf.fea_pk,
    (sf.doc_name::text || ', '::text) || rwydirection.rwyendid::text AS ident,
    rwydirection.rwydirection_pk AS occ_pk,
    rwydirection.uuid
   FROM rwydirection,
    sm_fea sf,
    const
  WHERE sf.db_name::text = 'rwydirection'::text AND "overlaps"(const.effective_date, const.effective_date, rwydirection.validfrom::timestamp with time zone, rwydirection.validto::timestamp with time zone)
UNION
 SELECT sf.fea_pk,
    (sf.doc_name::text || ', '::text) || dme.codeid::text AS ident,
    dme.dme_pk AS occ_pk,
    dme.uuid
   FROM dme,
    sm_fea sf,
    const
  WHERE sf.db_name::text = 'dme'::text AND "overlaps"(const.effective_date, const.effective_date, dme.validfrom::timestamp with time zone, dme.validto::timestamp with time zone);


-- set data model version
update navdb_common.navdb_admin set dm_version = :'navdb_version'; 

commit;

