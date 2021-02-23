package ca.ids.abms.modules.aerodromeserviceoutages;

import ca.ids.abms.modules.aerodromes.Aerodrome;
import ca.ids.abms.modules.aerodromeservicetypes.AerodromeServiceType;
import ca.ids.abms.modules.aerodromeservicetypes.AerodromeServiceTypeMap;
import ca.ids.abms.modules.aerodromeservicetypes.DiscountType;
import ca.ids.abms.modules.util.models.VersionedAuditedEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class AerodromeServiceOutage extends VersionedAuditedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @NotNull
    @JoinColumns({
        @JoinColumn(name="aerodrome_id", referencedColumnName="aerodrome_id"),
        @JoinColumn(name="aerodrome_service_type_id", referencedColumnName="service_type_id")
    })
    private AerodromeServiceTypeMap aerodromeServiceTypeMap;

    @NotNull
    private LocalDateTime startDateTime;

    @NotNull
    private LocalDateTime endDateTime;

    @NotNull
    @Enumerated(EnumType.STRING)
    private DiscountType approachDiscountType;

    @NotNull
    private Double approachDiscountAmount;

    @NotNull
    @Enumerated(EnumType.STRING)
    private DiscountType aerodromeDiscountType;

    @NotNull
    private Double aerodromeDiscountAmount;

    @NotNull
    @Size(max = 255)
    private String flightNotes;

    @ManyToOne
    @JoinColumn(name = "aerodrome_id", insertable = false, updatable = false)
    private Aerodrome aerodrome;

    @ManyToOne
    @JoinColumn(name = "aerodrome_service_type_id", insertable = false, updatable = false)
    private AerodromeServiceType aerodromeServiceType;

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

    public Aerodrome getAerodrome() {
        return aerodrome;
    }

    public void setAerodrome(Aerodrome aerodrome) {
        this.aerodrome = aerodrome;
    }

    public AerodromeServiceType getAerodromeServiceType() {
        return aerodromeServiceType;
    }

    public void setAerodromeServiceType(AerodromeServiceType aerodromeServiceType) {
        this.aerodromeServiceType = aerodromeServiceType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AerodromeServiceOutage that = (AerodromeServiceOutage) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "AerodromeServiceOutage{" +
            "id=" + id +
            ", aerodromeServiceTypeMap=" + aerodromeServiceTypeMap +
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
