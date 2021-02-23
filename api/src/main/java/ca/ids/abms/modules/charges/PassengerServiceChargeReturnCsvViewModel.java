package ca.ids.abms.modules.charges;

import ca.ids.abms.modules.common.dto.DefaultRejectableCsvModel;

import ca.ids.abms.modules.common.mappers.ControlCSVFile;
import ca.ids.abms.modules.common.mappers.Time4Digits;
import com.opencsv.bean.CsvBindByPosition;

import javax.validation.constraints.Size;

@ControlCSVFile(ignoreFirstRows = 6, stopAtThisToken = "TOTAL")
public class PassengerServiceChargeReturnCsvViewModel extends DefaultRejectableCsvModel {

    private Integer id;

    @CsvBindByPosition(position = 0)
    private String accountName;

    @CsvBindByPosition(position = 2)
    @Size(max = 16)
    private String flightId;

    @CsvBindByPosition(position = 1)
    @Size(max = 32)
    private String dayOfFlight;

    @Time4Digits
    private String departureTime;

    @CsvBindByPosition(position = 4)
    private Integer transitPassengers;

    @CsvBindByPosition(position = 5)
    private Integer joiningPassengers;

    @CsvBindByPosition(position = 6)
    private Integer children;

    @CsvBindByPosition(position = 7)
    private Integer chargeableItlPassengers;

    @CsvBindByPosition(position = 8)
    private Integer chargeableDomesticPassengers;

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

    public Integer getTransitPassengers() {
        return transitPassengers;
    }

    public void setTransitPassengers(Integer transitPassengers) {
        this.transitPassengers = transitPassengers;
    }

    public Integer getJoiningPassengers() {
        return joiningPassengers;
    }

    public void setJoiningPassengers(Integer joiningPassengers) {
        this.joiningPassengers = joiningPassengers;
    }

    public Integer getChildren() {
        return children;
    }

    public void setChildren(Integer children) {
        this.children = children;
    }

    public Integer getChargeableItlPassengers() {
        return chargeableItlPassengers;
    }

    public void setChargeableItlPassengers(Integer chargeableItlPassengers) {
        this.chargeableItlPassengers = chargeableItlPassengers;
    }

    public Integer getChargeableDomesticPassengers() {
        return chargeableDomesticPassengers;
    }

    public void setChargeableDomesticPassengers(Integer chargeableDomesticPassengers) {
        this.chargeableDomesticPassengers = chargeableDomesticPassengers;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccount(String accountName) {
        this.accountName = accountName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PassengerServiceChargeReturnCsvViewModel that = (PassengerServiceChargeReturnCsvViewModel) o;

        return id != null ? id.equals(that.id) : that.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
