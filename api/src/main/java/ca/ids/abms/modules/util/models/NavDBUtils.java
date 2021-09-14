package ca.ids.abms.modules.util.models;

import ca.ids.abms.config.cache.CacheNames;
import ca.ids.abms.modules.airspaces.Airspace;
import ca.ids.abms.modules.airspaces.utiliy.AirspaceNavDBMapper;
import ca.ids.abms.modules.util.models.geometry.CoordinatesVO;
import ca.ids.abms.util.StringUtils;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.io.WKTWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.google.common.io.CharStreams;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
public class NavDBUtils {

    private static final Logger LOG = LoggerFactory.getLogger(NavDBUtils.class);

    private static final String AIRPORT_COORD_SQLQRY = "SELECT ST_X(ST_Centroid (a.geom)) as lng, ST_Y(ST_Centroid (a.geom)) as lat FROM v_airport a WHERE ident = :airportIdentifier and geom is not null order by airport_pk desc limit 1";

    private static final String COUNT_AIRPORT_SQLQRY = "SELECT count(a.ident) FROM v_airport a WHERE a.ident LIKE :airportIdentifier";

    private static final String GET_ALL_AIRSPACES_SQLQRY = "select airspace_pk, ident, typ, geom, nam, absupperlimit from v_airspace_all where typ in ('TMA','FIR','FIR_P') order by airspace_pk asc";

    private static final String AIRSPACE_SQLQRY = "select airspace_pk, ident, typ, geom, nam, absupperlimit from v_airspace_all where airspace_pk = ";

    private static final String AERODROME_PREFIXES = "select org.codeid from airport a, orgauth org where a.orgauth_pk = org.orgauth_pk and org.codetype = 'STATE' and org.txtname = :countryName group by org.codeid;";

    private static final String GET_FIRS_BY_LOCATION = "SELECT ident FROM v_airspace_all WHERE ST_Intersects(geom::geography,ST_GeographyFromText(:text));";

    private static final String IS_AD_INSIDE_SOUTH_SUDAN = "SELECT ap.airport_pk FROM v_airspace_all asp, v_airport ap " +
        "WHERE asp.typ = 'FIR_P' AND asp.ident = 'HSSS2' and ap.ident = :text AND ST_Intersects(asp.geom::geography, ap.geom);";

    private static final String FIND_GREAT_CIRCLE_DISTANCE = "SELECT ST_Distance (CAST (:wkt1 AS GEOGRAPHY), CAST (:wkt2 AS GEOGRAPHY), true)";

    // SQL file is in src/main/resources/...
    private static final String FIND_CLOSEST_SIGNIFICANT_POINT = loadSqlQueryResource ("FIND_CLOSEST_SIGNIFICANT_POINT.sql");

    private final JdbcTemplate navdbJdbcTemplate;

    private final GeometryFactory geometryFactory = new GeometryFactory (new PrecisionModel(), 4326);

    private static final String AIRSPACE_TYPE_FIR = "FIR";

    public NavDBUtils(JdbcTemplate navDBJdbcTemplate){
        this.navdbJdbcTemplate=navDBJdbcTemplate;
    }

    @Cacheable(value = CacheNames.NAVDB_IDENTS, sync = true)
    public synchronized Boolean checkIdentFromAirportNAVDB(String airportIdentifier) {
        LOG.debug("Start check if ident there is in NAVDB Airport Table: {}", airportIdentifier);
        Boolean returnValue = Boolean.FALSE;
        if (navdbJdbcTemplate.getDataSource() != null && StringUtils.isNotBlank(airportIdentifier)) {

            NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(navdbJdbcTemplate.getDataSource());
            Map<String, String> namedParameters = Collections.singletonMap("airportIdentifier", airportIdentifier);
            int count = namedParameterJdbcTemplate.queryForObject(COUNT_AIRPORT_SQLQRY, namedParameters, Integer.class);
            if (count > 0) {
                returnValue = Boolean.TRUE;
            }
        }
        LOG.debug("End check if ident there is in NAVDB Airport Table: {}", returnValue);

        return returnValue;
    }

