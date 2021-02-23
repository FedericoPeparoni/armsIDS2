package ca.ids.abms.modules.manifests;

import java.time.LocalDate;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import ca.ids.abms.modules.aircraft.AircraftTypeViewModel;
import ca.ids.abms.modules.common.dto.EmbeddedFileDto;

public class PassengerManifestViewModel extends EmbeddedFileDto {

    private Integer documentNumber;

    @NotNull
    private LocalDate dateOfFlight;

    @NotNull
    @Size(max = 100)
    private String operator;

    @NotNull
    private FlightType typeOfFlight;

    private AircraftTypeViewModel aircraftType;

    @NotNull
    private Integer registrationNumber;

    @NotNull
    @Size(max = 7)
    private String flightId;

    @NotNull
    private Integer totalPersons;

    @NotNull
    private Integer internationalPassengers;

    @NotNull
    private Integer domesticPassengers;

    public Integer getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(Integer documentNumber) {
        this.documentNumber = documentNumber;
    }

    public LocalDate getDateOfFlight() {
        return dateOfFlight;
    }

    public void setDateOfFlight(LocalDate dateOfFlight) {
        this.dateOfFlight = dateOfFlight;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public FlightType getTypeOfFlight() {
        return typeOfFlight;
    }

    public void setTypeOfFlight(FlightType typeOfFlight) {
        this.typeOfFlight = typeOfFlight;
    }

    public AircraftTypeViewModel getAircraftType() {
        return aircraftType;
    }

    public void setAircraftType(AircraftTypeViewModel aircraftType) {
        this.aircraftType = aircraftType;
    }

    public Integer getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(Integer registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getFlightId() {
        return flightId;
    }

    public void setFlightId(String flightId) {
        this.flightId = flightId;
    }

    public Integer getTotalPersons() {
        return totalPersons;
    }

    public void setTotalPersons(Integer totalPersons) {
        this.totalPersons = totalPersons;
    }

    public Integer getInternationalPassengers() {
        return internationalPassengers;
    }

    public void setInternationalPassengers(Integer internationalPassengers) {
        this.internationalPassengers = internationalPassengers;
    }

    public Integer getDomesticPassengers() {
        return domesticPassengers;
    }

    public void setDomesticPassengers(Integer domesticPassengers) {
        this.domesticPassengers = domesticPassengers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PassengerManifestViewModel that = (PassengerManifestViewModel) o;

        return documentNumber != null ? documentNumber.equals(that.documentNumber) : that.documentNumber == null;

    }

    @Override
    public int hashCode() {
        return documentNumber != null ? documentNumber.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "PassengerManifestViewModel{" +
            "documentNumber=" + documentNumber +
            ", dateOfFlight=" + dateOfFlight +
            ", operator='" + operator + '\'' +
            ", typeOfFlight=" + typeOfFlight +
            ", aircraftType=" + aircraftType +
            ", registrationNumber=" + registrationNumber +
            ", flightId='" + flightId + '\'' +
            ", totalPersons=" + totalPersons +
            ", internationalPassengers=" + internationalPassengers +
            ", domesticPassengers=" + domesticPassengers +
            '}';
    }
}
