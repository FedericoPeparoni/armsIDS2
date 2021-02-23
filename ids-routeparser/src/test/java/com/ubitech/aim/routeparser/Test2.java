package com.ubitech.aim.routeparser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.TreeSet;

public class Test2 {

    private static String sourceFileName = new String(
            "C:\\Users\\p.samarin\\Documents\\IDS DOCS\\TEST\\Routes_KENYA.txt");

    private static String url1 = "jdbc:postgresql://aim78dev1/navdb";
    private static String user1 = "navdb";
    private static String pwd1 = "aftn";

    private static String url2 = "jdbc:postgresql://aim78dev1/aim";
    private static String user2 = "aim";
    private static String pwd2 = "aftn";

    private static Connection createConnection(String url, String user, String pwd) throws Exception {
        Class.forName("org.postgresql.Driver");
        return java.sql.DriverManager.getConnection(url, user, pwd);
    }

    public static void main(String argsp[]) throws Exception {
        Connection connection1 = createConnection(url1, user1, pwd1);
        Connection connection2 = createConnection(url2, user2, pwd2);

        String destinationFileName = sourceFileName.substring(0, sourceFileName.lastIndexOf('.')) + "_Report"
                + sourceFileName.substring(sourceFileName.lastIndexOf('.'), sourceFileName.length());

        LinkedList<String[]> routes;
        /*
         * routes = getRouresFromDB(connection2); TreeSet<String> treeSetOfRoutes = new
         * TreeSet<String>();
         * 
         * for (String[] route: routes) { treeSetOfRoutes.add(route[0] + "\n" + route[1]
         * + "\n" + route[2] + "\n\n"); } routes.clear();
         * 
         * writeRoutesIntoFile(treeSetOfRoutes, sourceFileName);
         */
        routes = getRouresFromFile(sourceFileName);
        LinkedList<String> toReportFile = new LinkedList<String>();

        RouteFinder routeFinder = new RouteFinder(connection1);
        LinkedList<String> outputWKTs;

        int numOfProcessedRoutes = 0;
        int numOfRecognizedA2Aroutes = 0;
        int numOfRecognizedA2Broutes = 0;
        int numOfUnRecognizedA2Aroutes = 0;
        int numOfUnRecognizedA2Broutes = 0;

        int numOfNotFoundADHPs = 0;
        int numOfNotFoundNavaids = 0;
        int numOfNotFoundWaypoints = 0;
        int numOfNotFoundATSroutes = 0;
        int numOfDisconnectedATSroutes = 0;
        int numOfIncorrectTokens = 0;
        int totalNumOfNotFoundPoints = 0;

        TreeSet<String> setOfNotFoundADHPs = new TreeSet<String>();
        TreeSet<String> setOfNotFoundNavaids = new TreeSet<String>();
        TreeSet<String> setOfNotFoundWaypoints = new TreeSet<String>();
        TreeSet<String> setOfNotFoundATSroutes = new TreeSet<String>();
        TreeSet<String> setOfDisconnectedATSroutes = new TreeSet<String>();
        TreeSet<String> setOfIncorrectTokens = new TreeSet<String>();

        boolean foundRouteFlag;

        for (String[] route : routes) {
            numOfProcessedRoutes++;

            routeFinder.setNewFlightPlan(route[0], route[1], new String[] { route[2] });
            foundRouteFlag = false;

            if (routeFinder.isCircularRoute()) {
                outputWKTs = routeFinder.getCircularRouteLineWKT("N0350", "0200", 1.5, 100);

                if (outputWKTs == null) {
                    numOfUnRecognizedA2Aroutes++;
                } else {
                    numOfRecognizedA2Aroutes++;
                    foundRouteFlag = true;
                }
            }

            else {
                outputWKTs = routeFinder.getRouteLineWKT();

                if (outputWKTs == null) {
                    numOfUnRecognizedA2Broutes++;
                } else {
                    numOfRecognizedA2Broutes++;
                    foundRouteFlag = true;
                }
            }

            setOfNotFoundADHPs.addAll(routeFinder.getNotFoundADHPs());

            if (foundRouteFlag) {
                setOfNotFoundNavaids.addAll(routeFinder.getNotFoundNavaids());
                setOfNotFoundWaypoints.addAll(routeFinder.getNotFoundWaypoints());
                setOfNotFoundATSroutes.addAll(routeFinder.getNotFoundATSroutes());
                setOfDisconnectedATSroutes.addAll(routeFinder.getDisconnectedATSroutes());
                setOfIncorrectTokens.addAll(routeFinder.getIncorrectTokens());
            }
        }

        numOfNotFoundADHPs = setOfNotFoundADHPs.size();
        numOfNotFoundNavaids = setOfNotFoundNavaids.size();
        numOfNotFoundWaypoints = setOfNotFoundWaypoints.size();
        numOfNotFoundATSroutes = setOfNotFoundATSroutes.size();
        numOfDisconnectedATSroutes = setOfDisconnectedATSroutes.size();
        numOfIncorrectTokens = setOfIncorrectTokens.size();
        totalNumOfNotFoundPoints = numOfNotFoundADHPs + numOfNotFoundNavaids + numOfNotFoundWaypoints;

//		System.out.println("");
        System.out.println("\n==============================================\n");
//		System.out.println("");

        toReportFile.add("          METRICS OF ROUTE IDENTIFICATION\n\n");
        toReportFile.add("Processed FPLs have been retrieved from: '" + connection2.getMetaData().getURL()
                + "' connection.\n\n");
        toReportFile.add("Processed Routes can be found in: '" + sourceFileName + "' file.\n\n");
        toReportFile.add("Aeronautical Features for Route identification are from: '"
                + connection1.getMetaData().getURL() + "' connection.\n\n");
        toReportFile.add("Total number of Processed Routes: " + String.valueOf(numOfProcessedRoutes) + "\n\n");
        toReportFile
                .add("Total number of Recognized A-to-B Routes: " + String.valueOf(numOfRecognizedA2Broutes) + "\n\n");
        toReportFile
                .add("Total number of Recognized A-to-A Routes: " + String.valueOf(numOfRecognizedA2Aroutes) + "\n\n");
        toReportFile.add(
                "Total number of Unrecognized A-to-B Routes: " + String.valueOf(numOfUnRecognizedA2Broutes) + "\n\n");
        toReportFile.add(
                "Total number of Unrecognized A-to-A Routes: " + String.valueOf(numOfUnRecognizedA2Aroutes) + "\n\n");

        toReportFile.add("Total number of Not Found Aerodromes: " + String.valueOf(numOfNotFoundADHPs) + "\n\n");
        toReportFile.add("Total number of Not Found Navaids: " + String.valueOf(numOfNotFoundNavaids) + "\n\n");
        toReportFile.add("Total number of Not Found Waypoints: " + String.valueOf(numOfNotFoundWaypoints) + "\n\n");
        toReportFile.add("Total number of Not Found Point-type Features (Aerodromes + Navaids + Waypoints): "
                + String.valueOf(totalNumOfNotFoundPoints) + "\n\n");
        toReportFile.add("Total number of ATS Route Designators Not Found in DB: "
                + String.valueOf(numOfNotFoundATSroutes) + "\n\n");
        toReportFile
                .add("Total number of ATS Routes present in DB, which do Not connect valid surrounding Route Points: "
                        + String.valueOf(numOfDisconnectedATSroutes) + "\n\n");
        toReportFile.add(
                "Total number of Tokens with unrecognized format: " + String.valueOf(numOfIncorrectTokens) + "\n\n");

        toReportFile.add("\n===================================================\n\n");
        toReportFile.add("         LIST OF NOT FOUND AERODROMES ( " + String.valueOf(numOfNotFoundADHPs) + " )\n\n");
        for (String tempStr : setOfNotFoundADHPs) {
            toReportFile.add(tempStr + "\n");
        }

        toReportFile.add("\n===================================================\n\n");
        toReportFile.add("         LIST OF NOT FOUND NAVAIDS ( " + String.valueOf(numOfNotFoundNavaids) + " )\n\n");
        for (String tempStr : setOfNotFoundNavaids) {
            toReportFile.add(tempStr + "\n");
        }

        toReportFile.add("\n===================================================\n\n");
        toReportFile.add("         LIST OF NOT FOUND WAYPOINTS ( " + String.valueOf(numOfNotFoundWaypoints) + " )\n\n");
        for (String tempStr : setOfNotFoundWaypoints) {
            toReportFile.add(tempStr + "\n");
        }

        toReportFile.add("\n==============================================================\n\n");
        toReportFile.add("         LIST OF ATS ROUTE DESIGNATORS NOT FOUND IN DB ( "
                + String.valueOf(numOfNotFoundATSroutes) + " )\n\n");
        for (String tempStr : setOfNotFoundATSroutes) {
            toReportFile.add(tempStr + "\n");
        }

        toReportFile.add(
                "\n============================================================================================================\n\n");
        toReportFile
                .add("         LIST OF ATS ROUTES PRESENT IN DB, WHICH DO NOT CONNECT VALID SURROUNDING ROUTE POINTS ( "
                        + String.valueOf(numOfDisconnectedATSroutes) + " )\n\n");
        for (String tempStr : setOfDisconnectedATSroutes) {
            toReportFile.add(tempStr + "\n");
        }

        toReportFile.add("\n===================================================\n\n");
        toReportFile.add("         LIST OF INCORRECT TOKENS ( " + String.valueOf(numOfIncorrectTokens) + " )\n\n");
        for (String tempStr : setOfIncorrectTokens) {
            toReportFile.add(tempStr + "\n");
        }
        toReportFile.add("\n===================================================\n");

        writeRoutesIntoFile(toReportFile, destinationFileName);
    }

