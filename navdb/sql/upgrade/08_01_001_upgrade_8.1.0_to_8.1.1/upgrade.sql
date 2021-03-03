-- vim:ts=2:sts=2:sw=2:et

-- include the local copy of config.sql
\i config.sql

begin;

--------------------------------------------------
-- V_AIRSPACE_ALL
--------------------------------------------------
CREATE OR REPLACE VIEW navdb_common.v_airspace_all (
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

-- set data model version
update navdb_common.navdb_admin set dm_version = :'navdb_version';

commit;

