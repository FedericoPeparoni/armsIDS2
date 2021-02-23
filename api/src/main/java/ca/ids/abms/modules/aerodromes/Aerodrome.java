package ca.ids.abms.modules.aerodromes;

import ca.ids.abms.config.db.SearchableEntity;
import ca.ids.abms.config.db.SearchableText;
import ca.ids.abms.config.db.validators.UniqueKey;
import ca.ids.abms.modules.aerodromeserviceoutages.AerodromeServiceOutage;
import ca.ids.abms.modules.aerodromeservicetypes.AerodromeServiceType;
import ca.ids.abms.modules.billingcenters.BillingCenter;
import ca.ids.abms.modules.unspecified.UnspecifiedDepartureDestinationLocation;
import ca.ids.abms.modules.util.models.VersionedAuditedEntity;
import ca.ids.abms.modules.util.models.geometry.GeometryDeserializer;
import ca.ids.abms.modules.util.models.geometry.GeometrySerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.vividsolutions.jts.geom.Geometry;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
@UniqueKey(columnNames = "aerodromeName")
public class Aerodrome extends VersionedAuditedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Size(max = 100)
    @SearchableText
    private String aerodromeName;

    @NotNull
    @Size(max = 100)
    @SearchableText
    private String extendedAerodromeName;

    @Column(name = "geometry", columnDefinition = "geometry")
    @NotNull
    @JsonSerialize(using = GeometrySerializer.class)
    @JsonDeserialize(using = GeometryDeserializer.class)
    private Geometry geometry;

    private Boolean aixmFlag;

    @NotNull
    private Boolean isDefaultBillingCenter;

    @NotNull
    @ManyToOne
    @SearchableEntity
    private AerodromeCategory aerodromeCategory;

    @NotNull
    @ManyToOne
    @SearchableEntity
    private BillingCenter billingCenter;

    @JsonIgnore
    @OneToMany(mappedBy = "aerodromeIdentifier")
    private Set<UnspecifiedDepartureDestinationLocation> unspecifiedDepartureDestinationLocations = new HashSet<>();

    @Column(name = "external_accounting_system_identifier", length = 20)
    @SearchableText
    @Size(max = 20)
    private String externalAccountingSystemIdentifier;

    @ManyToMany()
    @JoinTable(name="aerodrome_services",
        joinColumns={@JoinColumn(name="aerodrome_id")},
        inverseJoinColumns={@JoinColumn(name="service_type_id")})
    private Set<AerodromeServiceType> aerodromeServices;

    @JsonIgnore
    @OneToMany(mappedBy = "aerodrome")
    private Set<AerodromeServiceOutage> aerodromeServiceOutage = new HashSet<>();

    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Aerodrome aerodrome = (Aerodrome) o;

        return id != null ? id.equals(aerodrome.id) : aerodrome.id == null;
    }

    public AerodromeCategory getAerodromeCategory() {
        return aerodromeCategory;
    }

    public String getAerodromeName() {
        return aerodromeName;
    }

    public String getExtendedAerodromeName() {
        return extendedAerodromeName;
    }

    public Boolean getAixmFlag() {
        return aixmFlag;
    }

    public BillingCenter getBillingCenter() {
        return billingCenter;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public Integer getId() {
        return id;
    }

    public Boolean getIsDefaultBillingCenter() {
        return isDefaultBillingCenter;
    }

    public Set<UnspecifiedDepartureDestinationLocation> getUnspecifiedDepartureDestinationLocations() {
        return unspecifiedDepartureDestinationLocations;
    }

    public String getExternalAccountingSystemIdentifier() {
        return externalAccountingSystemIdentifier;
    }

    public Set<AerodromeServiceType> getAerodromeServices() {
        return aerodromeServices;
    }

    public Set<AerodromeServiceOutage> getAerodromeServiceOutage() {
        return aerodromeServiceOutage;
    }

    public boolean containsAerodromeServices (final AerodromeServiceType aerodromeServiceType) {
        if (aerodromeServices != null && aerodromeServiceType != null) {
            return aerodromeServices.contains (aerodromeServiceType);
        }
        return false;
    }

    public boolean hasAerodromeServices() {
        return aerodromeServices != null && !aerodromeServices.isEmpty();
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public void setAerodromeCategory(AerodromeCategory aAerodromeCategory) {
        aerodromeCategory = aAerodromeCategory;
    }

    public void setAerodromeName(String aAerodromeName) {
        aerodromeName = aAerodromeName;
    }

    public void setExtendedAerodromeName(String extendedAerodromeName) {
        this.extendedAerodromeName = extendedAerodromeName;
    }

    public void setAixmFlag(Boolean aAixmFlag) {
        aixmFlag = aAixmFlag;
    }

    public void setBillingCenter(BillingCenter aBillingCenter) {
        billingCenter = aBillingCenter;
    }

    public void setGeometry(Geometry aGeometry) {
        geometry = aGeometry;
    }

    public void setId(Integer aId) {
        id = aId;
    }

    public void setIsDefaultBillingCenter(Boolean aIsDefaultBillingCenter) {
        isDefaultBillingCenter = aIsDefaultBillingCenter;
    }

    public void setUnspecifiedDepartureDestinationLocations(Set<UnspecifiedDepartureDestinationLocation> aUnspecifiedDepartureDestinationLocations) {
        unspecifiedDepartureDestinationLocations = aUnspecifiedDepartureDestinationLocations;
    }

    public void setExternalAccountingSystemIdentifier(String externalAccountingSystemIdentifier) {
        this.externalAccountingSystemIdentifier = externalAccountingSystemIdentifier;
    }

    public void setAerodromeServices(Set<AerodromeServiceType> aerodromeServices) {
        this.aerodromeServices = aerodromeServices;
    }

    public void setAerodromeServiceOutage(Set<AerodromeServiceOutage> aerodromeServiceOutage) {
        this.aerodromeServiceOutage = aerodromeServiceOutage;
    }

    @Override
    public String toString() {
        return "Aerodrome{" +
            "id=" + id +
            ", aerodromeName='" + aerodromeName + '\'' +
            ", aerodromeServices=" + aerodromeServices +
            '}';
    }
}
