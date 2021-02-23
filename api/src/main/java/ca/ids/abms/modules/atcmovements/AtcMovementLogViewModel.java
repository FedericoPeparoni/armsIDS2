package ca.ids.abms.modules.atcmovements;

import ca.ids.abms.modules.common.enumerators.FlightCategory;
import ca.ids.abms.modules.common.enumerators.FlightType;
import ca.ids.abms.modules.common.enumerators.WakeTurbulence;
import ca.ids.abms.modules.common.mappers.Time4Digits;
import ca.ids.abms.modules.util.models.VersionedViewModel;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

public class AtcMovementLogViewModel extends VersionedViewModel {

    private Integer id;

    @NotNull
    private LocalDateTime dateOfContact;

    @Size(max = 100)
    private String registration;

    @Size(max = 100)
    private String operatorIdentifier;

    @Size(max = 255)
    private String route;

    @NotNull
    @Size(min = 1, max = 10)
    private String flightId;

    @NotNull
    @Size(max = 5)
    private String aircraftType;

    @Size(max = 100)
    private String departureAerodrome;

    @Size(max = 100)
    private String destinationAerodrome;

    @Size(max = 14)
    private String firEntryPoint;

    @Time4Digits
    private String firEntryTime;

    @Size(max = 14)
    private String firMidPoint;

    @Time4Digits
    private String firMidTime;

    @Size(max = 14)
    private String firExitPoint;

    @Time4Digits
    private String firExitTime;

    @Size(max = 10)
    private String flightLevel;

    private WakeTurbulence wakeTurbulence;

    @NotNull
    private FlightCategory flightCategory;

    private LocalDateTime dayOfFlight;

    private FlightType flightType;

    @Time4Digits
    private String departureTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getDateOfContact() {
        return dateOfContact;
    }

    public void setDateOfContact(LocalDateTime dateOfContact) {
        this.dateOfContact = dateOfContact;
    }

    public String getRegistration() {
        return registration;
    }

    public void setRegistration(String registration) {
        this.registration = registration;
    }

    public String getOperatorIdentifier() {
        return operatorIdentifier;
    }

    public void setOperatorIdentifier(String operatorIdentifier) {
        this.operatorIdentifier = operatorIdentifier;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getFlightId() {
        return flightId;
    }

    public void setFlightId(String flightId) {
        this.flightId = flightId;
    }

    public String getAircraftType() {
        return aircraftType;
    }

    public void setAircraftType(String aircraftType) {
        this.aircraftType = aircraftType;
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

    public String getFirEntryPoint() {
        return firEntryPoint;
    }

    public void setFirEntryPoint(String firEntryPoint) {
        this.firEntryPoint = firEntryPoint;
    }

    public String getFirEntryTime() {
        return firEntryTime;
    }

    public void setFirEntryTime(String firEntryTime) {
        this.firEntryTime = firEntryTime;
    }

    public String getFirMidPoint() {
        return firMidPoint;
    }

    public void setFirMidPoint(String firMidPoint) {
        this.firMidPoint = firMidPoint;
    }

    public String getFirMidTime() {
        return firMidTime;
    }

    public void setFirMidTime(String firMidTime) {
        this.firMidTime = firMidTime;
    }

    public String getFirExitPoint() {
        return firExitPoint;
    }

    public void setFirExitPoint(String firExitPoint) {
        this.firExitPoint = firExitPoint;
    }

    public String getFirExitTime() {
        return firExitTime;
    }

    public void setFirExitTime(String firExitTime) {
        this.firExitTime = firExitTime;
    }

    public String getFlightLevel() {
        return flightLevel;
    }

    public void setFlightLevel(String flightLevel) {
        this.flightLevel = flightLevel;
    }

    public WakeTurbulence getWakeTurbulence() {
        return wakeTurbulence;
    }

    public void setWakeTurbulence(WakeTurbulence wakeTurbulence) {
        this.wakeTurbulence = wakeTurbulence;
    }

    public FlightCategory getFlightCategory() {
        return flightCategory;
    }

    public void setFlightCategory(FlightCategory flightCategory) {
        this.flightCategory = flightCategory;
    }

    public FlightType getFlightType() {
        return flightType;
    }

    public void setFlightType(FlightType flightType) {
        this.flightType = flightType;
    }

    public LocalDateTime getDayOfFlight() {
        return dayOfFlight;
    }

    public void setDayOfFlight(LocalDateTime dayOfFlight) {
        this.dayOfFlight = dayOfFlight;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AtcMovementLogViewModel)) {
            return false;
        }
        AtcMovementLogViewModel that = (AtcMovementLogViewModel) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (dateOfContact != null ? !dateOfContact.equals(that.dateOfContact) : that.dateOfContact != null)
            return false;
        return flightId != null ? flightId.equals(that.flightId) : that.flightId == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (dateOfContact != null ? dateOfContact.hashCode() : 0);
        result = 31 * result + (flightId != null ? flightId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AtcMovementLog{" +
            "id=" + id +
            ", dateOfContact='" + dateOfContact + '\'' +
            ", registration='" + registration + '\'' +
            ", operatorIdentifier='" + operatorIdentifier + '\'' +
            ", flightId='" + flightId + '\'' +
            ", aircraftType='" + aircraftType + '\'' +
            ", departureAerodrome='" + departureAerodrome + '\'' +
            ", destinationAerodrome='" + destinationAerodrome + '\'' +
            ", wakeTurbulence='" + wakeTurbulence + '\'' +
            ", flightCategory='" + flightCategory + '\'' +
            ", flightType='" + flightType + '\'' +
            '}';
    }
}
