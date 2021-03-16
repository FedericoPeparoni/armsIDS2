package ca.ids.abms.modules.unifiedtaxes;

import java.time.LocalDateTime;

import ca.ids.abms.modules.util.models.VersionedViewModel;

public class UnifiedTaxValidityViewModel extends VersionedViewModel {

    private Integer id;

    private LocalDateTime toValidityYear;

    private LocalDateTime fromValidityYear;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getToValidityYear() {
        return toValidityYear;
    }

    public void setToValidityYear(LocalDateTime toValidityYear) {
        this.toValidityYear = toValidityYear;
    }

    public LocalDateTime getFromValidityYear() {
        return fromValidityYear;
    }

    public void setFromValidityYear(LocalDateTime fromValidityYear) {
        this.fromValidityYear = fromValidityYear;
    }

}