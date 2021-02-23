package ca.ids.abms.modules.routesegments;

import ca.ids.abms.modules.util.models.AuditedEntity;

import javax.persistence.Id;
import java.io.Serializable;

/**
 * The flight movements also has a related sub-table containing the segment points.
 * A route segment has attributes which include the following.
 *
 * Created by c.talpa on 07/02/2017.
 */
public class RouteSegmentViewModel extends AuditedEntity implements Serializable{

    private static final long serialVersionUID = 1L;

	@Id
    private Integer id;

    private Integer flightRecordId;

    private SegmentType segmentType;

    private Integer segmentNumber;

    private String segmentStartLabel;

    private String segmentEndLabel;

    private Double segmentLength;

    private Double segmentCost;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFlightRecordId() {
        return flightRecordId;
    }

    public void setFlightRecordId(Integer flightRecordId) {
        this.flightRecordId = flightRecordId;
    }

    public SegmentType getSegmentType() {
        return segmentType;
    }

    public void setSegmentType(SegmentType segmentType) {
        this.segmentType = segmentType;
    }

    public Integer getSegmentNumber() {
        return segmentNumber;
    }

    public void setSegmentNumber(Integer segmentNumber) {
        this.segmentNumber = segmentNumber;
    }


    public String getSegmentStartLabel() {
        return segmentStartLabel;
    }

    public void setSegmentStartLabel(String segmentStartLabel) {
        this.segmentStartLabel = segmentStartLabel;
    }

    public Double getSegmentLength() {
        return segmentLength;
    }

    public void setSegmentLength(Double segmentLength) {
        this.segmentLength = segmentLength;
    }

    public Double getSegmentCost() {
        return segmentCost;
    }

    public void setSegmentCost(Double segmentCost) {
        this.segmentCost = segmentCost;
    }

    public String getSegmentEndLabel() {
        return segmentEndLabel;
    }

    public void setSegmentEndLabel(String segmentEndLabel) {
        this.segmentEndLabel = segmentEndLabel;
    }

    @Override
    public String toString() {
        return "RouteSegment{" +
            "id=" + id +
            ", flightRecordId=" + flightRecordId +
            ", segmentType=" + segmentType +
            ", segmentNumber=" + segmentNumber +
            ", segmentStartLabel='" + segmentStartLabel + '\'' +
            ", segmentEndLabel='" + segmentEndLabel + '\'' +
            ", segmentLenght=" + segmentLength +
            ", segmentCost=" + segmentCost +
            '}';
    }
}
