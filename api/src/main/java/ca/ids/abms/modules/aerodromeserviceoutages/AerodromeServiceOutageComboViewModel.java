package ca.ids.abms.modules.aerodromeserviceoutages;

import ca.ids.abms.modules.aerodromeservicetypes.DiscountType;

import java.time.LocalDateTime;

public class AerodromeServiceOutageComboViewModel {

    private Integer id;

    private String aerodromeServiceType;

    private String aerodrome;

    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

    private DiscountType approachDiscountType;

    private Double approachDiscountAmount;

    private DiscountType aerodromeDiscountType;

    private Double aerodromeDiscountAmount;

    private String flightNotes;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAerodromeServiceType() {
        return aerodromeServiceType;
    }

    public void setAerodromeServiceType(String aerodromeServiceType) {
        this.aerodromeServiceType = aerodromeServiceType;
    }

    public String getAerodrome() {
        return aerodrome;
    }

    public void setAerodrome(String aerodrome) {
        this.aerodrome = aerodrome;
    }

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

    public DiscountType getApproachDiscountType() {
        return approachDiscountType;
    }

    public void setApproachDiscountType(DiscountType approachDiscountType) {
        this.approachDiscountType = approachDiscountType;
    }

    public Double getApproachDiscountAmount() {
        return approachDiscountAmount;
    }

    public void setApproachDiscountAmount(Double approachDiscountAmount) {
        this.approachDiscountAmount = approachDiscountAmount;
    }

    public DiscountType getAerodromeDiscountType() {
        return aerodromeDiscountType;
    }

    public void setAerodromeDiscountType(DiscountType aerodromeDiscountType) {
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
