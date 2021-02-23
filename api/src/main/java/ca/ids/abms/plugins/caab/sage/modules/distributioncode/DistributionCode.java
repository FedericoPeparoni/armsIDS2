package ca.ids.abms.plugins.caab.sage.modules.distributioncode;

import ca.ids.abms.modules.util.models.VersionedAuditedEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "caab_sage_distribution_codes")
public class DistributionCode extends VersionedAuditedEntity {

    public enum FlightMovementChargeType {
        ENROUTE_CHARGES,
        DOMESTIC_PASSENGER_CHARGES,
        INTERNATIONAL_PASSENGER_CHARGES,
        LANDING_CHARGES,
        PARKING_CHARGES
    }

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "code", length = 50, unique = true)
    @NotNull
    private String code;

    @Column(name = "abbrev", length = 20)
    @NotNull
    private String abbrev;

    @Column(name = "description", length = 100)
    private String description;

    @Column(name = "category", length = 20)
    @NotNull
    private String category;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAbbrev() {
        return abbrev;
    }

    public void setAbbrev(String abbrev) {
        this.abbrev = abbrev;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
