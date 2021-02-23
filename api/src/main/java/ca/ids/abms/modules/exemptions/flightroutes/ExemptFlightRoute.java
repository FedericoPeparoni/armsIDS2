package ca.ids.abms.modules.exemptions.flightroutes;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import ca.ids.abms.config.db.SearchableText;
import ca.ids.abms.config.db.validators.UniqueKey;
import ca.ids.abms.modules.exemptions.ExemptionType;
import ca.ids.abms.modules.util.models.VersionedAuditedEntity;

@Entity
@UniqueKey(columnNames = { "departureAerodrome", "destinationAerodrome", "exemptRouteFloor", "exemptRouteCeiling"})
public class ExemptFlightRoute extends VersionedAuditedEntity implements ExemptionType {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Size(max = 100)
    @SearchableText
    private String departureAerodrome;

    @NotNull
    @Size(max = 100)
    @SearchableText
    private String destinationAerodrome;

    @NotNull
    private Boolean exemptionInEitherDirection;

    @NotNull
    @Max(100)
    @Min(0)
    private Double enrouteFeesAreExempt;

    @NotNull
    @Max(100)
    @Min(0)
    private Double lateArrivalFeesAreExempt;

    @NotNull
    @Max(100)
    @Min(0)
    private Double lateDepartureFeesAreExempt;

    @NotNull
    @Max(100)
    @Min(0)
    private Double parkingFeesAreExempt;

    @NotNull
    @Max(100)
    @Min(0)
    private Double approachFeesAreExempt;

    @NotNull
    @Max(100)
    @Min(0)
    private Double aerodromeFeesAreExempt;

    @NotNull
    @Max(100)
    @Min(0)
    private Double extendedHours;

    @NotNull
    @Max(100)
    @Min(0)
    private Double domesticPax;

    @NotNull
    @Max(100)
    @Min(0)
    private Double internationalPax;

    @NotNull
    @Size(max = 255)
    @SearchableText
    private String flightNotes;

    @NotNull
    @Max(999)
    @Min(0)
    private Double exemptRouteFloor;
    
    @NotNull
    @Max(999)
    @Min(0)
    private Double exemptRouteCeiling;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDepartureAerodrome() {
        return departureAerodrome;
    }
    
    public void setDepartureAerodrome(String departureAerodrome) {
        this.departureAerodrome = departureAerodrome;
    }

    public String getDestinationAerodrome() {
        return destinationAerodrome;
    }

    public void setDestinationAerodrome(String destinationAerodrome) {
        this.destinationAerodrome = destinationAerodrome;
    }

    public Boolean getExemptionInEitherDirection() {
        return exemptionInEitherDirection;
    }

    public void setExemptionInEitherDirection(Boolean exemptionInEitherDirection) {
        this.exemptionInEitherDirection = exemptionInEitherDirection;
    }

    public Double getEnrouteFeesAreExempt() {
        return enrouteFeesAreExempt;
    }

    public void setEnrouteFeesAreExempt(Double enrouteFeesAreExempt) {
        this.enrouteFeesAreExempt = enrouteFeesAreExempt;
    }

    public Double getLateArrivalFeesAreExempt() {
        return lateArrivalFeesAreExempt;
    }

    public void setLateArrivalFeesAreExempt(Double lateArrivalFeesAreExempt) {
        this.lateArrivalFeesAreExempt = lateArrivalFeesAreExempt;
    }

    public Double getLateDepartureFeesAreExempt() {
        return lateDepartureFeesAreExempt;
    }

    public void setLateDepartureFeesAreExempt(Double lateDepartureFeesAreExempt) {
        this.lateDepartureFeesAreExempt = lateDepartureFeesAreExempt;
    }

    public Double getParkingFeesAreExempt() {
        return parkingFeesAreExempt;
    }

    public void setParkingFeesAreExempt(Double parkingFeesAreExempt) {
        this.parkingFeesAreExempt = parkingFeesAreExempt;
    }

