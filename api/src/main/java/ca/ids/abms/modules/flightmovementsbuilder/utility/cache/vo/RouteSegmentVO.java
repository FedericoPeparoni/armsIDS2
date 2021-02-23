package ca.ids.abms.modules.flightmovementsbuilder.utility.cache.vo;

import java.io.Serializable;

import com.vividsolutions.jts.geom.Geometry;

import ca.ids.abms.modules.routesegments.SegmentType;

public class RouteSegmentVO implements Serializable {

    private Integer segmentNumber;

    private Double segmentLength;

    private String segmentStartLabel;

    private String segmentEndLabel;

    private SegmentType segmentType;

    private Geometry location;

    private static final long serialVersionUID = 1L;

    public RouteSegmentVO() {

    }

    public Geometry getLocation() {
        return location;
    }

    public String getSegmentEndLabel() {
        return segmentEndLabel;
    }

    public Double getSegmentLength() {
        return segmentLength;
    }

    public Integer getSegmentNumber() {
        return segmentNumber;
    }

    public String getSegmentStartLabel() {
        return segmentStartLabel;
    }

    public SegmentType getSegmentType() {
        return segmentType;
    }

    public void setLocation(Geometry aLocation) {
        location = aLocation;
    }

    public void setSegmentEndLabel(String aSegmentEndLabel) {
        segmentEndLabel = aSegmentEndLabel;
    }

    public void setSegmentLength(Double aSegmentLength) {
        segmentLength = aSegmentLength;
    }

    public void setSegmentNumber(Integer aSegmentNumber) {
        segmentNumber = aSegmentNumber;
    }

    public void setSegmentStartLabel(String aSegmentStartLabel) {
        segmentStartLabel = aSegmentStartLabel;
    }

    public void setSegmentType(SegmentType aSegmentType) {
        segmentType = aSegmentType;
    }

}
