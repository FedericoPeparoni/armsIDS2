package ca.ids.abms.modules.unspecifiedaircraft;

import ca.ids.abms.modules.translation.Translation;
import ca.ids.abms.modules.util.models.VersionedViewModel;

import javax.validation.constraints.Size;

/**
 * Unspecified aircraft types are identified in flight plans with the identifier ZZZZ being used for the aircraft type.
 * @author c.talpa
 */
public class UnspecifiedAircraftTypeViewModel extends VersionedViewModel {

    private Integer id;

    @Size(max = 255)
    private String textIdentifier;

    @Size(max = 100)
    private String name;

    private Double MTOW;

    private UnspecifiedAircraftTypeStatus status;

    @Size(max = 4)
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