    public Double getApproachFeesAreExempt() {
        return approachFeesAreExempt;
    }

    public void setApproachFeesAreExempt(Double approachFeesAreExempt) {
        this.approachFeesAreExempt = approachFeesAreExempt;
    }

    public Double getAerodromeFeesAreExempt() {
        return aerodromeFeesAreExempt;
    }

    public void setAerodromeFeesAreExempt(Double aerodromeFeesAreExempt) {
        this.aerodromeFeesAreExempt = aerodromeFeesAreExempt;
    }

    public Double getExtendedHours() {
        return extendedHours;
    }

    public void setExtendedHours(Double extendedHours) {
        this.extendedHours = extendedHours;
    }

    public Double getDomesticPax() {
        return domesticPax;
    }

    public void setDomesticPax(Double domesticPax) {
        this.domesticPax = domesticPax;
    }

    public Double getInternationalPax() {
        return internationalPax;
    }

    public void setInternationalPax(Double internationalPax) {
        this.internationalPax = internationalPax;
    }

    public String getFlightNotes() {
        return flightNotes;
    }

    public void setFlightNotes(String flightNotes) {
        this.flightNotes = flightNotes;
    }

    public Double getExemptRouteFloor() {
        return exemptRouteFloor;
    }

    public void setExemptRouteFloor(Double exemptRouteFloor) {
        this.exemptRouteFloor = exemptRouteFloor;
    }

    public Double getExemptRouteCeiling() {
        return exemptRouteCeiling;
    }

    public void setExemptRouteCeiling(Double exemptRouteCeiling) {
        this.exemptRouteCeiling = exemptRouteCeiling;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ExemptFlightRoute that = (ExemptFlightRoute) o;

        return id != null ? id.equals(that.id) : that.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ExemptFlightRoute{" +
            "id=" + id +
            ", departureAerodrome='" + departureAerodrome + '\'' +
            ", destinationAerodrome='" + destinationAerodrome + '\'' +
            ", exemptionInEitherDirection=" + exemptionInEitherDirection +
            ", enrouteFeesAreExempt=" + enrouteFeesAreExempt +
            ", lateArrivalFeesAreExempt=" + lateArrivalFeesAreExempt +
            ", lateDepartureFeesAreExempt=" + lateDepartureFeesAreExempt +
            ", parkingFeesAreExempt=" + parkingFeesAreExempt +
            ", approachFeesAreExempt=" + approachFeesAreExempt +
            ", aerodromeFeesAreExempt=" + aerodromeFeesAreExempt +
            ", extendedHours=" + extendedHours +
            ", domesticPax=" + domesticPax +
            ", internationalPax=" + internationalPax +
            ", exemptRouteFloor=" + exemptRouteFloor +
            ", exemptRouteCeiling=" + exemptRouteCeiling +
            ", flightNotes='" + flightNotes + '\'' +
            '}';
    }

    @Override
    public Double enrouteChargeExemption() {
        return enrouteFeesAreExempt;
    }

    @Override
    public Double lateArrivalChargeExemption() {
        return lateArrivalFeesAreExempt;
    }

    @Override
    public Double lateDepartureChargeExemption() {
        return lateDepartureFeesAreExempt;
    }

    @Override
    public Double parkingChargeExemption() {
        return parkingFeesAreExempt;
    }

    @Override
    public Double approachChargeExemption() {
        return approachFeesAreExempt;
    }

    @Override
    public Double aerodromeChargeExemption() {
        return aerodromeFeesAreExempt;
    }

    @Override
    public Double domesticPaxChargeExemption() {
        return domesticPax;
    }

    @Override
    public Double internationalPaxChargeExemption() {
        return internationalPax;
    }

    @Override
    public Double extendedHoursSurchargeExemption() {
        return extendedHours;
    }

    @Override
    public String flightNoteChargeExemption() {
        return flightNotes;
    }  
}
