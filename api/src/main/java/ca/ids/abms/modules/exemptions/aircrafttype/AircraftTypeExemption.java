package ca.ids.abms.modules.exemptions.aircrafttype;

import ca.ids.abms.config.db.SearchableEntity;
import ca.ids.abms.config.db.SearchableText;
import ca.ids.abms.modules.aircraft.AircraftType;
import ca.ids.abms.modules.exemptions.ExemptionType;
import ca.ids.abms.modules.util.models.VersionedAuditedEntity;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
public class AircraftTypeExemption extends VersionedAuditedEntity implements ExemptionType {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @JoinColumn(name = "aircraft_type", referencedColumnName = "aircraftType")
    @OneToOne
    @SearchableEntity
    private AircraftType aircraftType;

    @NotNull
    @Max(100)
    @Min(0)
    private Double enrouteFeesExempt;

    @NotNull
    @Max(100)
    @Min(0)
    private Double approachFeesExempt;

    @NotNull
    @Max(100)
    @Min(0)
    private Double aerodromeFeesExempt;

    @NotNull
    @Max(100)
    @Min(0)
    private Double lateArrivalFeesExempt;

    @NotNull
    @Max(100)
    @Min(0)
    private Double lateDepartureFeesExempt;

    @NotNull
    @Max(100)
    @Min(0)
    private Double parkingFeesExempt;

    @NotNull
    @Max(100)
    @Min(0)
    private Double domesticPax;

    @NotNull
    @Max(100)
    @Min(0)
    private Double internationalPax;

    @NotNull
    @Max(100)
    @Min(0)
    private Double extendedHoursFeesExempt;

    @NotNull
    @Size(max = 255)
    @SearchableText
    private String flightNotes;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        AircraftTypeExemption that = (AircraftTypeExemption) o;

        return id != null ? id.equals(that.id) : that.id == null;

    }

    public Double getAerodromeFeesExempt() {
        return aerodromeFeesExempt;
    }

    public AircraftType getAircraftType() {
        return aircraftType;
    }

    public Double getApproachFeesExempt() {
        return approachFeesExempt;
    }

    public Double getEnrouteFeesExempt() {
        return enrouteFeesExempt;
    }

    public String getFlightNotes() {
        return flightNotes;
    }

    public Integer getId() {
        return id;
    }

    public Double getLateArrivalFeesExempt() {
        return lateArrivalFeesExempt;
    }

    public Double getLateDepartureFeesExempt() {
        return lateDepartureFeesExempt;
    }

    public Double getParkingFeesExempt() {
        return parkingFeesExempt;
    }

    public Double getInternationalPax() {
        return internationalPax;
    }

    public Double getDomesticPax() {
        return domesticPax;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public void setAerodromeFeesExempt(Double aerodromeFeesExempt) {
        this.aerodromeFeesExempt = aerodromeFeesExempt;
    }

    public void setAircraftType(AircraftType aircraftType) {
        this.aircraftType = aircraftType;
    }

    public void setApproachFeesExempt(Double approachFeesExempt) {
        this.approachFeesExempt = approachFeesExempt;
    }

    public void setEnrouteFeesExempt(Double enrouteFeesExempt) {
        this.enrouteFeesExempt = enrouteFeesExempt;
    }

    public void setFlightNotes(String aFlightNotes) {
        flightNotes = aFlightNotes;
    }

    public void setId(Integer aId) {
        id = aId;
    }

    public void setLateArrivalFeesExempt(Double lateArrivalFeesExempt) {
        this.lateArrivalFeesExempt = lateArrivalFeesExempt;
    }

    public void setLateDepartureFeesExempt(Double lateDepartureFeesExempt) {
        this.lateDepartureFeesExempt = lateDepartureFeesExempt;
    }

    public void setParkingFeesExempt(Double parkingFeesExempt) {
        this.parkingFeesExempt = parkingFeesExempt;
    }

    public void setDomesticPax(Double domesticPax) {
        this.domesticPax = domesticPax;
    }

    public void setInternationalPax(Double internationalPax) {
        this.internationalPax = internationalPax;
    }

    public Double getExtendedHoursFeesExempt() {
        return extendedHoursFeesExempt;
    }

    public void setExtendedHoursFeesExempt(Double extendedHoursFeesExempt) {
        this.extendedHoursFeesExempt = extendedHoursFeesExempt;
    }

    @Override
    public String toString() {
        return "AircraftTypeExemption{" +
            "id=" + id +
            ", aircraftType=" + aircraftType +
            ", enrouteFeesExempt=" + enrouteFeesExempt +
            ", approachFeesExempt=" + approachFeesExempt +
            ", aerodromeFeesExempt=" + aerodromeFeesExempt +
            ", lateArrivalFeesExempt=" + lateArrivalFeesExempt +
            ", lateDepartureFeesExempt=" + lateDepartureFeesExempt +
            ", parkingFeesExempt=" + parkingFeesExempt +
            ", domesticPax=" + domesticPax +
            ", internationalPax=" + internationalPax +
            ", extendedHoursFeesExempt=" + extendedHoursFeesExempt +
            ", flightNotes='" + flightNotes + '\'' +
            '}';
    }

    @Override
    public Double enrouteChargeExemption() {
        return enrouteFeesExempt;
    }

    @Override
    public Double lateArrivalChargeExemption() {
        return lateArrivalFeesExempt;
    }

    @Override
    public Double lateDepartureChargeExemption() {
        return lateDepartureFeesExempt;
    }

    @Override
    public Double parkingChargeExemption() {
        return parkingFeesExempt;
    }

    @Override
    public Double approachChargeExemption() {
        return approachFeesExempt;
    }

    @Override
    public Double aerodromeChargeExemption() {
        return aerodromeFeesExempt;
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
        return extendedHoursFeesExempt;
    }

    @Override
    public String flightNoteChargeExemption() {
        return flightNotes;
    }
}
