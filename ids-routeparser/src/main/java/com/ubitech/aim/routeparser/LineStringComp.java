package com.ubitech.aim.routeparser;

import java.util.Comparator;

class LineStringComp implements Comparator<LineString> {

    public int compare(LineString firstLine, LineString secondLine) {
        if (firstLine.connectedByStartPoint == secondLine.connectedByStartPoint) {
            if (firstLine.connectedByEndPoint == secondLine.connectedByEndPoint) {
                if (firstLine.xyOffset == secondLine.xyOffset)
                    return 0;
                else if (firstLine.xyOffset < secondLine.xyOffset)
                    return -1;
                else
                    return 1;
            } else if (secondLine.connectedByEndPoint)
                return -1;
            else
                return 1;
        } else if (firstLine.connectedByStartPoint)
            return -1;
        else
            return 1;
    }
}
