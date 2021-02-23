package ca.ids.abms.modules.exemptions.flightstatus;

import ca.ids.abms.config.db.SearchableText;
import ca.ids.abms.config.db.validators.UniqueKey;
import ca.ids.abms.modules.common.enumerators.FlightItemType;
import ca.ids.abms.modules.common.enumerators.FlightItemTypeConverter;
import ca.ids.abms.modules.exemptions.ExemptionType;
import ca.ids.abms.modules.util.models.VersionedAuditedEntity;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@UniqueKey(columnNames = { "flightItemType", "flightItemValue" })
public class ExemptFlightStatus extends VersionedAuditedEntity implements ExemptionType {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Basic
    @Convert( converter=FlightItemTypeConverter.class )
    @SearchableText
    private FlightItemType flightItemType;

    @NotNull
    @Size(max = 100)
    @SearchableText
    private String flightItemValue;

    @NotNull
    @Max(100)
    @Min(0)
    private Double enrouteFeesAreExempt;

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
    private Double parkingFeesAreExempt;

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

    @Size(max = 255)
    @SearchableText
    private String  flightNotes;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public FlightItemType getFlightItemType() {
        return flightItemType;
    }

    public void setFlightItemType(FlightItemType flightItemType) {
        this.flightItemType = flightItemType;
    }

    public String getFlightItemValue() {
        return flightItemValue;
    }

    public void setFlightItemValue(String flightItemValue) {
        this.flightItemValue = flightItemValue;
    }

    public Double getEnrouteFeesAreExempt() {
        return enrouteFeesAreExempt;
    }

    public void setEnrouteFeesAreExempt(Double enrouteFeesAreExempt) {
        this.enrouteFeesAreExempt = enrouteFeesAreExempt;
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

    public Double getParkingFeesAreExempt() {
        return parkingFeesAreExempt;
    }

    public void setParkingFeesAreExempt(Double parkingFeesAreExempt) {
        this.parkingFeesAreExempt = parkingFeesAreExempt;
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

    public String getFlightNotes() {
        return flightNotes;
    }

    public void setFlightNotes(String flightNotes) {
        this.flightNotes = flightNotes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExemptFlightStatus that = (ExemptFlightStatus) o;

        return id != null ? id.equals(that.id) : that.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ExemptFlightStatus{" +
            "id=" + id +
            ", flightItemType=" + flightItemType +
            ", flightItemValue='" + flightItemValue + '\'' +
            ", enrouteFeesAreExempt=" + enrouteFeesAreExempt +
            ", approachFeesExempt=" + approachFeesExempt +
            ", aerodromeFeesExempt=" + aerodromeFeesExempt +
            ", lateArrivalFeesExempt=" + lateArrivalFeesExempt +
            ", lateDepartureFeesExempt=" + lateDepartureFeesExempt +
            ", parkingFeesAreExempt=" + parkingFeesAreExempt +
            ", domesticPax=" + domesticPax +
            ", internationalPax=" + internationalPax +
            ", extendedHours=" + extendedHours +
            ", flightNotes='" + flightNotes + '\'' +
            '}';
    }

    @Override
    public Double enrouteChargeExemption() {
        return enrouteFeesAreExempt;
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
        return parkingFeesAreExempt;
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
    public String flightNoteChargeExemption() {
        return flightNotes;
    }
}
