package ca.ids.abms.modules.exemptions.flightroutes;

import ca.ids.abms.modules.util.models.VersionedViewModel;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ExemptFlightRouteViewModel extends VersionedViewModel {

    private Integer id;

    @NotNull
    @Size(max = 100)
    private String departureAerodrome;

    @NotNull
    @Size(max = 100)
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
    @NotNull
    private String  flightNotes;

    @Min(0)
    private Double exemptRouteFloor;
    
    @Min(0)
    private Double exemptRouteCeiling;
    
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
    public String toString() {
        return "ExemptFlightRouteViewModel{" +
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
            ", domesticPax=" + domesticPax +
            ", internationalPax=" + internationalPax +
            ", extendedHours=" + extendedHours +
            ", flightNotes='" + flightNotes + 
            ", exemptRouteCeiling =" + exemptRouteCeiling +
            ", exemptRouteFloor =" + exemptRouteFloor +'\'' +
            '}';
    }
}
