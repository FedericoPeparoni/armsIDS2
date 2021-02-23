package com.ubitech.aim.routeparser;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

public class AisDbDataRetriever {

    private static String ProcFPL_TABLE_NAME = "ais.processed_fpl";
    private static String DepAdField_NAME = "departure_ad";
    private static String RouteField_NAME = "route";
    private static String DestAdField_NAME = "destination_ad";

    private Connection connection = null;

    AisDbDataRetriever(Connection connection) throws SQLException {
        this.connection = connection;
    }

    private ResultSet ExecuteQuery(String sqlStatement) throws SQLException {
        Statement createStatement = this.connection.createStatement();
        return createStatement.executeQuery(sqlStatement);
    }

    public LinkedList<String[]> getRoutes() {
        return getRoutes(ProcFPL_TABLE_NAME, DepAdField_NAME, RouteField_NAME, DestAdField_NAME);
    }

    private LinkedList<String[]> getRoutes(String tableName, String depFldName, String rteFldName, String destFldName) {

        String columns = depFldName + ", " + rteFldName + ", " + destFldName;

        String query = String.format("SELECT %s FROM %s", columns, ProcFPL_TABLE_NAME);
        ResultSet executeQuery;
        LinkedList<String[]> outputList = new LinkedList<String[]>();
        try {
            executeQuery = ExecuteQuery(query);
            while (executeQuery.next()) {
                String[] routeFields = new String[3];
                routeFields[0] = executeQuery.getString(DepAdField_NAME);
                routeFields[1] = executeQuery.getString(RouteField_NAME);
                routeFields[2] = executeQuery.getString(DestAdField_NAME);
                outputList.add(routeFields);
            }
            executeQuery.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return outputList;
    }
}
