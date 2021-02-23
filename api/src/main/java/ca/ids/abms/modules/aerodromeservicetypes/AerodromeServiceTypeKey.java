package ca.ids.abms.modules.aerodromeservicetypes;

import ca.ids.abms.config.db.SearchableEntity;
import ca.ids.abms.modules.aerodromes.Aerodrome;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class AerodromeServiceTypeKey implements Serializable {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "aerodrome_id")
    @SearchableEntity(searchableField = "aerodromeName")
    private Aerodrome aerodrome;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "service_type_id")
    @SearchableEntity(searchableField = "serviceName")
    private AerodromeServiceType aerodromeServiceType;

    AerodromeServiceTypeKey() {}

    public AerodromeServiceTypeKey(Aerodrome aerodrome, AerodromeServiceType aerodromeServiceType) {
        this.aerodrome = aerodrome;
        this.aerodromeServiceType = aerodromeServiceType;
    }

    public Aerodrome getAerodrome() {
        return aerodrome;
    }

    public AerodromeServiceType getAerodromeServiceType() {
        return aerodromeServiceType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AerodromeServiceTypeKey that = (AerodromeServiceTypeKey) o;
        return Objects.equals(aerodrome, that.aerodrome) &&
            Objects.equals(aerodromeServiceType, that.aerodromeServiceType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aerodrome, aerodromeServiceType);
    }

}
