package ca.ids.abms.modules.towermovements;

import ca.ids.abms.config.db.SearchableText;
import ca.ids.abms.config.db.validators.UniqueKey;
import ca.ids.abms.modules.common.enumerators.FlightCategory;
import ca.ids.abms.modules.common.enumerators.FlightCategoryConverter;
import ca.ids.abms.modules.common.mappers.Time4Digits;
import ca.ids.abms.modules.util.models.VersionedAuditedEntity;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@UniqueKey(columnNames = {"flightId", "dateOfContact", "departureContactTime", "destinationContactTime"})
public class TowerMovementLog extends VersionedAuditedEntity {

    private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    private LocalDateTime dateOfContact;

    @SearchableText
    @Column(name="flight_id")
    @NotNull
    @Size(min = 1, max = 10)
    private String flightId;

    @SearchableText
    @Column(name="registration")
    @Size(max = 100)
    private String registration;

    @NotNull
    @Size(max = 5)
    private String aircraftType;

    @SearchableText
    @Column(name="operator_name")
    @Size(max = 100)
    private String operatorName;

    @SearchableText
    @Column(name="departure_aerodrome")
    @Size(max = 100)
    private String departureAerodrome;

    @SearchableText
    @Column(name="departure_contact_time")
    @Time4Digits
    private String departureContactTime;

    @SearchableText
    @Column(name="destination_aerodrome")
    @Size(max = 100)
    private String destinationAerodrome;

    @SearchableText
    @Column(name="destination_contact_time")
    @Time4Digits
    private String destinationContactTime;

    @SearchableText
    @Column(name="route")
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
    @Basic
    @Convert( converter=FlightCategoryConverter.class )
    private FlightCategory flightCategory;

    private LocalDateTime dayOfFlight;

    @SearchableText
    @Column(name="departure_time")
    @Time4Digits
    private String departureTime;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof TowerMovementLog)) {
            return false;
        }
        TowerMovementLog that = (TowerMovementLog) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        return flightId.equals(that.flightId);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (flightId != null ? flightId.hashCode() : 0);
        result = 31 * result + (departureAerodrome != null ? departureAerodrome.hashCode() : 0);
        result = 31 * result + (departureContactTime != null ? departureContactTime.hashCode() : 0);
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

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    @Override
    public String toString() {
        return "TowerMovementLog{" +
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

    @Transient
    public String getDisplayName() {
        final LocalDateTime dof = getDateOfContact();
        return String.format ("TowerMovementLog {flightId=%s, dateOfContact=%s, departureAerodrome=%s}",
            getFlightId(), dof == null ? null : dof.toLocalDate().toString(), getDepartureAerodrome());
    }
}
