package ca.ids.abms.modules.exemptions.aircraftflights;

import java.time.LocalDateTime;

import ca.ids.abms.modules.util.models.VersionedViewModel;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class AircraftFlightsExemptionViewModel extends VersionedViewModel {

    private Integer id;

	private String aircraftRegistration;
	
	private String flightId;

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
    private Double extendedHours;
    
    @NotNull
    @Max(100)
    @Min(0)
    private Double unifiedTax;


	@NotNull
    private String flightNotes;

    @NotNull
    private LocalDateTime exemptionStartDate;

    @NotNull
    private LocalDateTime exemptionEndDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAircraftRegistration() {
        return aircraftRegistration;
    }

    public void setAircraftRegistration(String aircraftRegistration) {
        this.aircraftRegistration = aircraftRegistration;
    }

    public String getFlightId() {
        return flightId;
    }

    public void setFlightId(String flightId) {
        this.flightId = flightId;
    }

    public Double getEnrouteFeesExempt() {
        return enrouteFeesExempt;
    }

    public void setEnrouteFeesExempt(Double enrouteFeesExempt) {
        this.enrouteFeesExempt = enrouteFeesExempt;
    }

    public Double getApproachFeesExempt() {
        return approachFeesExempt;
    }

    public void setApproachFeesExempt(Double approachFeesExempt) {
        this.approachFeesExempt = approachFeesExempt;
    }

    public Double getAerodromeFeesExempt() {
        return aerodromeFeesExempt;
    }

    public void setAerodromeFeesExempt(Double aerodromeFeesExempt) {
        this.aerodromeFeesExempt = aerodromeFeesExempt;
    }

    public Double getLateArrivalFeesExempt() {
        return lateArrivalFeesExempt;
    }

    public void setLateArrivalFeesExempt(Double lateArrivalFeesExempt) {
        this.lateArrivalFeesExempt = lateArrivalFeesExempt;
    }

    public Double getLateDepartureFeesExempt() {
        return lateDepartureFeesExempt;
    }

    public void setLateDepartureFeesExempt(Double lateDepartureFeesExempt) {
        this.lateDepartureFeesExempt = lateDepartureFeesExempt;
    }

    public Double getParkingFeesExempt() {
        return parkingFeesExempt;
    }

    public void setParkingFeesExempt(Double parkingFeesExempt) {
        this.parkingFeesExempt = parkingFeesExempt;
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

    public LocalDateTime getExemptionStartDate() {
        return exemptionStartDate;
    }

    public void setExemptionStartDate(LocalDateTime exemptionStartDate) {
        this.exemptionStartDate = exemptionStartDate;
    }

    public LocalDateTime getExemptionEndDate() {
        return exemptionEndDate;
    }

    public void setExemptionEndDate(LocalDateTime exemptionEndDate) {
        this.exemptionEndDate = exemptionEndDate;
    }
    
    public Double getUnifiedTax() {
		return unifiedTax;
	}

	public void setUnifiedTax(Double unifiedTax) {
		this.unifiedTax = unifiedTax;
	}

    public Double getExtendedHours() {
        return extendedHours;
    }

    public void setExtendedHours(Double extendedHours) {
        this.extendedHours = extendedHours;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        AircraftFlightsExemptionViewModel that = (AircraftFlightsExemptionViewModel) o;

        return id != null ? id.equals(that.id) : that.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "AircraftFlightsExemptionViewModel{" +
            "id=" + id +
            ", aircraftRegistration='" + aircraftRegistration + '\'' +
            ", flightId='" + flightId + '\'' +
            ", enrouteFeesExempt=" + enrouteFeesExempt +
            ", approachFeesExempt=" + approachFeesExempt +
            ", aerodromeFeesExempt=" + aerodromeFeesExempt +
            ", lateArrivalFeesExempt=" + lateArrivalFeesExempt +
            ", lateDepartureFeesExempt=" + lateDepartureFeesExempt +
            ", parkingFeesExempt=" + parkingFeesExempt +
            ", domesticPax=" + domesticPax +
            ", internationalPax=" + internationalPax +
            ", extendedHours=" + extendedHours +
            ", flightNotes='" + flightNotes + '\'' +
            ", exemptionStartDate=" + exemptionStartDate +
            ", exemptionEndDate=" + exemptionEndDate +
            '}';
    }
}
