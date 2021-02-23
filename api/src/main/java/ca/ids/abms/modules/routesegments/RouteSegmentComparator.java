package ca.ids.abms.modules.routesegments;

import java.util.Comparator;

/**
 * Created by c.talpa on 10/03/2017.
 */
public class RouteSegmentComparator implements Comparator<RouteSegment> {

    @Override
    public int compare(RouteSegment o1, RouteSegment o2) {
        return o1.getSegmentNumber().compareTo(o2.getSegmentNumber());
    }
}
