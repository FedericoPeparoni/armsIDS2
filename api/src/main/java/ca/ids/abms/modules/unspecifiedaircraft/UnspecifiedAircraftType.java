package ca.ids.abms.modules.unspecifiedaircraft;

import ca.ids.abms.config.db.SearchableText;
import ca.ids.abms.config.db.validators.UniqueKey;
import ca.ids.abms.modules.util.models.VersionedAuditedEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Unspecified aircraft types are identified in flight plans with the identifier ZZZZ being used for the aircraft type.
 * @author c.talpa
 */
@Entity
@UniqueKey(columnNames = "textIdentifier")
public class UnspecifiedAircraftType extends VersionedAuditedEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Size(max = 255)
    @Column(unique = true)
    @SearchableText
    private String textIdentifier;

    @Size(max = 100)
    @SearchableText
    private String name;

    private Double MTOW;

    @NotNull
    @Enumerated(EnumType.STRING)
    @SearchableText (exactMatch = true)
    private UnspecifiedAircraftTypeStatus status;

    @Size(max = 4)
    @SearchableText
    private String aircraftType;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTextIdentifier() {
        return textIdentifier;
    }

    public void setTextIdentifier(String textIdentifier) {
        this.textIdentifier = textIdentifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getMTOW() {
        return MTOW;
    }

    public void setMTOW(Double MTOW) {
        this.MTOW = MTOW;
    }

    public UnspecifiedAircraftTypeStatus getStatus() {
        return status;
    }

    public void setStatus(UnspecifiedAircraftTypeStatus status) {
        this.status = status;
    }

    public String getAircraftType() {
        return aircraftType;
    }

    public void setAircraftType(String aircraftType) {
        this.aircraftType = aircraftType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        UnspecifiedAircraftType that = (UnspecifiedAircraftType) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }


    @Override
    public String toString() {
        return "UnspecifiedAircraftType{" +
            "id=" + id +
            ", textIdentifier='" + textIdentifier + '\'' +
            ", name='" + name + '\'' +
            ", aircraftType='" + aircraftType + '\'' +
            ", MTOW=" + MTOW +
            ", status='" + status + '\'' +
            '}';
    }
}
