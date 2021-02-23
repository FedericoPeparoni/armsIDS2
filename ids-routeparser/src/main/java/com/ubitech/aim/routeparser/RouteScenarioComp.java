package com.ubitech.aim.routeparser;

import java.util.Comparator;

class RouteScenarioComp implements Comparator<RouteScenario> {

    public int compare(RouteScenario routeScenario0, RouteScenario routeScenario1) {
        if (routeScenario0.getCompletionStatus() == routeScenario1.getCompletionStatus()) {
            if (routeScenario0.getSyntaxCorrectnessStatus() == routeScenario1.getSyntaxCorrectnessStatus()) {
                if (routeScenario0.getGeoFeasibilityStatus() == routeScenario1.getGeoFeasibilityStatus()) {
                    if (routeScenario0.getNumOfFoundPoints() == routeScenario1.getNumOfFoundPoints()) {
                        if (routeScenario0.getNumOfNotFoundFeatures() == routeScenario1.getNumOfNotFoundFeatures()) {
                            if (routeScenario0.getRouteLength() == routeScenario1.getRouteLength()) {
                                return 0;
                            } else if (routeScenario0.getRouteLength() < routeScenario1.getRouteLength())
                                return -1;
                            else
                                return 1;
                        } else if (routeScenario0.getNumOfNotFoundFeatures() < routeScenario1
                                .getNumOfNotFoundFeatures())
                            return -1;
                        else
                            return 1;
                    } else if (routeScenario0.getNumOfFoundPoints() > routeScenario1.getNumOfFoundPoints())
                        return -1;
                    else
                        return 1;
                } else if (routeScenario0.getGeoFeasibilityStatus())
                    return -1;
                else
                    return 1;
            } else if (routeScenario0.getSyntaxCorrectnessStatus())
                return -1;
            else
                return 1;
        } else if (routeScenario0.getCompletionStatus())
            return -1;
        else
            return 1;
    }
}
