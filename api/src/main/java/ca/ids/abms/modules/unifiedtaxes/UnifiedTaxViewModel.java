package ca.ids.abms.modules.unifiedtaxes;

import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;

import ca.ids.abms.modules.util.models.VersionedViewModel;

public class UnifiedTaxViewModel extends VersionedViewModel {

    private Integer id;

    private LocalDateTime fromManufactureYear;

    private LocalDateTime toManufactureYear;

    private UnifiedTaxValidity validity;

    @NotNull
    private Integer ars;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getFromManufactureYear() {
        return fromManufactureYear;
    }

    public void setFromManufactureYear(LocalDateTime fromManufactureYear) {
        this.fromManufactureYear = fromManufactureYear;
    }

    public LocalDateTime getToManufactureYear() {
        return toManufactureYear;
    }

    public void setToManufactureYear(LocalDateTime toManufactureYear) {
        this.toManufactureYear = toManufactureYear;
    }

    public Integer getArs() {
        return ars;
    }

    public void setArs(Integer ars) {
        this.ars = ars;
    }

    public UnifiedTaxValidity getValidity() {
        return validity;
    }

    public void setValidity(UnifiedTaxValidity validity) {
        this.validity = validity;
    }

}