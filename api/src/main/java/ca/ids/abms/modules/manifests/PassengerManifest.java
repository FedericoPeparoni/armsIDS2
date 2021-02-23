package ca.ids.abms.modules.manifests;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import ca.ids.abms.modules.aircraft.AircraftType;
import ca.ids.abms.modules.util.models.VersionedAuditedEntity;

@Entity
public class PassengerManifest extends VersionedAuditedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer documentNumber;

    @NotNull
    private LocalDate dateOfFlight;

    @NotNull
    @Size(max = 100)
    private String operator;

    @NotNull
    private FlightType typeOfFlight;

    @ManyToOne
    @JoinColumn(name = "aircraft_type")
    private AircraftType aircraftType;

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

    private byte[]  passengerManifestImage;

    private String passengerManifestImageType;

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

    public AircraftType getAircraftType() {
        return aircraftType;
    }

    public void setAircraftType(AircraftType aircraftType) {
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

    public String getPassengerManifestImageType() {
        return passengerManifestImageType;
    }

    public void setPassengerManifestImageType(String passengerManifestImageType) {
        this.passengerManifestImageType = passengerManifestImageType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PassengerManifest that = (PassengerManifest) o;

        return documentNumber != null ? documentNumber.equals(that.documentNumber) : that.documentNumber == null;

    }

    @Override
    public int hashCode() {
        return documentNumber != null ? documentNumber.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "PassengerManifest{" +
                "documentNumber='" + documentNumber + '\'' +
                ", dateOfFlight=" + dateOfFlight +
                ", operator='" + operator + '\'' +
                ", typeOfFlight='" + typeOfFlight + '\'' +
                ", aircraftType='" + aircraftType + '\'' +
                ", registrationNumber=" + registrationNumber +
                ", flightId='" + flightId + '\'' +
                ", totalPersons=" + totalPersons +
                ", internationalPassengers=" + internationalPassengers +
                ", domesticPassengers=" + domesticPassengers +
                ", passengerManifestImageType=" + passengerManifestImageType +
                '}';
    }
}
