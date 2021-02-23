package ca.ids.abms.modules.interestrates;

import ca.ids.abms.modules.interestrates.enumerate.AppliedRate;
import ca.ids.abms.modules.interestrates.enumerate.SpecifiedRate;
import ca.ids.abms.util.csv.annotations.CsvProperty;

import java.time.LocalDate;

public class InterestRateCsvExportModel {

    @CsvProperty(date = true)
    private LocalDate startDate;

    @CsvProperty(date = true)
    private LocalDate endDate;

    private SpecifiedRate defaultInterestSpecification;

    private AppliedRate defaultInterestApplication;

    @CsvProperty(value = "Default Foreign Interest Percentage", precision = 2)
    private Double defaultForeignInterestSpecifiedPercentage;

    @CsvProperty(value = "Default National Interest Percentage", precision = 2)
    private Double defaultNationalInterestSpecifiedPercentage;

    private Integer defaultInterestGracePeriod;

    private Integer punitiveInterestGracePeriod;

    private SpecifiedRate punitiveInterestSpecification;

    private AppliedRate punitiveInterestApplication;

    @CsvProperty(value = "Punitive Interest Percentage", precision = 2)
    private Double punitiveInterestSpecifiedPercentage;

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

    public Integer getDefaultInterestGracePeriod() {
        return defaultInterestGracePeriod;
    }

    public void setDefaultInterestGracePeriod(Integer defaultInterestGracePeriod) {
        this.defaultInterestGracePeriod = defaultInterestGracePeriod;
    }

    public Integer getPunitiveInterestGracePeriod() {
        return punitiveInterestGracePeriod;
    }

    public void setPunitiveInterestGracePeriod(Integer punitiveInterestGracePeriod) {
        this.punitiveInterestGracePeriod = punitiveInterestGracePeriod;
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

    public Double getPunitiveInterestSpecifiedPercentage() {
        return punitiveInterestSpecifiedPercentage;
    }

    public void setPunitiveInterestSpecifiedPercentage(Double punitiveInterestSpecifiedPercentage) {
        this.punitiveInterestSpecifiedPercentage = punitiveInterestSpecifiedPercentage;
    }
}
