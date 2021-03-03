-- vim:ts=2:sts=2:sw=2:et

-- include the local copy of config.sql
\i upgrade_config.sql

begin;

update navdb.runway set geom = (select ST_Multi(ST_SetSRID((geometry(ST_Transform(ST_Buffer(ST_Transform(geometry(B.points), _ST_BestSRID(B.points)), (width * (CASE COALESCE(nominalwidthuom, 'X') WHEN 'M' THEN 1 WHEN 'NM' THEN 1852 WHEN 'FT' THEN 0.3048 ELSE 0 END)), 'endcap=square'), 4326))), 4326)) FROM (select ST_MakeLine(geom) AS points from navdb.rwydirection rd where "overlaps"(navdb__get_effective_date(), navdb__get_effective_date(), validfrom::timestamp with time zone, validto::timestamp with time zone) AND rd.runway_pk = runway.runway_pk and rd.geom is not null group by rd.runway_pk having count(*) > 1) B )
		where width is not null and runway_pk in (select runway_pk from navdb.rwydirection where "overlaps"(navdb__get_effective_date(), navdb__get_effective_date(), validfrom::timestamp with time zone, validto::timestamp with time zone) AND geom is not null) AND "overlaps"(navdb__get_effective_date(), navdb__get_effective_date(), validfrom::timestamp with time zone, validto::timestamp with time zone);

-- set data model version
update navdb_common.navdb_admin set dm_version = :'navdb_version';

commit;

