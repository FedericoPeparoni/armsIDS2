package ca.ids.abms.modules.aerodromeserviceoutages;

import ca.ids.abms.util.csv.annotations.CsvProperty;

import java.time.LocalDateTime;

public class AerodromeServiceOutageCsvExportModel {

    @CsvProperty(value = "Start Date/Time", dateTime = true)
    private LocalDateTime startDateTime;

    @CsvProperty(value = "End Date/Time", dateTime = true)
    private LocalDateTime endDateTime;

    private String approachDiscountType;

    @CsvProperty(precision = 2)
    private Double approachDiscountAmount;

    private String aerodromeDiscountType;

    @CsvProperty(precision = 2)
    private Double aerodromeDiscountAmount;

    private String flightNotes;

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public String getApproachDiscountType() {
        return approachDiscountType;
    }

    public void setApproachDiscountType(String approachDiscountType) {
        this.approachDiscountType = approachDiscountType;
    }

    public Double getApproachDiscountAmount() {
        return approachDiscountAmount;
    }

    public void setApproachDiscountAmount(Double approachDiscountAmount) {
        this.approachDiscountAmount = approachDiscountAmount;
    }

    public String getAerodromeDiscountType() {
        return aerodromeDiscountType;
    }

    public void setAerodromeDiscountType(String aerodromeDiscountType) {
        this.aerodromeDiscountType = aerodromeDiscountType;
    }

    public Double getAerodromeDiscountAmount() {
        return aerodromeDiscountAmount;
    }

    public void setAerodromeDiscountAmount(Double aerodromeDiscountAmount) {
        this.aerodromeDiscountAmount = aerodromeDiscountAmount;
    }

    public String getFlightNotes() {
        return flightNotes;
    }

    public void setFlightNotes(String flightNotes) {
        this.flightNotes = flightNotes;
    }
}
