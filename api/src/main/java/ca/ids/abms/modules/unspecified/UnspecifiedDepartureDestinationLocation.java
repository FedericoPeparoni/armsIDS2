package ca.ids.abms.modules.unspecified;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import ca.ids.abms.config.db.SearchableEntity;
import ca.ids.abms.config.db.SearchableText;
import ca.ids.abms.config.db.validators.UniqueKey;
import ca.ids.abms.modules.aerodromes.Aerodrome;
import ca.ids.abms.modules.countries.Country;
import ca.ids.abms.modules.util.models.VersionedAuditedEntity;

@Entity
@UniqueKey(columnNames={"textIdentifier", "name"})
public class UnspecifiedDepartureDestinationLocation extends VersionedAuditedEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Size(max = 255)
    @NotNull
    @SearchableText
    private String textIdentifier;

    @Size(max = 100)
    @NotNull
    @SearchableText
    private String name;

    @NotNull
    private Boolean maintained;

    @ManyToOne
    @JoinColumn(name="aerodrome_identifier", referencedColumnName="aerodromeName")
    @SearchableEntity
    private Aerodrome aerodromeIdentifier;

    private Double latitude;

    private Double longitude;

    @NotNull
    @Enumerated(EnumType.STRING)
    @SearchableText(exactMatch = true)
    private UnspecifiedDepartureDestinationLocationStatus status;

    @ManyToOne
    @JoinColumn(name = "country_code")
    @NotNull
    @SearchableEntity
    private Country countryCode;

    public UnspecifiedDepartureDestinationLocation(String name) {
        super();
        this.name = name;
    }

    public UnspecifiedDepartureDestinationLocation() {
        super();
    }

    public Aerodrome getAerodromeIdentifier() {
        return aerodromeIdentifier;
    }

    public Integer getId() {
        return id;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Boolean getMaintained() {
        return maintained;
    }

    public String getName() {
        return name;
    }

    public UnspecifiedDepartureDestinationLocationStatus getStatus() {
        return status;
    }

    public String getTextIdentifier() {
        return textIdentifier;
    }

    public void setAerodromeIdentifier(Aerodrome aAerodromeIdentifier) {
        aerodromeIdentifier = aAerodromeIdentifier;
    }

    public void setId(Integer aId) {
        id = aId;
    }

    public void setLatitude(Double aLatitude) {
        latitude = aLatitude;
    }

    public void setLongitude(Double aLongitude) {
        longitude = aLongitude;
    }

    public void setMaintained(Boolean aMaintained) {
        maintained = aMaintained;
    }

    public void setName(String aName) {
        name = aName;
    }

    public void setStatus(UnspecifiedDepartureDestinationLocationStatus aStatus) {
        status = aStatus;
    }

    public void setTextIdentifier(String aTextIdentifier) {
        textIdentifier = aTextIdentifier;
    }

	public Country getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(Country countryCode) {
		this.countryCode = countryCode;
	}

	@Override
    public String toString() {
        return "UnspecifiedDepartureDestinationLocation [id=" + id + ", textIdentifier=" + textIdentifier + ", name="
                + name + ", maintained=" + maintained + ", aerodromeIdentifier=" + aerodromeIdentifier + ", latitude="
                + latitude + ", longitude=" + longitude + ", status=" + status + "]";
    }
}
