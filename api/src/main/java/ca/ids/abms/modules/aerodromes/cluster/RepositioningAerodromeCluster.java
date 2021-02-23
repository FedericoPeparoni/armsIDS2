package ca.ids.abms.modules.aerodromes.cluster;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import ca.ids.abms.config.db.SearchableText;
import ca.ids.abms.config.db.validators.UniqueKey;
import ca.ids.abms.modules.exemptions.ExemptionType;
import ca.ids.abms.modules.util.models.VersionedAuditedEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@UniqueKey(columnNames = "repositioningAerodromeClusterName")
public class RepositioningAerodromeCluster extends VersionedAuditedEntity implements ExemptionType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Size(max = 255)
    @SearchableText
    private String repositioningAerodromeClusterName;

    @NotNull
    @Max(100)
    @Min(0)
    private Double enrouteFeesAreExempt;

    @NotNull
    @Max(100)
    @Min(0)
    private Double approachFeesAreExempt;

    @NotNull
    @Max(100)
    @Min(0)
    private Double aerodromeFeesAreExempt;

    @NotNull
    @Max(100)
    @Min(0)
    private Double lateArrivalFeesAreExempt;

    @NotNull
    @Max(100)
    @Min(0)
    private Double lateDepartureFeesAreExempt;

    @NotNull
    @Max(100)
    @Min(0)
    private Double parkingFeesAreExempt;

    @NotNull
    @Max(100)
    @Min(0)
    private Double domesticPax;

    @NotNull
    @Max(100)
    @Min(0)
    private Double internationalPax;

    @NotNull
    @Max(100)
    @Min(0)
    private Double extendedHours;

    @NotNull
    @Size(max = 255)
    @SearchableText
    private String flightNotes;

    @JsonIgnore
    @OneToMany(mappedBy = "repositioningAerodromeCluster")
    private Set<RepositioningAssignedAerodromeCluster> aerodromeIdentifiers = new HashSet<>();

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRepositioningAerodromeClusterName() {
        return repositioningAerodromeClusterName;
    }

    public void setRepositioningAerodromeClusterName(String repositioningAerodromeClusterName) {
        this.repositioningAerodromeClusterName = repositioningAerodromeClusterName;
    }

    public Double getEnrouteFeesAreExempt() {
        return enrouteFeesAreExempt;
    }

    public void setEnrouteFeesAreExempt(Double enrouteFeesAreExempt) {
        this.enrouteFeesAreExempt = enrouteFeesAreExempt;
    }

    public Double getApproachFeesAreExempt() {
        return approachFeesAreExempt;
    }

    public void setApproachFeesAreExempt(Double approachFeesAreExempt) {
        this.approachFeesAreExempt = approachFeesAreExempt;
    }

    public Double getAerodromeFeesAreExempt() {
        return aerodromeFeesAreExempt;
    }

    public void setAerodromeFeesAreExempt(Double aerodromeFeesAreExempt) {
        this.aerodromeFeesAreExempt = aerodromeFeesAreExempt;
    }

    public Double getLateArrivalFeesAreExempt() {
        return lateArrivalFeesAreExempt;
    }

    public void setLateArrivalFeesAreExempt(Double lateArrivalFeesAreExempt) {
        this.lateArrivalFeesAreExempt = lateArrivalFeesAreExempt;
    }

    public Double getLateDepartureFeesAreExempt() {
        return lateDepartureFeesAreExempt;
    }

    public void setLateDepartureFeesAreExempt(Double lateDepartureFeesAreExempt) {
        this.lateDepartureFeesAreExempt = lateDepartureFeesAreExempt;
    }

    public Double getParkingFeesAreExempt() {
        return parkingFeesAreExempt;
    }

    public void setParkingFeesAreExempt(Double parkingFeesAreExempt) {
        this.parkingFeesAreExempt = parkingFeesAreExempt;
    }

    public Double getDomesticPax() {
        return domesticPax;
    }

    public void setDomesticPax(Double domesticPax) {
        this.domesticPax = domesticPax;
    }

    public Double getInternationalPax() {
        return internationalPax;
    }

    public void setInternationalPax(Double internationalPax) {
        this.internationalPax = internationalPax;
    }

    public Double getExtendedHours() {
        return extendedHours;
    }

    public void setExtendedHours(Double extendedHours) {
        this.extendedHours = extendedHours;
    }

    public String getFlightNotes() {
        return flightNotes;
    }

    public void setFlightNotes(String flightNotes) {
        this.flightNotes = flightNotes;
    }

    public Set<RepositioningAssignedAerodromeCluster> getAerodromeIdentifiers() {
        return aerodromeIdentifiers;
    }

    public void setAerodromeIdentifiers(Set<RepositioningAssignedAerodromeCluster> aerodromeIdentifiers) {
        this.aerodromeIdentifiers = aerodromeIdentifiers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        RepositioningAerodromeCluster item = (RepositioningAerodromeCluster) o;

        return id != null ? id.equals(item.id) : item.id == null;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, repositioningAerodromeClusterName, enrouteFeesAreExempt,
            approachFeesAreExempt, aerodromeFeesAreExempt, lateArrivalFeesAreExempt,
            lateDepartureFeesAreExempt, parkingFeesAreExempt, domesticPax, internationalPax,
            extendedHours, flightNotes, aerodromeIdentifiers);
    }

    @Override
    public String toString() {
        return "RepositioningAerodromeCluster{" +
            "id=" + id +
            ", repositioningAerodromeClusterName='" + repositioningAerodromeClusterName + '\'' +
            ", enrouteFeesAreExempt=" + enrouteFeesAreExempt +
            ", approachFeesAreExempt=" + approachFeesAreExempt +
            ", aerodromeFeesAreExempt=" + aerodromeFeesAreExempt +
            ", lateArrivalFeesAreExempt=" + lateArrivalFeesAreExempt +
            ", lateDepartureFeesAreExempt=" + lateDepartureFeesAreExempt +
            ", parkingFeesAreExempt=" + parkingFeesAreExempt +
            ", domesticPax=" + domesticPax +
            ", internationalPax=" + internationalPax +
            ", extendedHours=" + extendedHours +
            ", flightNotes='" + flightNotes + '\'' +
            ", aerodromeIdentifiers=" + aerodromeIdentifiers +
            '}';
    }

    @Override
    public Double enrouteChargeExemption() {
        return enrouteFeesAreExempt;
    }

    @Override
    public Double lateArrivalChargeExemption() {
        return lateArrivalFeesAreExempt;
    }

    @Override
    public Double lateDepartureChargeExemption() {
        return lateDepartureFeesAreExempt;
    }

    @Override
    public Double parkingChargeExemption() {
        return parkingFeesAreExempt;
    }

    @Override
    public Double approachChargeExemption() {
        return approachFeesAreExempt;
    }

    @Override
    public Double aerodromeChargeExemption() {
        return aerodromeFeesAreExempt;
    }

    @Override
    public Double domesticPaxChargeExemption() {
        return domesticPax;
    }

    @Override
    public Double internationalPaxChargeExemption() {
        return internationalPax;
    }

    @Override
    public Double extendedHoursSurchargeExemption() {
        return extendedHours;
    }

    @Override
    public String flightNoteChargeExemption() {
        return flightNotes;
    }
}
