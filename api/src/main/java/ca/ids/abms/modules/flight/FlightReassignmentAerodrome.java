package ca.ids.abms.modules.flight;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import ca.ids.abms.modules.util.models.AuditedEntity;

@Entity
public class FlightReassignmentAerodrome extends AuditedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private FlightReassignment flightReassignment;

    @NotNull
    private String aerodromeIdentifier;

    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        FlightReassignmentAerodrome item = (FlightReassignmentAerodrome) o;

        return id != null ? id.equals(item.id) : item.id == null;
    }

    public String getAerodromeIdentifier() {
        return aerodromeIdentifier;
    }

    public FlightReassignment getFlightReassignment() {
        return flightReassignment;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public void setAerodromeIdentifier(String aAerodromeIdentifier) {
        aerodromeIdentifier = aAerodromeIdentifier;
    }

    public void setFlightReassignment(FlightReassignment aFlightReassignment) {
        flightReassignment = aFlightReassignment;
    }

    public void setId(Integer aId) {
        id = aId;
    }

    @Override
    public String toString() {
        return "RepositioningAssignedAerodromeCluster [id=" + id + ", flightReassignment="
                + flightReassignment + ", aerodromeIdentifier=" + aerodromeIdentifier + "]";
    }
}
