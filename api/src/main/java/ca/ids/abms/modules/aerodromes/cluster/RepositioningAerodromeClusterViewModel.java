package ca.ids.abms.modules.aerodromes.cluster;

import ca.ids.abms.modules.util.models.VersionedViewModel;

import java.util.Collection;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class RepositioningAerodromeClusterViewModel extends VersionedViewModel {

    private Integer id;

    @NotNull
    @Size(max = 255)
    private String repositioningAerodromeClusterName;

    @NotNull
    @Max(100)
    @Min(0)
    private Double enrouteFeesAreExempt;

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
    @Size(max = 255)
    private String flightNotes;

    private Collection<String> aerodromeIdentifiers;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRepositioningAerodromeClusterName() {
        return repositioningAerodromeClusterName;
    }

    public void setRepositioningAerodromeClusterName(String repositioningAerodromeClusterName) {
        this.repositioningAerodromeClusterName = repositioningAerodromeClusterName;
    }

    public Double getEnrouteFeesAreExempt() {
        return enrouteFeesAreExempt;
    }

    public void setEnrouteFeesAreExempt(Double enrouteFeesAreExempt) {
        this.enrouteFeesAreExempt = enrouteFeesAreExempt;
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

    public Collection<String> getAerodromeIdentifiers() {
        return aerodromeIdentifiers;
    }

    public void setAerodromeIdentifiers(Collection<String> aerodromeIdentifiers) {
        this.aerodromeIdentifiers = aerodromeIdentifiers;
    }

    @Override
    public String toString() {
        return "RepositioningAerodromeClusterViewModel{" +
            "id=" + id +
            ", repositioningAerodromeClusterName='" + repositioningAerodromeClusterName + '\'' +
            ", enrouteFeesAreExempt=" + enrouteFeesAreExempt +
            ", approachFeesAreExempt=" + approachFeesAreExempt +
            ", aerodromeFeesAreExempt=" + aerodromeFeesAreExempt +
            ", lateArrivalFeesAreExempt=" + lateArrivalFeesAreExempt +
            ", lateDepartureFeesAreExempt=" + lateDepartureFeesAreExempt +
            ", parkingFeesAreExempt=" + parkingFeesAreExempt +
            ", domesticPax=" + domesticPax +
            ", internationalPax=" + internationalPax +
            ", extendedHours=" + extendedHours +
            ", flightNotes='" + flightNotes + '\'' +
            ", aerodromeIdentifiers=" + aerodromeIdentifiers +
            '}';
    }
}
