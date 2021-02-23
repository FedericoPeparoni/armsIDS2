package ca.ids.abms.modules.towermovements;

import ca.ids.abms.modules.common.enumerators.FlightCategory;
import ca.ids.abms.modules.common.mappers.Time4Digits;
import ca.ids.abms.modules.util.models.VersionedViewModel;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

public class TowerMovementLogViewModel extends VersionedViewModel {

    private Integer id;

    @NotNull
    private LocalDateTime dateOfContact;

    @NotNull
    @Size(min = 1, max = 10)
    private String flightId;

    @Size(max = 100)
    private String registration;

    @NotNull
    @Size(max = 5)
    private String aircraftType;

    @Size(max = 100)
    private String operatorName;

    @Size(max = 100)
    private String departureAerodrome;

    @Time4Digits
    private String departureContactTime;

    @Size(max = 100)
    private String destinationAerodrome;

    @Time4Digits
    private String destinationContactTime;

    @Size(max = 255)
    private String route;

    @Size(max = 10)
    private String flightLevel;

    @NotNull
    @Min(value = 0)
    private Integer flightCrew;

    @NotNull
    @Min(value = 0)
    private Integer passengers;

    @NotNull
    private FlightCategory flightCategory;

    private LocalDateTime dayOfFlight;

    @Time4Digits
    private String departureTime;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof TowerMovementLogViewModel)) {
            return false;
        }
        TowerMovementLogViewModel that = (TowerMovementLogViewModel) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        return flightId.equals(that.flightId);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + flightId.hashCode();
        return result;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFlightId() {
        return flightId;
    }

    public void setFlightId(String flightId) {
        this.flightId = flightId;
    }

    public String getRegistration() {
        return registration;
    }

    public void setRegistration(String registration) {
        this.registration = registration;
    }

    public String getAircraftType() {
        return aircraftType;
    }

    public void setAircraftType(String aircraftType) {
        this.aircraftType = aircraftType;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getDepartureAerodrome() {
        return departureAerodrome;
    }

    public void setDepartureAerodrome(String departureAerodrome) {
        this.departureAerodrome = departureAerodrome;
    }

    public String getDepartureContactTime() {
        return departureContactTime;
    }

    public void setDepartureContactTime(String departureContactTime) {
        this.departureContactTime = departureContactTime;
    }

    public LocalDateTime getDateOfContact() {
        return dateOfContact;
    }

    public void setDateOfContact(LocalDateTime dateOfContact) {
        this.dateOfContact = dateOfContact;
    }

    public FlightCategory getFlightCategory() {
        return flightCategory;
    }

    public void setFlightCategory(FlightCategory flightCategory) {
        this.flightCategory = flightCategory;
    }

    public String getDestinationAerodrome() {
        return destinationAerodrome;
    }

    public void setDestinationAerodrome(String destinationAerodrome) {
        this.destinationAerodrome = destinationAerodrome;
    }

    public String getDestinationContactTime() {
        return destinationContactTime;
    }

    public void setDestinationContactTime(String destinationContactTime) {
        this.destinationContactTime = destinationContactTime;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getFlightLevel() {
        return flightLevel;
    }

    public void setFlightLevel(String flightLevel) {
        this.flightLevel = flightLevel;
    }

    public Integer getFlightCrew() {
        return flightCrew;
    }

    public void setFlightCrew(Integer flightCrew) {
        this.flightCrew = flightCrew;
    }

    public Integer getPassengers() {
        return passengers;
    }

    public void setPassengers(Integer passengers) {
        this.passengers = passengers;
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
    public String toString() {
        return "TowerMovementLogViewModel{" +
            "id=" + id +
            ", flightId='" + flightId + '\'' +
            ", dateOfContact='" + dateOfContact + '\'' +
            ", registration='" + registration + '\'' +
            ", aircraftType='" + aircraftType + '\'' +
            ", operatorName='" + operatorName + '\'' +
            ", departureAerodrome='" + departureAerodrome + '\'' +
            ", departureContactTime='" + departureContactTime + '\'' +
            ", destinationAerodrome='" + destinationAerodrome + '\'' +
            ", destinationContactTime='" + destinationContactTime + '\'' +
            ", flightLevel='" + flightLevel + '\'' +
            ", flightCrew=" + flightCrew +
            ", passengers=" + passengers +
            '}';
    }
}
