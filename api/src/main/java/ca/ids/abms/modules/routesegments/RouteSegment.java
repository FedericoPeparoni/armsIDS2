package ca.ids.abms.modules.routesegments;

import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.util.models.AuditedEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vividsolutions.jts.geom.Geometry;

import javax.persistence.*;
import java.io.Serializable;

/**
 * The flight movements also has a related sub-table containing the segment points.
 * A route segment has attributes which include the following.
 *
 * Created by c.talpa on 07/02/2017.
 */
@Entity
@Table(name="route_segments")
public class RouteSegment extends AuditedEntity implements Serializable{

    private static final long serialVersionUID = 1L;

	@Id
    @Column(name = "id", unique = true, nullable = false, precision = 10, scale = 0)
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_movement")
    private FlightMovement flightMovement;

    @Column(name="segment_type")
    @Enumerated(EnumType.STRING)
    private SegmentType segmentType;

    @Column(name="segment_number")
    private Integer segmentNumber;

    @Column(name="location")
    @JsonIgnore
    private Geometry location;

    @Column(name="segment_start_label")
    private String segmentStartLabel;

    @Column(name="segment_end_label")
    private String segmentEndLabel;

    @Column(name="segment_length")
    private Double segmentLength;

    @Column(name="segment_cost")
    private Double segmentCost;

    @Column(name ="flight_level")
    private Double flightLevel;
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public FlightMovement getFlightMovement() {
        return flightMovement;
    }

    public void setFlightMovement(FlightMovement flightMovement) {
        this.flightMovement = flightMovement;
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

    public Geometry getLocation() {
        return location;
    }

    public void setLocation(Geometry location) {
        this.location = location;
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

    public Double getFlightLevel() {
        return flightLevel;
    }

    public void setFlightLevel(Double flightLevel) {
        this.flightLevel = flightLevel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RouteSegment that = (RouteSegment) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (segmentType != that.segmentType) return false;
        if (segmentNumber != null ? !segmentNumber.equals(that.segmentNumber) : that.segmentNumber != null)
            return false;
        if (location != null ? !location.equals(that.location) : that.location != null) return false;
        if (segmentStartLabel != null ? !segmentStartLabel.equals(that.segmentStartLabel) : that.segmentStartLabel != null)
            return false;
        if (segmentEndLabel != null ? !segmentEndLabel.equals(that.segmentEndLabel) : that.segmentEndLabel != null)
            return false;
        if (segmentLength != null ? !segmentLength.equals(that.segmentLength) : that.segmentLength != null)
            return false;
        return segmentCost != null ? segmentCost.equals(that.segmentCost) : that.segmentCost == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (segmentType != null ? segmentType.hashCode() : 0);
        result = 31 * result + (segmentNumber != null ? segmentNumber.hashCode() : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        result = 31 * result + (segmentStartLabel != null ? segmentStartLabel.hashCode() : 0);
        result = 31 * result + (segmentEndLabel != null ? segmentEndLabel.hashCode() : 0);
        result = 31 * result + (segmentLength != null ? segmentLength.hashCode() : 0);
        result = 31 * result + (segmentCost != null ? segmentCost.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "RouteSegment{" +
            "id=" + id +
            ", segmentType=" + segmentType +
            ", segmentNumber=" + segmentNumber +
            ", location=" + location +
            ", segmentStartLabel='" + segmentStartLabel + '\'' +
            ", segmentEndLabel='" + segmentEndLabel + '\'' +
            ", segmentLenght=" + segmentLength +
            ", segmentCost=" + segmentCost +
            '}';
    }

    /**
     * Copy route segment with new segment type.
     */
    public RouteSegment copy(final SegmentType segmentType) {
        RouteSegment result = new RouteSegment();

        result.setSegmentType(segmentType);

        result.setFlightMovement(this.flightMovement);
        result.setSegmentNumber(this.segmentNumber);
        result.setLocation(this.location);
        result.setSegmentStartLabel(this.segmentStartLabel);
        result.setSegmentEndLabel(this.segmentEndLabel);
        result.setSegmentLength(this.segmentLength);
        result.setSegmentCost(this.segmentCost);

        return result;
    }
}
