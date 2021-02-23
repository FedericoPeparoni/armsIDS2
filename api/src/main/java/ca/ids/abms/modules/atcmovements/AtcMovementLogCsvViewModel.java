package ca.ids.abms.modules.atcmovements;

import ca.ids.abms.modules.common.dto.DefaultRejectableCsvModel;
import ca.ids.abms.modules.common.enumerators.FlightType;
import ca.ids.abms.modules.common.mappers.ControlCSVFile;
import com.opencsv.bean.CsvBindByPosition;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@ControlCSVFile(ignoreFirstRows = 1)
public class AtcMovementLogCsvViewModel extends DefaultRejectableCsvModel {

    private Integer id;

    @Size(max = 30)
    @NotNull
    @CsvBindByPosition(position = 0)
    private String dateOfContact;

    @Size(max = 100)
    @CsvBindByPosition(position = 1)
    private String registration;

    @CsvBindByPosition(position = 3)
    private String operator;

    @Size(max = 255)
    @CsvBindByPosition(position = 11)
    private String route;

    @NotNull
    @Size(max = 16)
    @CsvBindByPosition(position = 4)
    private String flightId;

    @NotNull
    @Size(max = 5)
    @CsvBindByPosition(position = 6)
    private String aircraftType;

    @Size(max = 100)
    @CsvBindByPosition(position = 5)
    private String operatorIdentifier;

    @Size(max = 100)
    @CsvBindByPosition(position = 9)
    private String departureAerodrome;

    @Size(max = 4)
    @CsvBindByPosition(position = 2)
    private String departureTime;

    @Size(max = 100)
    @CsvBindByPosition(position = 10)
    private String destinationAerodrome;

    @Size(max = 14)
    @CsvBindByPosition(position = 12)
    private String firEntryPoint;

    @Size(max = 5)
    @CsvBindByPosition(position = 13)
    private String firEntryTime;

    @CsvBindByPosition(position = 14)
    private String flEntryTime;

    @Size(max = 14)
    @CsvBindByPosition(position = 15)
    private String firMidPoint;

    @Size(max = 5)
    @CsvBindByPosition(position = 16)
    private String firMidTime;

    @CsvBindByPosition(position = 17)
    private String flMidTime;

    @Size(max = 14)
    @CsvBindByPosition(position = 18)
    private String firExitPoint;

    @Size(max = 5)
    @CsvBindByPosition(position = 19)
    private String firExitTime;

    @CsvBindByPosition(position = 20)
    private String flExitTime;

    @CsvBindByPosition(position = 7)
    private String wakeTurbulence;

    @NotNull
    @CsvBindByPosition(position = 21)
    private String flightCategory;

    @CsvBindByPosition(position = 8)
    private String domesticOrIntl;

    @Size(max = 10)
    private String flightLevel;

    @Size(max = 30)
    private String dayOfFlight;

    @Size(max = 8)
    private FlightType flightType;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDateOfContact() {
        return dateOfContact;
    }

    public void setDateOfContact(String dateOfContact) {
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

    public String getWakeTurbulence() {
        return wakeTurbulence;
    }

    public void setWakeTurbulence(String wakeTurbulence) {
        this.wakeTurbulence = wakeTurbulence;
    }

    public String getFlightCategory() {
        return flightCategory;
    }

    public void setFlightCategory(String flightCategory) {
        this.flightCategory = flightCategory;
    }

    public FlightType getFlightType() {
        return flightType;
    }

    public void setFlightType(FlightType flightType) {
        this.flightType = flightType;
    }

    public String getDayOfFlight() {
        return dayOfFlight;
    }

    public void setDayOfFlight(String dayOfFlight) {
        this.dayOfFlight = dayOfFlight;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getOperatorName() {
        return operator;
    }

    public void setOperatorName(String operator) {
        this.operator = operator;
    }

    public String getFlEntryTime() {
        return flEntryTime;
    }

    public void setFlEntryTime(String flEntryTime) {
        this.flEntryTime = flEntryTime;
    }

    public String getFlMidTime() {
        return flMidTime;
    }

    public void setFlMidTime(String flMidTime) {
        this.flMidTime = flMidTime;
    }

    public String getFlExitTime() {
        return flExitTime;
    }

    public void setFlExitTime(String flExitTime) {
        this.flExitTime = flExitTime;
    }

    public String getDomesticOrIntl() {
        return domesticOrIntl;
    }

    public void setDomesticOrIntl(String domesticOrIntl) {
        this.domesticOrIntl = domesticOrIntl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AtcMovementLogCsvViewModel)) {
            return false;
        }
        AtcMovementLogCsvViewModel that = (AtcMovementLogCsvViewModel) o;

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
