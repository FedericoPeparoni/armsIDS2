-- vim:ts=4:sts=4:sw=4:et

set search_path to navdb_common,navdb,public,pg_catalog;

-- View: v_airway_seg

-- DROP VIEW v_airway_seg;

CREATE OR REPLACE VIEW v_airway_seg AS 
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
    b.geom
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

ALTER TABLE v_airway_seg
  OWNER TO navdb;
GRANT ALL ON TABLE v_airway_seg TO navdb;
GRANT SELECT ON TABLE v_airway_seg TO navdb_ro_role;
GRANT UPDATE, INSERT, DELETE ON TABLE v_airway_seg TO navdb_rw_role;

