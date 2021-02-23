package ca.ids.abms.modules.towermovements;

import ca.ids.abms.modules.common.dto.DefaultRejectableCsvModel;
import ca.ids.abms.modules.common.mappers.ControlCSVFile;
import com.opencsv.bean.CsvBindByPosition;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@ControlCSVFile(ignoreFirstRows = 5)
public class TowerMovementLogCsvViewModel extends DefaultRejectableCsvModel {

    private Integer id;

    @Size(max = 10)
    @NotNull
    @CsvBindByPosition(position = 0)
    private String dateOfContact;

    @NotNull
    @Size(max = 16)
    @CsvBindByPosition(position = 1)
    private String flightId;

    @Size(max = 100)
    @CsvBindByPosition(position = 2)
    private String registration;

    @NotNull
    @CsvBindByPosition(position = 3)
    @Size(max = 5)
    private String aircraftType;

    @Size(max = 3)
    @CsvBindByPosition(position = 4)
    private String operatorName;

    @Size(max = 100)
    @CsvBindByPosition(position = 5)
    private String departureAerodrome;

    @Size(max = 5)
    @CsvBindByPosition(position = 6)
    private String departureContactTime;

    @Size(max = 255)
    @CsvBindByPosition(position = 7)
    private String route;

    @Size(max = 100)
    @CsvBindByPosition(position = 8)
    private String destinationAerodrome;

    @Size(max = 5)
    @CsvBindByPosition(position = 9)
    private String destinationContactTime;

    @Size(max = 10)
    @CsvBindByPosition(position = 10)
    private String flightLevel;

    @NotNull
    @CsvBindByPosition(position = 11)
    private Integer flightCrew;

    @NotNull
    @CsvBindByPosition(position = 12)
    private Integer passengers;

    @CsvBindByPosition(position = 13)
    private String flightCategory;

    @CsvBindByPosition(position = 14)
    private String nonSchedule;

    @CsvBindByPosition(position = 15)
    private String pvt;

    @CsvBindByPosition(position = 16)
    private String commercial;

    @CsvBindByPosition(position = 17)
    private String local;

    @Size(max = 4)
    @CsvBindByPosition(position = 18)
    private String departureTime;

    @CsvBindByPosition(position = 19)
    private String landingTime;

    @CsvBindByPosition(position = 20)
    private String noMovNts;

    @Size(max = 8)
    private String dayOfFlight;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof TowerMovementLogCsvViewModel)) {
            return false;
        }
        TowerMovementLogCsvViewModel that = (TowerMovementLogCsvViewModel) o;

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

    public String getRegistration() { return registration;}

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

    public String getDateOfContact() {
        return dateOfContact;
    }

    public void setDateOfContact(String dateOfContact) {
        this.dateOfContact = dateOfContact;
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

    public String getFlightCategory() {
        return flightCategory;
    }

    public void setFlightCategory(String flightCategory) {
        this.flightCategory = flightCategory;
    }

    public String getNonSchedule() {
        return nonSchedule;
    }

    public void setNonSchedule(String nonSchedule) {
        this.nonSchedule = nonSchedule;
    }

    public String getPvt() {
        return pvt;
    }

    public void setPvt(String pvt) {
        this.pvt = pvt;
    }

    public String getCommercial() {
        return commercial;
    }

    public void setCommercial(String commercial) {
        this.commercial = commercial;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getLandingTime() {
        return landingTime;
    }

    public void setLandingTime(String landingTime) {
        this.landingTime = landingTime;
    }

    public String getNoMovNts() {
        return noMovNts;
    }

    public void setNoMovNts(String noMovNts) {
        this.noMovNts = noMovNts;
    }

    @Override
    public String toString() {
        return "TowerMovementLogCsvViewModel{" +
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
