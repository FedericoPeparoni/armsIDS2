package ca.ids.abms.modules.exemptions.aircraftflights;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import ca.ids.abms.config.db.SearchableText;
import ca.ids.abms.config.db.validators.UniqueKey;
import ca.ids.abms.modules.exemptions.ExemptionType;
import ca.ids.abms.modules.util.models.VersionedAuditedEntity;

@Entity
@Table(name = "exempt_aircraft_flights")
@UniqueKey(columnNames = { "aircraftRegistration", "flightId", "exemptionStartDate" })
public class AircraftFlightsExemption extends VersionedAuditedEntity implements ExemptionType {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @SearchableText
	private String aircraftRegistration;

    @SearchableText
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


	@SearchableText
    @Size(max = 255)
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

    public Double getExtendedHours() {
        return extendedHours;
    }

    public void setExtendedHours(Double extendedHours) {
        this.extendedHours = extendedHours;
    }
    
    public Double getUnifiedTax() {
		return unifiedTax;
	}

	public void setUnifiedTax(Double unifiedTax) {
		this.unifiedTax = unifiedTax;
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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        AircraftFlightsExemption that = (AircraftFlightsExemption) o;

        return id != null ? id.equals(that.id) : that.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "AircraftFlightsExemption{" +
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
        return extendedHours;
    }

    @Override
    public Double unifiedTaxExemption() {
        return unifiedTax;
    }
    
    @Override
    public String flightNoteChargeExemption() {
        return flightNotes;
    }
}
