package com.ubitech.aim.routeparser;

import java.sql.SQLException;
import java.util.Comparator;

class RouteDraftComp implements Comparator<RouteDraft> {

    public int compare(RouteDraft routeDraft0, RouteDraft routeDraft1) {
        double actualVSestimatedLength0;
        double actualVSestimatedLength1;

        try {
            actualVSestimatedLength0 = routeDraft0.longerThanEstimatedLength();
            actualVSestimatedLength1 = routeDraft1.longerThanEstimatedLength();

            if (routeDraft0.isLengthWithinTheLimit() == routeDraft1.isLengthWithinTheLimit()) {
                if (routeDraft0.numOfCrediblePoints() == routeDraft1.numOfCrediblePoints()) {
                    if (Math.abs(actualVSestimatedLength0) == Math.abs(actualVSestimatedLength1)) {
                        if (routeDraft0.numOfRoutePoints() == routeDraft1.numOfRoutePoints()) {
                            return 0;
                        } else if (routeDraft0.numOfRoutePoints() > routeDraft1.numOfRoutePoints()) {
                            return -1;
                        } else
                            return 1;
                    } else if (Math.abs(actualVSestimatedLength0) < Math.abs(actualVSestimatedLength1)) {
                        return -1;
                    } else
                        return 1;
                } else if (routeDraft0.numOfCrediblePoints() > routeDraft1.numOfCrediblePoints()) {
                    return -1;
                } else
                    return 1;
            } else if (routeDraft0.isLengthWithinTheLimit())
                return -1;
            else
                return 1;

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0;
    }
}
