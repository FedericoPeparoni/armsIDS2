package com.ubitech.aim.routeparser;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AeroDbDataRetriever extends DataRetriver {

    private static String ADHP_TABLE_NAME = "airport";
    private static String DME_TABLE_NAME = "dme";
    private static String NDB_TABLE_NAME = "ndb";
    private static String TACAN_TABLE_NAME = "tacan";
    private static String VOR_TABLE_NAME = "vor";
    private static String WAYPOINT_TABLE_NAME = "waypoint";
    private static String MARKER_TABLE_NAME = "marker";
    private static String AIRWAY_TABLE_NAME = "airway";
    private static String AIRWAY_SEGMENT_TABLE_NAME = "airwayseg";
    private static String SEGMENT_TABLE_NAME = "segmnt";
    private static String SM_FEA_TABLE_NAME = "sm_fea";
    private Dictionary<String, SmGeomInfo> smGeomDictionary = new Hashtable<String, SmGeomInfo>();

    public AeroDbDataRetriever(Connection connection) throws SQLException {
        super(connection);
        // TODO Auto-generated constructor stub
    }

    static String buildFixStatement(String dbName, Integer fixOccPk) {
        return MessageFormat.format("SELECT * FROM {0} WHERE {1} = {2}",
                new Object[] { dbName, AeroDbDataRetriever.primaryKeyOf(dbName), fixOccPk.toString() });
    }

    static String getNameFieldName(String dbName) {
        if (dbName.equals(AeroDbDataRetriever.ADHP_TABLE_NAME)) {
            return "nam";
        } else if (dbName.equals(AeroDbDataRetriever.DME_TABLE_NAME)) {
            return "txtname";
        } else if (dbName.equals(AeroDbDataRetriever.MARKER_TABLE_NAME)) {
            return "txtname";
        } else if (dbName.equals(AeroDbDataRetriever.NDB_TABLE_NAME)) {
            return "txtname";
        } else if (dbName.equals(AeroDbDataRetriever.TACAN_TABLE_NAME)) {
            return "txtname";
        } else if (dbName.equals(AeroDbDataRetriever.VOR_TABLE_NAME)) {
            return "txtname";
        } else if (dbName.equals(AeroDbDataRetriever.WAYPOINT_TABLE_NAME)) {
            return "nam";
        } else {
            throw new RuntimeException();
        }
    }

    static String getDesignatorFieldName(String dbName) {
        if (dbName.equals(AeroDbDataRetriever.ADHP_TABLE_NAME)) {
            return "ident";
        } else if (dbName.equals(AeroDbDataRetriever.DME_TABLE_NAME)) {
            return "codeid";
        } else if (dbName.equals(AeroDbDataRetriever.MARKER_TABLE_NAME)) {
            return "codeid";
        } else if (dbName.equals(AeroDbDataRetriever.NDB_TABLE_NAME)) {
            return "codeid";
        } else if (dbName.equals(AeroDbDataRetriever.TACAN_TABLE_NAME)) {
            return "codeid";
        } else if (dbName.equals(AeroDbDataRetriever.VOR_TABLE_NAME)) {
            return "ident";
        } else if (dbName.equals(AeroDbDataRetriever.WAYPOINT_TABLE_NAME)) {
            return "ident";
        } else {
            throw new RuntimeException();
        }
    }

    private SmGeomInfo getGeomInfo(String dbName) {
        SmGeomInfo toReturn = this.smGeomDictionary.get(dbName);
        if (toReturn == null) {
            toReturn = new SmGeomInfo(dbName, this);
            this.smGeomDictionary.put(dbName, toReturn);
        }
        return toReturn;
    }

    private static RoutePoint buildRoutePoint(Fix fix, Timestamp timestamp) {
        RoutePoint toReturn = new RoutePoint();
        Double latitude = fix.getLatitude();
        Double longitude = fix.getLongitude();
        toReturn.designator = fix.getDesignator();
        toReturn.pointType = fix.getType();
        toReturn.setCoordinates(longitude, latitude);
        toReturn.timestamp = (Timestamp) timestamp.clone();
        toReturn.recordID = fix.getRecordID();
        toReturn.name = fix.getName();
        return toReturn;
    }

    @Override
    RoutePoint getADHP(String designator, Timestamp timeStamp) {
        RoutePoint toReturn = null;
        String query = String.format("SELECT * FROM %s WHERE %s = '%s'", ADHP_TABLE_NAME, "ident", designator);
        ResultSet executeQuery;
        try {
            executeQuery = super.ExecuteQuery(query);
            Boolean hasData = executeQuery.next();
            if (hasData) {
                Fix airport = new Fix(executeQuery, ADHP_TABLE_NAME, this, timeStamp);
                toReturn = buildRoutePoint(airport, timeStamp);
            }
            executeQuery.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return toReturn;
    }

    private static Integer parseInteger(Object dbValue) {
        Double doubleValue = null;
        if (dbValue == null)
            return Integer.valueOf(-1);
        if (dbValue instanceof Double) {
            doubleValue = (Double) dbValue;
        } else {
            doubleValue = Double.valueOf(dbValue.toString());
        }
        return doubleValue.intValue();
    }

    private static Long parseLong(Object dbValue) {
        Double doubleValue = null;
        if (dbValue == null)
            return null;
        if (dbValue instanceof Double) {
            doubleValue = (Double) dbValue;
        } else {
            doubleValue = Double.valueOf(dbValue.toString());
        }
        return doubleValue.longValue();
    }

    @Override
    List<Long> getRoutesRecIDs(String designator, Timestamp timeStamp) {
        List<Long> toReturn = new ArrayList<Long>();
        String sqlStatement = MessageFormat.format("SELECT {3} FROM {0} WHERE {1} = ''{2}''",
                new Object[] { AIRWAY_TABLE_NAME, "txtdesig", designator, primaryKeyOf(AIRWAY_TABLE_NAME) });
        try {
            ResultSet rs = super.ExecuteQuery(sqlStatement);
            while (rs.next()) {
                toReturn.add(parseLong(rs.getObject(1)));
            }
            rs.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return toReturn;
    }

    String getTableName(Integer featurePk) {
        String toExecute = MessageFormat.format("SELECT db_name FROM {0} WHERE fea_pk = {1}",
                new Object[] { SM_FEA_TABLE_NAME, featurePk.toString() });
        String toReturn = null;
        try {
            toReturn = String.valueOf(super.ExecuteScalar(toExecute));
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return toReturn;
    }

    private static Map<String, Object> FreezeRow(ResultSet resultSet) {
        ResultSetMetaData md;
        HashMap<String, Object> row = null;
        try {
            md = resultSet.getMetaData();
            int columns = md.getColumnCount();
            row = new HashMap<String, Object>(columns);
            for (int i = 1; i <= columns; i++) {
                row.put(md.getColumnName(i).toLowerCase(), resultSet.getObject(i));
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return row;
    }

    private static String primaryKeyOf(String tableName) {
        return MessageFormat.format("{0}_pk", new Object[] { tableName });
    }

    private static String getAirwaySegmentQuery(Long airwayIdentifier, Timestamp timeStamp) {
        return MessageFormat.format("SELECT * FROM {0} WHERE {3} = {2} ORDER BY seq ASC",
                new Object[] { AIRWAY_SEGMENT_TABLE_NAME, AIRWAY_TABLE_NAME, airwayIdentifier.toString(),
                        primaryKeyOf(AIRWAY_TABLE_NAME) });
    }

    class SmGeomInfo {
        private final String dbName;
//		private final AeroDbDataRetriever aeroDbDataRetriever;
        private String latitudeFieldName, longitudeFieldName;

        public SmGeomInfo(String dbName, AeroDbDataRetriever aeroDbDataRetriever) {
            this.dbName = dbName;
//			this.aeroDbDataRetriever = aeroDbDataRetriever;
            String sqlStatement = MessageFormat.format(
                    "SELECT * FROM sm_geom WHERE db_name = ''{0}'' OR db_name = ''{1}''",
                    new Object[] { dbName.toUpperCase(), dbName.toLowerCase() });
            ResultSet rs;
            try {
                rs = aeroDbDataRetriever.ExecuteQuery(sqlStatement);
                rs.next();
                latitudeFieldName = rs.getString("lat_field");
                longitudeFieldName = rs.getString("lon_field");
                rs.close();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        public String getLatitudeFieldName() {
            return this.latitudeFieldName;
        }

        public String getLongitudeFieldName() {
            return this.longitudeFieldName;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof SmGeomInfo) {
                SmGeomInfo other = (SmGeomInfo) obj;
                return other.dbName.equals(this.dbName);
            }
            return false;
        }

        @Override
        public int hashCode() {
            // TODO Auto-generated method stub
            return this.dbName.hashCode();
        }
    }

    class Fix {
        private Integer fixFeaPk;
        private Integer fixOccPk;
        @SuppressWarnings("unused")
        private final AeroDbDataRetriever aeroDbDataRetriever;
        private final String dbName;
        @SuppressWarnings("unused")
        private final Timestamp timestamp;
        private final SmGeomInfo smGeom;
        private Map<String, Object> rowFreezing;

        public Fix(Integer fixFeaPk, Integer fixOccPk, AeroDbDataRetriever aeroDbDataRetriever, Timestamp timestamp)
                throws SQLException {
            this(aeroDbDataRetriever
                    .ExecuteQuery(buildFixStatement(aeroDbDataRetriever.getTableName(fixFeaPk), fixOccPk), true),
                    aeroDbDataRetriever.getTableName(fixFeaPk), aeroDbDataRetriever, timestamp);
            this.fixFeaPk = fixFeaPk;
            this.fixOccPk = fixOccPk;
        }

        public Fix(ResultSet fixRs, String dbName, AeroDbDataRetriever aeroDbDataRetriever, Timestamp timestamp) {
            this.aeroDbDataRetriever = aeroDbDataRetriever;
            this.timestamp = timestamp;
            this.dbName = dbName;
            smGeom = aeroDbDataRetriever.getGeomInfo(dbName);
            this.rowFreezing = FreezeRow(fixRs);
            fixFeaPk = null;
            fixOccPk = null;
        }

        private Object getValue(String fieldName) {
            return this.rowFreezing.get(fieldName.toLowerCase());
        }

        public String getDesignator() {
            try {
                return (String) this.getValue(this.getDesignatorFieldName());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return new String();
        }

        private String getDesignatorFieldName() {
            return AeroDbDataRetriever.getDesignatorFieldName(this.dbName);
        }

        public boolean isOfType(List<RoutePointType> routePointTypes) {
            for (RoutePointType rpt : routePointTypes) {
                if (this.isOfType(rpt)) {
                    return true;
                }
            }
            return false;
        }

        private String getLatitudeFieldName() {
            return this.smGeom.getLatitudeFieldName();
        }

        private String getLongitudeFieldName() {
            return this.smGeom.getLongitudeFieldName();
        }

        public double getLatitude() {
            double toReturn = 0;
            try {
                toReturn = (Double) this.getValue(this.getLatitudeFieldName());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return toReturn;
        }

        public double getLongitude() {
            double toReturn = 0;
            try {
                toReturn = (Double) this.getValue(this.getLongitudeFieldName());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return toReturn;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Fix) {
                Fix other = (Fix) obj;
                return other.fixFeaPk.equals(this.fixFeaPk) && other.fixOccPk.equals(this.fixOccPk);
            }
            return false;
        }

        @Override
        public int hashCode() {
            // TODO Auto-generated method stub
            return this.fixFeaPk.hashCode() + this.fixOccPk.hashCode();
        }

        public RoutePointType getType() {
            String dbName = this.dbName;
            if (dbName.equals(AeroDbDataRetriever.ADHP_TABLE_NAME)) {
                return RoutePointType.ADHP;
            } else if (dbName.equals(AeroDbDataRetriever.DME_TABLE_NAME)) {
                return RoutePointType.DME;
            } else if (dbName.equals(AeroDbDataRetriever.MARKER_TABLE_NAME)) {
                return RoutePointType.Marker;
            } else if (dbName.equals(AeroDbDataRetriever.NDB_TABLE_NAME)) {
                return RoutePointType.NDB;
            } else if (dbName.equals(AeroDbDataRetriever.TACAN_TABLE_NAME)) {
                return RoutePointType.TACAN;
            } else if (dbName.equals(AeroDbDataRetriever.VOR_TABLE_NAME)) {
                return RoutePointType.VOR;
            } else if (dbName.equals(AeroDbDataRetriever.WAYPOINT_TABLE_NAME)) {
                return RoutePointType.Waypoint;
            } else {
                return RoutePointType.OTHER;
            }
        }

        public boolean isOfType(RoutePointType routePointType) {
            RoutePointType type = this.getType();
            return type.equals(routePointType);
        }

        public String getName() {
            try {
                return (String) this.getValue(AeroDbDataRetriever.getNameFieldName(this.dbName));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        public Long getRecordID() {
            try {
                return parseInteger(this.getValue(AeroDbDataRetriever.primaryKeyOf(this.dbName))).longValue();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }
    }

    private Fix getFix(ResultSet row, Timestamp timestamp, String fixFeaPkFieldName, String fixOccPkFieldName) {
        Fix toReturn = null;
        Integer feaPk;
        try {
            feaPk = row.getInt(fixFeaPkFieldName);
            Integer occPk = row.getInt(fixOccPkFieldName);
            toReturn = new Fix(feaPk, occPk, this, timestamp);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return toReturn;
    }

    private static String buildSegmentQuery(Long segmentPk, Timestamp timestamp) {
        return MessageFormat.format("SELECT * FROM {0} WHERE {1} = {2}",
                new Object[] { SEGMENT_TABLE_NAME, primaryKeyOf(SEGMENT_TABLE_NAME), segmentPk.toString() });
    }

    private void processFixToList(List<RoutePoint> currentList, Fix fixToAdd, List<RoutePointType> pointTypes,
            Timestamp timestamp) {
        if (fixToAdd.isOfType(pointTypes)) {
            RoutePoint toAdd = buildRoutePoint(fixToAdd, timestamp);
            currentList.add(toAdd);
        }
    }

    @Override
    List<RoutePoint> getFixes(String designator, List<RoutePointType> pointTypes, Timestamp timeStamp)
            throws SQLException {
        List<RoutePoint> toReturn = new ArrayList<RoutePoint>();
        for (RoutePointType rpt : pointTypes) {
            String fixDbName = getFixDbName(rpt);
            if (fixDbName != null) {
                String desigFieldName = AeroDbDataRetriever.getDesignatorFieldName(fixDbName);
                if (desigFieldName != null) {
                    String sqlStatement = MessageFormat.format("SELECT * FROM {0} WHERE {1} = ''{2}''",
                            new Object[] { fixDbName, desigFieldName, designator });
                    ResultSet executeQuery = super.ExecuteQuery(sqlStatement);
                    while (executeQuery.next()) {
                        Fix fix = new Fix(executeQuery, fixDbName, this, timeStamp);
                        RoutePoint toAdd = buildRoutePoint(fix, timeStamp);
                        toReturn.add(toAdd);
                    }
                    executeQuery.close();
                }
            }
        }
        return toReturn;
    }

    @Override
    List<RoutePoint> getFixesAroundPoint(List<RoutePointType> pointTypes, String refPointWKT, Double radiusInMeters,
            Timestamp timeStamp) throws SQLException {

        List<RoutePoint> toReturn = new LinkedList<RoutePoint>();
        for (RoutePointType rpt : pointTypes) {
            String fixDbName = getFixDbName(rpt);
            SmGeomInfo smGeom = getGeomInfo(fixDbName);
//			String geomFieldName = smGeom.getGeomFieldName();
            String longFieldName = smGeom.getLongitudeFieldName();
            String latFieldName = smGeom.getLatitudeFieldName();

            if (fixDbName != null) {
                String desigFieldName = AeroDbDataRetriever.getDesignatorFieldName(fixDbName);
                if (desigFieldName != null) {
//				String sqlStatement = MessageFormat.format("SELECT * FROM {0} WHERE ST_Distance({1}::geography, ST_GeogFromText(''{2}'')) < {3}", new Object[] {fixDbName, geomFieldName, refPointWKT, radiusInMeters.toString()});
                    String sqlStatement = MessageFormat.format(
                            "SELECT * FROM {0} WHERE ST_Distance(ST_Point({1}, {2})::geography, ST_GeogFromText(''{3}'')) < {4}",
                            new Object[] { fixDbName, longFieldName, latFieldName, refPointWKT,
                                    radiusInMeters.toString() });
                    ResultSet executeQuery = super.ExecuteQuery(sqlStatement);
                    while (executeQuery.next()) {
                        Fix fix = new Fix(executeQuery, fixDbName, this, timeStamp);
                        RoutePoint toAdd = buildRoutePoint(fix, timeStamp);
                        toReturn.add(toAdd);
                    }
                    executeQuery.close();
                }
            }
        }
        return toReturn;
    }

    @Override
    List<RoutePoint> getFixesAroundPoint(String designLikePattern, List<RoutePointType> pointTypes, String refPointWKT,
            Double radiusInMeters, Timestamp timeStamp) throws SQLException {

        List<RoutePoint> toReturn = new LinkedList<RoutePoint>();
        for (RoutePointType rpt : pointTypes) {
            String fixDbName = getFixDbName(rpt);
            SmGeomInfo smGeom = getGeomInfo(fixDbName);
//			String geomFieldName = smGeom.getGeomFieldName();
            String longFieldName = smGeom.getLongitudeFieldName();
            String latFieldName = smGeom.getLatitudeFieldName();

            if (fixDbName != null) {
                String desigFieldName = AeroDbDataRetriever.getDesignatorFieldName(fixDbName);
                if (desigFieldName != null) {
//				String sqlStatement = MessageFormat.format("SELECT * FROM {0} WHERE {1} LIKE ''{2}'' AND ST_Distance({3}::geography, ST_GeogFromText(''{4}'')) < {5}", new Object[] {fixDbName, desigFieldName, designLikePattern, geomFieldName, refPointWKT, radiusInMeters.toString()});
                    String sqlStatement = MessageFormat.format(
                            "SELECT * FROM {0} WHERE {1} LIKE ''{2}'' AND ST_Distance(ST_Point({3}, {4})::geography, ST_GeogFromText(''{5}'')) < {6}",
                            new Object[] { fixDbName, desigFieldName, designLikePattern, longFieldName, latFieldName,
                                    refPointWKT, radiusInMeters.toString() });
                    ResultSet executeQuery = super.ExecuteQuery(sqlStatement);
                    while (executeQuery.next()) {
                        Fix fix = new Fix(executeQuery, fixDbName, this, timeStamp);
                        RoutePoint toAdd = buildRoutePoint(fix, timeStamp);
                        toReturn.add(toAdd);
                    }
                    executeQuery.close();
                }
            }
        }
        return toReturn;
    }

    private String getFixDbName(RoutePointType rpt) {
        switch (rpt) {
        case ADHP:
            return ADHP_TABLE_NAME;
        case DME:
            return DME_TABLE_NAME;
        case Marker:
            return MARKER_TABLE_NAME;
        case NDB:
            return NDB_TABLE_NAME;
        case OTHER:
            return null;
        case TACAN:
            return TACAN_TABLE_NAME;
        case VOR:
            return VOR_TABLE_NAME;
        case Waypoint:
            return WAYPOINT_TABLE_NAME;
        default:
            return null;
        }
    }

    @Override
    List<List<RoutePoint>> getRoutePoints(List<Long> atsRouteRecordsIDs, Timestamp timestamp) {
        List<List<RoutePoint>> toReturn = new LinkedList<List<RoutePoint>>();
        List<RoutePointType> pointTypes = anyRoutePointType();
        List<Long> routeRecIDs = atsRouteRecordsIDs;
        for (Long routeIdent : routeRecIDs) {
            List<RoutePoint> routePoints = new ArrayList<RoutePoint>();
            String queryForAirwaySegment = getAirwaySegmentQuery(routeIdent, timestamp);
            try {
                ResultSet executeQuery = super.ExecuteQuery(queryForAirwaySegment);
                Fix lastToFix = null;
                while (executeQuery.next()) {
                    Long segmentPk = executeQuery.getLong(primaryKeyOf(SEGMENT_TABLE_NAME));
                    String segmentQuery = buildSegmentQuery(segmentPk, timestamp);
                    ResultSet segmentQueryRs = super.ExecuteQuery(segmentQuery);
                    if (segmentQueryRs.next()) {
                        Fix fromFix = this.getFix(segmentQueryRs, timestamp, "fromfixfea_pk", "fromfix_pk");
                        if (lastToFix != null && lastToFix.equals(fromFix)) {
                            // NOP
                        } else {
                            this.processFixToList(routePoints, fromFix, pointTypes, timestamp);
                        }
                        Fix toFix = this.getFix(segmentQueryRs, timestamp, "tofixfea_pk", "tofix_pk");
                        this.processFixToList(routePoints, toFix, pointTypes, timestamp);
                        lastToFix = toFix;
                    }
                    segmentQueryRs.close();
                }
                executeQuery.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (routePoints.size() > 0) {
                toReturn.add(routePoints);
            }
        }
        return toReturn;
    }
}