    @Cacheable(value = CacheNames.NAVDB_COORDINATES, sync = true)
    public synchronized CoordinatesVO getCoordinatesFromAirportNAVDB(String airportIdentifier) {
        LOG.debug("Start get Coordinates from Airport Table in NAVDB: {}", airportIdentifier);
        CoordinatesVO coordinate = null;

        if (navdbJdbcTemplate.getDataSource() != null && airportIdentifier != null && !airportIdentifier.isEmpty()) {
            NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(navdbJdbcTemplate.getDataSource());
            Map<String, String> namedParameters = Collections.singletonMap("airportIdentifier", airportIdentifier);
            List <CoordinatesVO> result = namedParameterJdbcTemplate.query(AIRPORT_COORD_SQLQRY, namedParameters, new RowMapper <CoordinatesVO>() {
                @Override
                public CoordinatesVO mapRow(ResultSet rs, int rowNum) throws SQLException {
                    final float lng = rs.getFloat("lng");
                    if (lng != 0.0f || !rs.wasNull()) {
                        final Float lat = rs.getFloat ("lat");
                        if (lat != 0.0f || !rs.wasNull()) {
                            return new CoordinatesVO ((double)lat, (double)lng);
                        }
                    }
                    return null;
                }
            });
            if (result != null && !result.isEmpty() && result.get(0) != null) {
                coordinate = result.get(0);
            }
        }

        LOG.debug("End get Coordinates from Airport Table in NAVDB: {}", coordinate);

        return coordinate;
    }

    @Cacheable(value = CacheNames.NAVDB_AIRSPACE_BY_ID)
    public Airspace getAirspaceNavdbById(Integer id) {
        LOG.debug("Start get Airspace by Id from NAVDB");
        Airspace airspace = null;
        if (navdbJdbcTemplate.getDataSource() != null) {
            NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(navdbJdbcTemplate.getDataSource());
            List<Airspace> airspaceList = namedParameterJdbcTemplate.query(AIRSPACE_SQLQRY + id, new AirspaceNavDBMapper());

            if(airspaceList!=null && !airspaceList.isEmpty()){
                airspace=airspaceList.get(0);

                // 2020-04-09 TFS 115533
                //FIRs are included by default, TMAs are excluded by default
                airspace.setAirspaceIncluded(airspace.getAirspaceType().startsWith(AIRSPACE_TYPE_FIR));
            }
        }
        LOG.debug("End get Airspace by Id from NAVDB");
        return airspace;
    }

    @Cacheable(value = CacheNames.NAVDB_AIRSPACES)
    public List<Airspace> getAllAirspacesNavdb() {
        LOG.debug("Start get AllAirspace from NAVDB");
        List<Airspace> airspaceLst = null;
        if (navdbJdbcTemplate.getDataSource() != null) {
            NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(navdbJdbcTemplate.getDataSource());
            airspaceLst = namedParameterJdbcTemplate.query(GET_ALL_AIRSPACES_SQLQRY,
                new AirspaceNavDBMapper());
        }
        LOG.debug("End get AllAirspace from NAVDB");
        return airspaceLst;
    }

    @Cacheable(value = CacheNames.NAVDB_AERODROME_PREFIXES)
    public List<String> getAerodromePrefixes(final String countryName) {
        List<String> prefixes = null;
        if (navdbJdbcTemplate.getDataSource() != null && countryName != null) {
            final NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(navdbJdbcTemplate.getDataSource());
            final Map<String, String> namedParameters = Collections.singletonMap("countryName", countryName.toUpperCase(Locale.US));
            prefixes = namedParameterJdbcTemplate.queryForList(AERODROME_PREFIXES, namedParameters, String.class);
        }
        LOG.debug("Retrieved {} aerodrome prefixes for the country {}", prefixes != null ? prefixes.size() : '0', countryName);
        return prefixes;
    }

    @Cacheable(value = CacheNames.NAVDB_FIRS_BY_LOCATION)
    public List<String> getFirsByLocation(final String coordinate) {
        List<String> firs = null;
        if (navdbJdbcTemplate.getDataSource() != null && coordinate != null) {
            final NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(navdbJdbcTemplate.getDataSource());
            final Map<String, String> namedParameters = Collections.singletonMap("text", coordinate);
            firs = namedParameterJdbcTemplate.queryForList(GET_FIRS_BY_LOCATION, namedParameters, String.class);
        }
        LOG.debug("Retrieved {} firs for coordinates {}", firs != null ? firs.size() : '0', coordinate);
        return firs;
    }

    @Cacheable(value = CacheNames.NAVDB_AD_INSIDE_SOUTH_SUDAN)
    public Boolean isAdInsideSouthSudan(String adCode) {
        if(adCode == null || adCode.isEmpty()) {
            return false;
        }
        List<String> ids=null;
        if (navdbJdbcTemplate.getDataSource() != null) {
            final NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(navdbJdbcTemplate.getDataSource());

            final Map<String, String> namedParameters = Collections.singletonMap("text", adCode);
            ids = namedParameterJdbcTemplate.queryForList(IS_AD_INSIDE_SOUTH_SUDAN, namedParameters, String.class);

        }
        LOG.debug("Retrieved {} FIR id for aerodrome {}", ids , adCode);
        return (ids!=null && !ids.isEmpty());
    }