    private static LinkedList<String[]> getRouresFromFile(String fileName) {
        LinkedList<String[]> routes = new LinkedList<String[]>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));

            String line;
            int indexAfterEmptyLine = 0;
            String[] routeFields = new String[3];

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty()) {
                    indexAfterEmptyLine = 0;
                    continue;
                } else
                    indexAfterEmptyLine++;

                switch (indexAfterEmptyLine) {
                case 1:
                    routeFields = new String[3];
                    routeFields[0] = line;
                    break;
                case 2:
                    routeFields[1] = line;
                    break;
                case 3:
                    routeFields[2] = line;
                    routes.add(routeFields);
                    break;
                default:
                }
            }
            reader.close();
            return routes;
        } catch (Exception e) {
            System.err.format("Exception occurred trying to read '%s'.", fileName);
            e.printStackTrace();
            return null;
        }
    }

    private static LinkedList<String[]> getRouresFromDB(Connection connection) throws SQLException {

        AisDbDataRetriever dbRetriever = new AisDbDataRetriever(connection);

        LinkedList<String[]> dbRoutes = dbRetriever.getRoutes();

        if (dbRoutes == null || dbRoutes.isEmpty())
            return null;

        for (String[] route : dbRoutes) {
            if (route[0] != null)
                route[0] = route[0].trim();
            if (route[1] != null)
                route[1] = route[1].replaceAll("\\s+", " ").trim();
            if (route[2] != null)
                route[2] = route[2].trim();
        }

        LinkedList<String[]> outputRoutes = new LinkedList<String[]>();

        for (int i = 0; i < dbRoutes.size(); i++) {
            if (dbRoutes.get(i)[1].isEmpty() || dbRoutes.get(i)[0].length() != 4 || dbRoutes.get(i)[2].length() != 4
                    || dbRoutes.get(i)[0].equals("ZZZZ") || dbRoutes.get(i)[2].equals("ZZZZ"))
                continue;

            outputRoutes.add(dbRoutes.get(i));
        }

        return outputRoutes;
    }

    private static void writeRoutesIntoFile(Collection<String> routes, String fileName) {

        FileWriter fileWriter = null;
        BufferedWriter bw = null;
        try {
            File file = new File(fileName);

            if (!file.exists())
                file.createNewFile();

            fileWriter = new FileWriter(file);

            bw = new BufferedWriter(fileWriter);

            for (String inputRoute : routes) {
                bw.write(inputRoute);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bw.close();
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
