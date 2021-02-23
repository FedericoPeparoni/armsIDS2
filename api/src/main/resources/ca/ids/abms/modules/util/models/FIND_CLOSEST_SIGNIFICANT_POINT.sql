/*
 * This query is used by NavDBUtils class to find a nearby significant point near a local airspace.
 *
 * PARAMETERS
 * =============
 *
 *   nameOrIdent           - (String) the identifier of the waypoint
 *   refPointWkt           - (String) reference point geometry WKT, typically the departure point of the flight
 *   localAirspaceWkt      - (String) geometry WKT that represents the local airspace(s)
 *   localAirspaceSegmentLengthMeters - (Integer) segmentize local_airspace_geom with this segment length
 *   distanceMeters        - (Integer) maximum distance between refPointWkt and localAirspaceWkt
 *
 * We will look for a point whose name matches "nameOrIdent", that is within "distanceMeters" of
 * any of the border points making up the "localAirspaceWkt". Among multiple matches we pick the one closest
 * to "refPointWkt". We segmentize localAirspaceWkt using geoid math before any calculations.
 *
 * Returns at most one row with the following columns:
 *
 *   type   - (String) the type of significant point found, such as "navaid"
 *   ident  - (String) the identifier, may be different from "name_or_ident" parameter
 *   geom   - (String) the geometry; normally a point; in WKT format
 *
 */

WITH
const AS (
    SELECT
        -- effective_date   time without time zone
        navdb__get_effective_date() AS effective_date,

        -- name_or_ident  text
        CAST (:nameOrIdent AS TEXT) AS name_or_ident,

        -- ref_point_geom  geometry
        ST_SetSRID (CAST (:refPointWkt AS GEOMETRY), 4326) AS ref_point_geom,

        -- border_tolerance_meters  integer
        CAST (:distanceMeters AS INTEGER) AS border_tolerance_meters,

        -- local_airspace_geom  geometry
        CAST (ST_Segmentize (CAST (:localAirspaceWkt AS GEOGRAPHY), CAST (:localAirspaceSegmentLengthMeters AS INTEGER)) AS GEOMETRY) AS local_airspace_geom
)
SELECT
    fix.type as type,
    fix.ident as ident,
    st_astext (fix.geom) as geom
FROM

    -- constants and parameters
    const,

    -- "fix"
    (
        -- This selects significant points, but (re-)constructs geometries from lat/long values if necessary.
        -- We need this because some records have the individual lat/long fields, but their "geom" fields
        -- are null.
        SELECT
            type,
            ident,
            CASE
                WHEN geom IS NULL AND lat IS NOT NULL AND lng IS NOT NULL THEN ST_SetSRID (ST_MakePoint (lng, lat), 4326)
                ELSE geom
            END as geom
        FROM (
            -- The following selects from different significant point tables, using only the most
            -- recent timeslice ("OVERLAPS ...") while filtering on point identifiers
            SELECT type, ident, lat, lng, geom
            FROM const,
            (
                SELECT 'dme' as type, codeid ident, geom, geolat as lat, geolong as lng
                FROM dme, const
                WHERE (codeid = const.name_or_ident OR txtname = const.name_or_ident)
                AND OVERLAPS (const.effective_date, const.effective_date, validfrom::timestamp with time zone, validto::timestamp with time zone)

                UNION

                SELECT 'ndb' as type, codeid ident, geom, geolat as lat, geolong as lng
                FROM ndb, const
                WHERE (codeid = const.name_or_ident OR txtname = const.name_or_ident)
                AND OVERLAPS (const.effective_date, const.effective_date, validfrom::timestamp with time zone, validto::timestamp with time zone)

                UNION

                SELECT 'tacan' as type, codeid ident, geom, geolat as lat, geolong as lng
                FROM tacan, const
                WHERE (codeid = const.name_or_ident OR txtname = const.name_or_ident)
                AND OVERLAPS (const.effective_date, const.effective_date, validfrom::timestamp with time zone, validto::timestamp with time zone)

                UNION

                SELECT 'vor' as type, ident ident, geom, latitude as lat, longitude as lng
                FROM vor, const
                WHERE (ident = const.name_or_ident OR txtname = const.name_or_ident)
                AND OVERLAPS (const.effective_date, const.effective_date, validfrom::timestamp with time zone, validto::timestamp with time zone)

                UNION

                SELECT 'waypoint' as type, ident ident, geom, latitude as lat, longitude as lng
                FROM waypoint, const
                WHERE (ident = const.name_or_ident OR nam = const.name_or_ident)
                AND OVERLAPS (const.effective_date, const.effective_date, validfrom::timestamp with time zone, validto::timestamp with time zone)

                UNION

                SELECT 'marker' as type, txtname ident, geom, geolat as lat, geolong as lng
                FROM marker, const
                WHERE txtname = const.name_or_ident
                AND OVERLAPS (const.effective_date, const.effective_date, validfrom::timestamp with time zone, validto::timestamp with time zone)

                UNION

                SELECT 'navaid' as type, designator ident, geom, latitude as lat, longitude as lng
                FROM navaids, const
                WHERE (designator = const.name_or_ident OR name = const.name_or_ident)
                AND OVERLAPS (const.effective_date, const.effective_date, validfrom::timestamp with time zone, validto::timestamp with time zone)

            ) AS x
        ) AS y
    ) AS fix

WHERE
    -- geometry exists
    fix.geom IS NOT NULL AND
    (
        -- it is within the specified distance within any border point of the given airspace geometry
        ST_DWithin (CAST (const.local_airspace_geom AS GEOGRAPHY), CAST (fix.geom AS GEOGRAPHY), const.border_tolerance_meters)
        OR
        -- or it intersects with the airspace
        ids__st_intersects (const.local_airspace_geom, fix.geom)
    )
ORDER BY
    ST_Distance (CAST (fix.geom AS GEOGRAPHY), CAST (const.ref_point_geom AS GEOGRAPHY)) ASC
LIMIT 1
;
Leonardo