package ca.ids.abms.modules.aerodromeserviceoutages;

import ca.ids.abms.modules.aerodromeservicetypes.AerodromeServiceTypeMap;
import ca.ids.abms.modules.aerodromeservicetypes.DiscountType;
import ca.ids.abms.modules.util.models.VersionedViewModel;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Objects;

public class AerodromeServiceOutageViewModel extends VersionedViewModel {

    private Integer id;

    private AerodromeServiceTypeMap aerodromeServiceTypeMap;

    @NotNull
    private String aerodrome;

    @NotNull
    private String aerodromeServiceType;

    @NotNull
    private LocalDateTime startDateTime;

    @NotNull
    private LocalDateTime endDateTime;

    @NotNull
    private DiscountType approachDiscountType;

    @NotNull
    private Double approachDiscountAmount;

    @NotNull
    private DiscountType aerodromeDiscountType;

    @NotNull
    private Double aerodromeDiscountAmount;

    @NotNull
    @Size(max = 255)
    private String flightNotes;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public AerodromeServiceTypeMap getAerodromeServiceTypeMap() {
        return aerodromeServiceTypeMap;
    }

    public void setAerodromeServiceTypeMap(AerodromeServiceTypeMap aerodromeServiceTypeMap) {
        this.aerodromeServiceTypeMap = aerodromeServiceTypeMap;
    }

    public String getAerodrome() {
        return aerodrome;
    }

    public void setAerodrome(String aerodrome) {
        this.aerodrome = aerodrome;
    }

    public String getAerodromeServiceType() {
        return aerodromeServiceType;
    }

    public void setAerodromeServiceType(String aerodromeServiceType) {
        this.aerodromeServiceType = aerodromeServiceType;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AerodromeServiceOutageViewModel that = (AerodromeServiceOutageViewModel) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "AerodromeServiceOutageViewModel{" +
            "id=" + id +
            ", aerodrome='" + aerodrome + '\'' +
            ", aerodromeServiceType='" + aerodromeServiceType + '\'' +
            ", startDateTime=" + startDateTime +
            ", endDateTime=" + endDateTime +
            ", approachDiscountType=" + approachDiscountType +
            ", approachDiscountAmount=" + approachDiscountAmount +
            ", aerodromeDiscountType=" + aerodromeDiscountType +
            ", aerodromeDiscountAmount=" + aerodromeDiscountAmount +
            ", flightNotes='" + flightNotes + '\'' +
            '}';
    }
}
