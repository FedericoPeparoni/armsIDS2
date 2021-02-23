package ca.ids.abms.modules.interestrates;

import ca.ids.abms.modules.interestrates.enumerate.AppliedRate;
import ca.ids.abms.modules.interestrates.enumerate.SpecifiedRate;
import ca.ids.abms.modules.util.models.VersionedViewModel;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Objects;

public class InterestRateViewModel extends VersionedViewModel {

    private Integer id;

    @NotNull
    private SpecifiedRate defaultInterestSpecification;

    @NotNull
    private AppliedRate defaultInterestApplication;

    @NotNull
    private Integer defaultInterestGracePeriod;

    @NotNull
    private Double defaultForeignInterestSpecifiedPercentage;

    @NotNull
    private Double defaultNationalInterestSpecifiedPercentage;

    @NotNull
    private Double defaultForeignInterestAppliedPercentage;

    @NotNull
    private Double defaultNationalInterestAppliedPercentage;

    @NotNull
    private SpecifiedRate punitiveInterestSpecification;

    @NotNull
    private AppliedRate punitiveInterestApplication;

    @NotNull
    private Integer punitiveInterestGracePeriod;

    @NotNull
    private Double punitiveInterestSpecifiedPercentage;

    @NotNull
    private Double punitiveInterestAppliedPercentage;

    @NotNull
    private LocalDate startDate;

    private LocalDate endDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public SpecifiedRate getDefaultInterestSpecification() {
        return defaultInterestSpecification;
    }

    public void setDefaultInterestSpecification(SpecifiedRate defaultInterestSpecification) {
        this.defaultInterestSpecification = defaultInterestSpecification;
    }

    public AppliedRate getDefaultInterestApplication() {
        return defaultInterestApplication;
    }

    public void setDefaultInterestApplication(AppliedRate defaultInterestApplication) {
        this.defaultInterestApplication = defaultInterestApplication;
    }

    public Integer getDefaultInterestGracePeriod() {
        return defaultInterestGracePeriod;
    }

    public void setDefaultInterestGracePeriod(Integer defaultInterestGracePeriod) {
        this.defaultInterestGracePeriod = defaultInterestGracePeriod;
    }

    public Double getDefaultForeignInterestSpecifiedPercentage() {
        return defaultForeignInterestSpecifiedPercentage;
    }

    public void setDefaultForeignInterestSpecifiedPercentage(Double defaultForeignInterestSpecifiedPercentage) {
        this.defaultForeignInterestSpecifiedPercentage = defaultForeignInterestSpecifiedPercentage;
    }

    public Double getDefaultNationalInterestSpecifiedPercentage() {
        return defaultNationalInterestSpecifiedPercentage;
    }

    public void setDefaultNationalInterestSpecifiedPercentage(Double defaultNationalInterestSpecifiedPercentage) {
        this.defaultNationalInterestSpecifiedPercentage = defaultNationalInterestSpecifiedPercentage;
    }

    public Double getDefaultForeignInterestAppliedPercentage() {
        return defaultForeignInterestAppliedPercentage;
    }

    public void setDefaultForeignInterestAppliedPercentage(Double defaultForeignInterestAppliedPercentage) {
        this.defaultForeignInterestAppliedPercentage = defaultForeignInterestAppliedPercentage;
    }

    public Double getDefaultNationalInterestAppliedPercentage() {
        return defaultNationalInterestAppliedPercentage;
    }

    public void setDefaultNationalInterestAppliedPercentage(Double defaultNationalInterestAppliedPercentage) {
        this.defaultNationalInterestAppliedPercentage = defaultNationalInterestAppliedPercentage;
    }

    public SpecifiedRate getPunitiveInterestSpecification() {
        return punitiveInterestSpecification;
    }

    public void setPunitiveInterestSpecification(SpecifiedRate punitiveInterestSpecification) {
        this.punitiveInterestSpecification = punitiveInterestSpecification;
    }

    public AppliedRate getPunitiveInterestApplication() {
        return punitiveInterestApplication;
    }

    public void setPunitiveInterestApplication(AppliedRate punitiveInterestApplication) {
        this.punitiveInterestApplication = punitiveInterestApplication;
    }

    public Integer getPunitiveInterestGracePeriod() {
        return punitiveInterestGracePeriod;
    }

    public void setPunitiveInterestGracePeriod(Integer punitiveInterestGracePeriod) {
        this.punitiveInterestGracePeriod = punitiveInterestGracePeriod;
    }

    public Double getPunitiveInterestSpecifiedPercentage() {
        return punitiveInterestSpecifiedPercentage;
    }

    public void setPunitiveInterestSpecifiedPercentage(Double punitiveInterestSpecifiedPercentage) {
        this.punitiveInterestSpecifiedPercentage = punitiveInterestSpecifiedPercentage;
    }

    public Double getPunitiveInterestAppliedPercentage() {
        return punitiveInterestAppliedPercentage;
    }

    public void setPunitiveInterestAppliedPercentage(Double punitiveInterestAppliedPercentage) {
        this.punitiveInterestAppliedPercentage = punitiveInterestAppliedPercentage;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InterestRate that = (InterestRate) o;
        return Objects.equals(id, that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "InterestRateViewModel{" +
            "id=" + id +
            ", defaultInterestSpecification=" + defaultInterestSpecification +
            ", defaultInterestApplication=" + defaultInterestApplication +
            ", defaultInterestGracePeriod=" + defaultInterestGracePeriod +
            ", defaultForeignInterestSpecifiedPercentage=" + defaultForeignInterestSpecifiedPercentage +
            ", defaultNationalInterestSpecifiedPercentage=" + defaultNationalInterestSpecifiedPercentage +
            ", defaultForeignInterestAppliedPercentage=" + defaultForeignInterestAppliedPercentage +
            ", defaultNationalInterestAppliedPercentage=" + defaultNationalInterestAppliedPercentage +
            ", punitiveInterestSpecification=" + punitiveInterestSpecification +
            ", punitiveInterestApplication=" + punitiveInterestApplication +
            ", punitiveInterestGracePeriod=" + punitiveInterestGracePeriod +
            ", punitiveInterestSpecifiedPercentage=" + punitiveInterestSpecifiedPercentage +
            ", punitiveInterestAppliedPercentage=" + punitiveInterestAppliedPercentage +
            ", startDate=" + startDate +
            ", endDate=" + endDate +
            '}';
    }
}
