package ca.ids.abms.modules.statistics;

import ca.ids.abms.modules.util.models.VersionedViewModel;

public class RevenueStatisticViewModel extends VersionedViewModel {

    private Integer id;

    private String name;

    private String analysisType;

    private String billingCentres;

    private String accounts;

    private String aerodromes;

    private String paymentMode;

    private String chargeClass;

    private String chargeCategory;

    private String chargeType;

    private String temporalGroup;

    private String groupBy;

    private String sort;

    private Boolean fiscalYear;

    private String value;

    private String chartType;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAnalysisType() {
        return analysisType;
    }

    public void setAnalysisType(String analysisType) {
        this.analysisType = analysisType;
    }

    public String getBillingCentres() {
        return billingCentres;
    }

    public void setBillingCentres(String billingCentres) {
        this.billingCentres = billingCentres;
    }

    public String getAccounts() {
        return accounts;
    }

    public void setAccounts(String accounts) {
        this.accounts = accounts;
    }

    public String getAerodromes() {
        return aerodromes;
    }

    public void setAerodromes(String aerodromes) {
        this.aerodromes = aerodromes;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getChargeClass() {
        return chargeClass;
    }

    public void setChargeClass(String chargeClass) {
        this.chargeClass = chargeClass;
    }

    public String getChargeCategory() {
        return chargeCategory;
    }

    public void setChargeCategory(String chargeCategory) {
        this.chargeCategory = chargeCategory;
    }

    public String getChargeType() {
        return chargeType;
    }

    public void setChargeType(String chargeType) {
        this.chargeType = chargeType;
    }

    public String getTemporalGroup() {
        return temporalGroup;
    }

    public void setTemporalGroup(String temporalGroup) {
        this.temporalGroup = temporalGroup;
    }

    public String getGroupBy() {
        return groupBy;
    }

    public void setGroupBy(String groupBy) {
        this.groupBy = groupBy;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public Boolean getFiscalYear() {
        return fiscalYear;
    }

    public void setFiscalYear(Boolean fiscalYear) {
        this.fiscalYear = fiscalYear;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getChartType() {
        return chartType;
    }

    public void setChartType(String chartType) {
        this.chartType = chartType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RevenueStatistic)) return false;

        RevenueStatistic that = (RevenueStatistic) o;

        if (!getId().equals(that.getId())) return false;
        return getName().equals(that.getName());
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "RevenueStatistic{" +
            "id=" + id +
            ", name='" + name + '\'' +
            '}';
    }
}
