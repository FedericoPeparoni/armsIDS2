package ca.ids.abms.modules.atcmovements;

import ca.ids.abms.config.db.SearchableText;
import ca.ids.abms.config.db.validators.UniqueKey;
import ca.ids.abms.modules.common.enumerators.*;
import ca.ids.abms.modules.common.mappers.Time4Digits;
import ca.ids.abms.modules.util.models.VersionedAuditedEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@UniqueKey(columnNames = {"flightId","dateOfContact","firEntryTime","firMidTime","firExitTime"})
public class AtcMovementLog extends VersionedAuditedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    private LocalDateTime dateOfContact;

    @SearchableText
    @Column(name="registration")
    @Size(max = 100)
    private String registration;

    @SearchableText
    @Column(name = "operator_identifier")
    @Size(max = 100)
    private String operatorIdentifier;

    @Size(max = 255)
    private String route;

    @SearchableText
    @Column(name="flight_id")
    @NotNull
    @Size(min = 1, max = 10)
    private String flightId;

    @NotNull
    @Size(max = 5)
    private String aircraftType;

    @SearchableText
    @Column(name="departure_aerodrome")
    @Size(max = 100)
    private String departureAerodrome;

    @SearchableText
    @Column(name="destination_aerodrome")
    @Size(max = 100)
    private String destinationAerodrome;

    @SearchableText
    @Column(name="fir_entry_point")
    @Size(max = 14)
    private String firEntryPoint;

    @SearchableText
    @Column(name="fir_entry_time")
    @Time4Digits
    private String firEntryTime;

    @SearchableText
    @Column(name="fir_mid_point")
    @Size(max = 14)
    private String firMidPoint;

    @SearchableText
    @Column(name="fir_mid_time")
    @Time4Digits
    private String firMidTime;

    @SearchableText
    @Column(name="fir_exit_point")
    @Size(max = 14)
    private String firExitPoint;

    @SearchableText
    @Column(name="fir_exit_time")
    @Time4Digits
    private String firExitTime;

    @Size(max = 10)
    private String flightLevel;

    @Enumerated(EnumType.STRING)
    private WakeTurbulence wakeTurbulence;

    @NotNull
    @Basic
    @Convert( converter=FlightCategoryConverter.class )
    private FlightCategory flightCategory;

    @NotNull
    @Basic
    @Convert( converter=FlightTypeConverter.class )
    private FlightType flightType;

    private LocalDateTime dayOfFlight;

    @SearchableText
    @Column(name="departure_time")
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

    public void setWakeTurbulence(WakeTurbulence wakeTurbolence) {
        this.wakeTurbulence = wakeTurbolence;
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
        if (!(o instanceof AtcMovementLog)) {
            return false;
        }
        AtcMovementLog that = (AtcMovementLog) o;

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

    @Transient
    public String getDisplayName() {
        final LocalDateTime dof = getDateOfContact();
        return String.format ("AtcMovementLog {flightId=%s, dateOfContact=%s, departureAerodrome=%s}",
            getFlightId(), dof == null ? null : dof.toLocalDate().toString(), getDepartureAerodrome());
    }
}
