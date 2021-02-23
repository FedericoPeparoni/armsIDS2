package ca.ids.abms.modules.flightmovementsbuilder.utility.cache.vo;

import java.io.Serializable;
import java.util.List;

import com.vividsolutions.jts.geom.Geometry;

public class RouteCacheVO implements Serializable {

    private Geometry route;

    private Double distance;

    private List<RouteSegmentVO> routeSegmentList;

    private transient boolean valid = false;

    private transient String destAd;

    private static final long serialVersionUID = 1L;

    public RouteCacheVO() {

    }

    public Double getDistance() {
        return distance;
    }

    public Geometry getRoute() {
        return route;
    }

    public List<RouteSegmentVO> getRouteSegmentList() {
        return routeSegmentList;
    }

    public void setDistance(Double aDistance) {
        distance = aDistance;
    }

    public void setRoute(Geometry aRoute) {
        route = aRoute;
    }

    public void setRouteSegmentList(List<RouteSegmentVO> aRouteSegmentList) {
        routeSegmentList = aRouteSegmentList;
    }

    @Override
    public String toString() {
        return "RouteCacheVO [route=" + route + ", distance=" + distance + ", routeSegmentList=" + routeSegmentList
                + "]";
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getDestAd() {
        return destAd;
    }

    public void setDestAd(String destAd) {
        this.destAd = destAd;
    }
}
