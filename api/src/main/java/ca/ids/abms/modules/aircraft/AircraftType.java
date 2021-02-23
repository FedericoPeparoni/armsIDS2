package ca.ids.abms.modules.aircraft;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import ca.ids.abms.config.db.SearchableText;
import com.fasterxml.jackson.annotation.JsonIgnore;

import ca.ids.abms.config.db.validators.UniqueKey;
import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.util.models.VersionedAuditedEntity;
import ca.ids.abms.modules.waketurbulence.WakeTurbulenceCategory;

@Entity
@UniqueKey(columnNames = "aircraftType")
public class AircraftType extends VersionedAuditedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Size(max = 4)
    @NotNull
    @Column(unique = true)
    @SearchableText
    @SuppressWarnings("squid:S1700")
    private String aircraftType;

    @Size(max = 100)
    @NotNull
    @SearchableText
    private String aircraftName;

    private byte[] aircraftImage;

    @Size(max = 100)
    @NotNull
    @SearchableText
    private String manufacturer;

    @NotNull
    private Double maximumTakeoffWeight;

    @ManyToOne
    @JoinColumn(name = "wake_turbulence_category")
    @NotNull
    private WakeTurbulenceCategory wakeTurbulenceCategory;

    @JsonIgnore
    @OneToMany(mappedBy = "aircraftType")
    private Set<AircraftRegistration> aircraftRegistrations = new HashSet<>();

    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        AircraftType that = (AircraftType) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Transient
    public Collection<Account> getAccounts() {
        return getAircraftRegistrations().stream().map(AircraftRegistration::getAccount)
                .collect(Collectors.toList());
    }

    public byte[] getAircraftImage() {
        return aircraftImage;
    }

    public String getAircraftName() {
        return aircraftName;
    }

    public Set<AircraftRegistration> getAircraftRegistrations() {
        return aircraftRegistrations;
    }

    public String getAircraftType() {
        return aircraftType;
    }

    public Integer getId() {
        return id;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public Double getMaximumTakeoffWeight() {
        return maximumTakeoffWeight;
    }

    public WakeTurbulenceCategory getWakeTurbulenceCategory() {
        return wakeTurbulenceCategory;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public void setAircraftImage(byte[] aAircraftImage) {
        aircraftImage = aAircraftImage;
    }

    public void setAircraftName(String aAircraftName) {
        aircraftName = aAircraftName;
    }

    public void setAircraftRegistrations(Set<AircraftRegistration> aAircraftRegistrations) {
        aircraftRegistrations = aAircraftRegistrations;
    }

    public void setAircraftType(String aAircraftType) {
        aircraftType = aAircraftType;
    }

    public void setId(Integer aId) {
        id = aId;
    }

    public void setManufacturer(String aManufacturer) {
        manufacturer = aManufacturer;
    }



    public void setMaximumTakeoffWeight(Double aMaximumTakeoffWeight) {
        maximumTakeoffWeight = aMaximumTakeoffWeight;
    }

    public void setWakeTurbulenceCategory(WakeTurbulenceCategory aWakeTurbulenceCategory) {
        wakeTurbulenceCategory = aWakeTurbulenceCategory;
    }

    @Override
    public String toString() {
        return "AircraftType [id=" + id + ", aircraftType=" + aircraftType + ", aircraftName=" + aircraftName
                + ", manufacturer=" + manufacturer + ", maximumTakeoffWeight=" + maximumTakeoffWeight
                + ", wakeTurbulenceCategory=" + wakeTurbulenceCategory + "]";
    }
}
