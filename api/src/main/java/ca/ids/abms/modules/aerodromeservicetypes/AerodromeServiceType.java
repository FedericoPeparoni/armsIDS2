package ca.ids.abms.modules.aerodromeservicetypes;

import ca.ids.abms.config.db.SearchableText;
import ca.ids.abms.config.db.validators.UniqueKey;
import ca.ids.abms.modules.aerodromeserviceoutages.AerodromeServiceOutage;
import ca.ids.abms.modules.util.models.VersionedAuditedEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
@UniqueKey(columnNames = "serviceName")
public class AerodromeServiceType extends VersionedAuditedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Size(max = 50)
    @SearchableText
    private String serviceName;

    @NotNull
    @Size(max = 30)
    @Enumerated(EnumType.STRING)
    private DiscountType serviceOutageApproachDiscountType;

    @NotNull
    private Double serviceOutageApproachAmount;

    @NotNull
    @Size(max = 30)
    @Enumerated(EnumType.STRING)
    private DiscountType serviceOutageAerodromeDiscountType;

    @NotNull
    private Double serviceOutageAerodromeAmount;

    @NotNull
    @Size(max = 255)
    private String defaultFlightNotes;

    @JsonIgnore
    @OneToMany(mappedBy = "aerodromeServiceType")
    private Set<AerodromeServiceOutage> aerodromeServiceOutage = new HashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public DiscountType getServiceOutageApproachDiscountType() {
        return serviceOutageApproachDiscountType;
    }

    public void setServiceOutageApproachDiscountType(DiscountType serviceOutageApproachDiscountType) {
        this.serviceOutageApproachDiscountType = serviceOutageApproachDiscountType;
    }

    public Double getServiceOutageApproachAmount() {
        return serviceOutageApproachAmount;
    }

    public void setServiceOutageApproachAmount(Double serviceOutageApproachAmount) {
        this.serviceOutageApproachAmount = serviceOutageApproachAmount;
    }

    public DiscountType getServiceOutageAerodromeDiscountType() {
        return serviceOutageAerodromeDiscountType;
    }

    public void setServiceOutageAerodromeDiscountType(DiscountType serviceOutageAerodromeDiscountType) {
        this.serviceOutageAerodromeDiscountType = serviceOutageAerodromeDiscountType;
    }

    public Double getServiceOutageAerodromeAmount() {
        return serviceOutageAerodromeAmount;
    }

    public void setServiceOutageAerodromeAmount(Double serviceOutageAerodromeAmount) {
        this.serviceOutageAerodromeAmount = serviceOutageAerodromeAmount;
    }

    public String getDefaultFlightNotes() {
        return defaultFlightNotes;
    }

    public void setDefaultFlightNotes(String defaultFlightNotes) {
        this.defaultFlightNotes = defaultFlightNotes;
    }

    public Set<AerodromeServiceOutage> getAerodromeServiceOutage() {
        return aerodromeServiceOutage;
    }

    public void setAerodromeServiceOutage(Set<AerodromeServiceOutage> aerodromeServiceOutage) {
        this.aerodromeServiceOutage = aerodromeServiceOutage;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        AerodromeServiceType aerodromeServiceType = (AerodromeServiceType) o;

        return id != null ? id.equals(aerodromeServiceType.id) : aerodromeServiceType.id == null;
    }

    @Override
    public String toString() {
        return "AerodromeServiceType{" +
            "id=" + id +
            ", serviceName='" + serviceName + '\'' +
            ", serviceOutageApproachDiscountType='" + serviceOutageApproachDiscountType + '\'' +
            ", serviceOutageApproachAmount=" + serviceOutageApproachAmount +
            ", serviceOutageAerodromeDiscountType='" + serviceOutageAerodromeDiscountType + '\'' +
            ", serviceOutageAerodromeAmount=" + serviceOutageAerodromeAmount +
            ", defaultFlightNotes='" + defaultFlightNotes + '\'' +
            '}';
    }
}
