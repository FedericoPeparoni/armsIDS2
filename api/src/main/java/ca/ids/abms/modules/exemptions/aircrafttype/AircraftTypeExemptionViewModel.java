package ca.ids.abms.modules.exemptions.aircrafttype;

import ca.ids.abms.modules.util.models.VersionedViewModel;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class AircraftTypeExemptionViewModel extends VersionedViewModel {

    private Integer id;

    @NotNull
    private String aircraftType;

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
    private String flightNotes;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        AircraftTypeExemptionViewModel that = (AircraftTypeExemptionViewModel) o;

        return id != null ? id.equals(that.id) : that.id == null;

    }

    public Double getAerodromeFeesExempt() {
        return aerodromeFeesExempt;
    }

    public String getAircraftType() {
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

    public Double getDomesticPax() {
        return domesticPax;
    }

    public Double getInternationalPax() {
        return internationalPax;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public void setAerodromeFeesExempt(Double aAerodromeFeesExempt) {
        aerodromeFeesExempt = aAerodromeFeesExempt;
    }

    public void setAircraftType(String aAircraftType) {
        aircraftType = aAircraftType;
    }

    public void setApproachFeesExempt(Double aApproachFeesExempt) {
        approachFeesExempt = aApproachFeesExempt;
    }

    public void setEnrouteFeesExempt(Double aEnrouteFeesExempt) {
        enrouteFeesExempt = aEnrouteFeesExempt;
    }

    public void setFlightNotes(String aFlightNotes) {
        flightNotes = aFlightNotes;
    }

    public void setId(Integer aId) {
        id = aId;
    }

    public void setLateArrivalFeesExempt(Double aLateArrivalFeesExempt) {
        lateArrivalFeesExempt = aLateArrivalFeesExempt;
    }

    public void setLateDepartureFeesExempt(Double aLateDepartureFeesExempt) {
        lateDepartureFeesExempt = aLateDepartureFeesExempt;
    }

    public void setParkingFeesExempt(Double aParkingFeesExempt) {
        parkingFeesExempt = aParkingFeesExempt;
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
        return "AircraftTypeExemptionViewModel{" +
            "id=" + id +
            ", aircraftType='" + aircraftType + '\'' +
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
}
