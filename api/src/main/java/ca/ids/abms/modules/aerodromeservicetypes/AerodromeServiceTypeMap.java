package ca.ids.abms.modules.aerodromeservicetypes;

import ca.ids.abms.config.db.SearchableEntity;
import ca.ids.abms.modules.aerodromes.Aerodrome;
import ca.ids.abms.modules.aerodromeserviceoutages.AerodromeServiceOutage;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "aerodrome_services")
public class AerodromeServiceTypeMap implements Serializable {

    @EmbeddedId
    @SearchableEntity
    private AerodromeServiceTypeKey id;

    @MapsId("aerodrome")
    @OneToOne
    @JoinColumn(name = "aerodrome_id")
    private Aerodrome aerodrome;

    @MapsId("aerodromeServiceType")
    @OneToOne
    @JoinColumn(name = "service_type_id")
    private AerodromeServiceType aerodromeServiceType;

    @OneToMany(mappedBy = "aerodromeServiceTypeMap")
    private Set<AerodromeServiceOutage> aerodromeServiceOutages;

    public AerodromeServiceTypeKey getId() {
        return id;
    }

    public void setId(AerodromeServiceTypeKey id) {
        this.id = id;
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

    public Set<AerodromeServiceOutage> getAerodromeServiceOutages() {
        return aerodromeServiceOutages;
    }

    public void setAerodromeServiceOutages(Set<AerodromeServiceOutage> aerodromeServiceOutages) {
        this.aerodromeServiceOutages = aerodromeServiceOutages;
    }
}

