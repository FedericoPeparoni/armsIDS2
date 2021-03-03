-- vim:ts=2:sts=2:sw=2:et

-- include the local copy of config.sql
\i upgrade_config.sql

begin;

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
    a.codedistverlower
   FROM navdb_staging.airspace a,
    const
  WHERE a.geom IS NOT NULL AND "overlaps"(const.effective_date, const.effective_date, a.validfrom::timestamp with time zone, a.validto::timestamp with time zone);

-- set data model version
update navdb_common.navdb_admin set dm_version = :'navdb_version';

commit;

