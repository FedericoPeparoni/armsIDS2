package ca.ids.abms.modules.manifests;

import ca.ids.abms.modules.aircraft.AircraftTypeViewModel;
import ca.ids.abms.modules.common.dto.DefaultRejectableCsvModel;
import ca.ids.abms.modules.dataimport.LocalDateConverter;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

public class PassengerManifestCsvViewModel extends DefaultRejectableCsvModel {

    @CsvBindByName
    private Integer documentNumber;

    @CsvCustomBindByName(converter = LocalDateConverter.class, column="dateOfFlight")
    @NotNull
    private LocalDate dateOfFlight;

    @CsvBindByName
    @NotNull
    @Size(max = 100)
    private String operator;

    @CsvCustomBindByName(converter = FlightTypeConverter.class, column="typeOfFlight")
    @NotNull
    private FlightType typeOfFlight;

    @CsvCustomBindByName(converter = AircraftTypeViewModelConverter.class, column="aircraftType")
    private AircraftTypeViewModel aircraftType;

    @CsvBindByName
    @NotNull
    private Integer registrationNumber;

    @CsvBindByName
    @NotNull
    @Size(max = 7)
    private String flightId;

    @CsvBindByName
    @NotNull
    private Integer totalPersons;

    @CsvBindByName
    @NotNull
    private Integer internationalPassengers;

    @CsvBindByName
    @NotNull
    private Integer domesticPassengers;

    private byte[]  passengerManifestImage;

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

    public byte[] getPassengerManifestImage() {
        return passengerManifestImage;
    }

    public void setPassengerManifestImage(byte[] passengerManifestImage) {
        this.passengerManifestImage = passengerManifestImage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PassengerManifestCsvViewModel that = (PassengerManifestCsvViewModel) o;

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