    private final Geometry tryParseWkt (final String wkt) {
        final WKTReader reader = new WKTReader (geometryFactory);
        try {
            return reader.read (wkt);
        }
        catch (final ParseException e) {
            LOG.error ("Failed to parse geometry WKT \"{}\": {}", wkt, e.getMessage(), e);
        }
        return null;
    }

    private static String formatPointWkt (final Coordinate coord) {
        //return String.format ("POINT (%f %f)", coord.x, coord.y);
        GeometryFactory geomFactory = new GeometryFactory();
        Point point = geomFactory.createPoint(coord);
        WKTWriter writer = new WKTWriter();
        String wktStr = writer.write(point);
        return wktStr;
    }

    /**
     * Find a nearby significant point near a local airspace.
     *
     * We will look for a point whose name matches "nameOrIdent", that is within "distanceMeters" of
     * any of the border points making up the "localAirspaceWkt". Among multiple matches we pick the one closest
     * to "refPointWkt". We segmentize localAirspaceWkt using geoid math before any calculations.
     *
     * @return the coordinate of the closest point matching the arguments, or null
     *
     * @param nameOrIdent           - (String) the identifier of the waypoint
     * @param refCoord              - (String) reference point geometry WKT, typically the departure point of the flight
     * @param localAirspaceWkt      - (String) geometry WKT that represents the local airspace(s)
     * @param localAirspaceSegmentLengthMeters - (Integer) segmentize local_airspace_geom with this segment length
     * @param distanceMeters        - (Integer) maximum distance between refPointWkt and localAirspaceWkt
     *
     */
    public Coordinate findClosestSignificantPoint (final String nameOrIdent, final Coordinate refCoord, final String localAirspaceWkt,
            final Integer localAirspaceSegmentLengthMeters, final Integer distanceMeters) {
        if (nameOrIdent == null || refCoord == null) {
            return null;
        }
        final NamedParameterJdbcTemplate t = new NamedParameterJdbcTemplate (navdbJdbcTemplate.getDataSource());
        final String refPointWkt = formatPointWkt (refCoord);
        final Map <String, String> args = new HashMap<>();
        args.put ("nameOrIdent", nameOrIdent);
        args.put ("refPointWkt", refPointWkt);
        args.put ("localAirspaceWkt", localAirspaceWkt);
        args.put ("distanceMeters", Integer.toString (distanceMeters));
        args.put ("localAirspaceSegmentLengthMeters", Integer.toString (localAirspaceSegmentLengthMeters));
        final Geometry result = t.query(FIND_CLOSEST_SIGNIFICANT_POINT, args, rs->{
            if (rs.next()) {
                final String type = rs.getString ("type");
                final String ident = rs.getString ("ident");
                final Geometry geom = tryParseWkt (rs.getString ("geom"));
                LOG.debug ("found significant point type={} ident={} geom={}", type, ident, geom);
                return geom;
            }
            return null;
        });
        return result == null ? null : result.getCentroid().getCoordinate();
    }

    public Double getGreatCircleDistance(Coordinate coord1, Coordinate coord2) {
        if (coord1 != null && coord2 != null) {
            final NamedParameterJdbcTemplate t = new NamedParameterJdbcTemplate (navdbJdbcTemplate.getDataSource());
            final String wkt1 = formatPointWkt (coord1);
            final String wkt2 = formatPointWkt (coord2);
            final Map <String, String> args = new HashMap<>();
            args.put ("wkt1", wkt1);
            args.put ("wkt2", wkt2);
            final List <String> result = t.queryForList (FIND_GREAT_CIRCLE_DISTANCE, args, String.class);
            if (result != null && !result.isEmpty() && result.get(0) != null && !result.get(0).isEmpty()) {
                try {
                    return Double.parseDouble(result.get(0));
                }
                catch (final NumberFormatException e) {
                    LOG.error ("Failed to parse result of ST_Distance(): {}", e.getMessage(), e);
                }

            }
        }
        return null;
    }

    @SuppressWarnings ("squid:S00112")
    private static final String loadSqlQueryResource (final String path) {
        try (final InputStream x = new ClassPathResource (path, NavDBUtils.class).getInputStream()) {
            return CharStreams.toString (new InputStreamReader (x, StandardCharsets.UTF_8));
        }
        catch (final IOException x) {
            throw new RuntimeException (x);
        }
    }

}
