package com.ubitech.aim.routeparser;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

abstract class DataRetriver {

    protected Connection connection = null;
    private Timestamp _dataRetrievingTimestamp = null;

    DataRetriver(Connection connection) throws SQLException {
        this.connection = connection;
        this.setTimestamp(now());
    }

    private static Timestamp now() {
        java.util.Date date = new java.util.Date();
        return new Timestamp(date.getTime());
    }

    protected ResultSet ExecuteQuery(String sqlStatement) throws SQLException {
        Statement createStatement = this.connection.createStatement();
        return createStatement.executeQuery(sqlStatement);
    }

    protected ResultSet ExecuteQuery(String sqlStatement, Boolean moveAtFirstRow) throws SQLException {
        ResultSet rs = this.ExecuteQuery(sqlStatement);
        if (moveAtFirstRow) {
            rs.next();
        }
        return rs;
    }

    protected Object ExecuteScalar(String sqlStatement) throws SQLException {
        ResultSet executeQuery = this.ExecuteQuery(sqlStatement);
        Object toReturn = null;
        if (executeQuery.next()) {
            toReturn = executeQuery.getObject(1);
        }
        executeQuery.close();
        return toReturn;
    }

    ResultSet ExecuteQuery(String tableName, String whereCondition) throws SQLException {
        String finalQuery = String.format("SELECT * FROM %s WHERE %s", tableName, whereCondition);
        return this.ExecuteQuery(finalQuery);
    }

//	The function is supposed to close all initialised PreparedStatement instances.
//	It is supposed to be called any time the resources should be released.
    void close() throws SQLException {
        // this.connection.close();
        // this.connection = null;
    }

//	Returns null if no one Airport/Heliport with transfered 'designator' value is found in the database. 
    abstract RoutePoint getADHP(String designator, Timestamp timeStamp);

    RoutePoint getADHP(String designator) {
        return this.getADHP(designator, this.getTimestamp());
    }

//	Retrieves from the database all routes with a particular designator.
//	Returns an empty list if no one route with transfered 'designator' value is found in the database.
    abstract List<Long> getRoutesRecIDs(String designator, Timestamp timeStamp);

    List<Long> getRouteRecID(String designator) {
        return this.getRoutesRecIDs(designator, this.getTimestamp());
    }

    abstract List<List<RoutePoint>> getRoutePoints(List<Long> atsRouteRecordIDs, Timestamp timestamp);

//	Returns all fixes (if exist in the database) with a particular designator from all tables/features listed in pointTypes parameter.
//	Returns an empty lists if no one fix with transfered 'designator' and 'List<RoutePointType>' values is found in the database
    List<RoutePoint> getFixes(String designator, RoutePointType pointType, Timestamp timeStamp) throws SQLException {
        List<RoutePointType> pointTypesFake = new LinkedList<RoutePointType>();
        pointTypesFake.add(pointType);
        return this.getFixes(designator, pointTypesFake, timeStamp);
    }

//	Returns all fixes (if exist in the database) with a particular designator from all tables/features listed in pointTypes parameter.
//	Returns an empty lists if no one fix with transfered 'designator' and 'List<RoutePointType>' values is found in the database
    abstract List<RoutePoint> getFixes(String designator, List<RoutePointType> pointTypes, Timestamp timeStamp)
            throws SQLException;

    List<RoutePoint> getFixes(String designator, List<RoutePointType> pointTypes) throws SQLException {
        return this.getFixes(designator, pointTypes, this.getTimestamp());
    }

//=========================================================================

//	Returns all fixes (if exist in the database) with a particular designator from a table/feature specified in 'pointType' parameter, 
//	and located at a distance no longer than 'radiusInMeters' from 'refPointWKT'.
//	Returns an empty lists if no one fix with transfered 'designator' and 'RoutePointType' values is found in the database.
    List<RoutePoint> getFixesAroundPoint(String designLikePattern, RoutePointType pointType, String refPointWKT,
            Double radiusInMeters, Timestamp timeStamp) throws SQLException {
        List<RoutePointType> pointTypesFake = new LinkedList<RoutePointType>();
        pointTypesFake.add(pointType);
        return this.getFixesAroundPoint(designLikePattern, pointTypesFake, refPointWKT, radiusInMeters, timeStamp);
    }

//	Returns all fixes (if exist in the database) with a particular designator from all tables/features listed in 'pointTypes' parameter, 
//	and located at a distance no longer than 'radiusInMeters' from 'refPointWKT'.
//	Returns an empty lists if no one fix with transfered 'designator' and 'List<RoutePointType>' values is found in the database.
    abstract List<RoutePoint> getFixesAroundPoint(String designLikePattern, List<RoutePointType> pointTypes,
            String refPointWKT, Double radiusInMeters, Timestamp timeStamp) throws SQLException;

    List<RoutePoint> getFixesAroundPoint(String designLikePattern, List<RoutePointType> pointTypes, String refPointWKT,
            Double radiusInMeters) throws SQLException {
        return this.getFixesAroundPoint(designLikePattern, pointTypes, refPointWKT, radiusInMeters,
                this.getTimestamp());
    }

//=========================================================================

//	Returns all fixes (if exist in the database) from a table/feature specified in 'pointType' parameter, 
//	and located at a distance no longer than 'radiusInMeters' from 'refPointWKT'.
//	Returns an empty lists if no one fix with transfered 'RoutePointType' value is found in the database.
    List<RoutePoint> getFixesAroundPoint(RoutePointType pointType, String refPointWKT, Double radiusInMeters,
            Timestamp timeStamp) throws SQLException {
        List<RoutePointType> pointTypesFake = new LinkedList<RoutePointType>();
        pointTypesFake.add(pointType);
        return this.getFixesAroundPoint(pointTypesFake, refPointWKT, radiusInMeters, timeStamp);
    }

//	Returns all fixes (if exist in the database) from tables/features listed in 'pointTypes' parameter, 
//	and located at a distance no longer than 'radiusInMeters' from 'refPointWKT'.
//	Returns an empty lists if no one fix with transfered 'List<RoutePointType>' values is found in the database
    abstract List<RoutePoint> getFixesAroundPoint(List<RoutePointType> pointTypes, String refPointWKT,
            Double radiusInMeters, Timestamp timeStamp) throws SQLException;

    List<RoutePoint> getFixesAroundPoint(List<RoutePointType> pointTypes, String refPointWKT, Double radiusInMeters)
            throws SQLException {
        return this.getFixesAroundPoint(pointTypes, refPointWKT, radiusInMeters, this.getTimestamp());
    }

//=========================================================================

    public void setTimestamp(Timestamp _dataRetrievingTimestamp) {
        this._dataRetrievingTimestamp = _dataRetrievingTimestamp;
    }

    public Timestamp getTimestamp() {
        return _dataRetrievingTimestamp;
    }

    protected static List<RoutePointType> anyRoutePointType() {
        List<RoutePointType> list = Arrays.asList(RoutePointType.values());
        return list;
    }
}
