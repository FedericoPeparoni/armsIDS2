package ca.ids.abms.modules.dataimport;

import java.time.LocalDate;
import java.time.LocalTime;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;

public class DataImportTestPojo {
    
    private Integer id;

    @CsvBindByName
    private String flightId;

    @CsvCustomBindByName(converter = LocalDateConverter.class, column="dayOfFlight")
    private LocalDate dayOfFlight;

    @CsvCustomBindByName(converter = LocalTimeConverter.class, column="departureTime")
    private LocalTime departureTime;

    @CsvBindByName
    private Integer transitPassengers;

    @CsvBindByName
    private Integer joiningPassengers;

    @CsvBindByName
    private Integer children;

    @CsvBindByName
    private Integer chargeableItlPassengers;

    @CsvBindByName
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

    public LocalDate getDayOfFlight() {
        return dayOfFlight;
    }

    public void setDayOfFlight(LocalDate dayOfFlight) {
        this.dayOfFlight = dayOfFlight;
    }

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalTime departureTime) {
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

    
}
