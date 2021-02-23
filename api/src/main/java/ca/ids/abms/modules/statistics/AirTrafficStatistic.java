package ca.ids.abms.modules.statistics;

import ca.ids.abms.modules.util.models.VersionedAuditedEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class AirTrafficStatistic extends VersionedAuditedEntity {

    private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, name = "name")
    @NotNull
    private String name;

    @Column(name = "aerodromes")
    private String aerodromes;

    @Column(name = "aircraft_types")
    private String aircraftTypes;

    @Column(name = "mtow_factor_class")
    private String mtowFactorClass;

    @Column(name = "mtow_categories")
    private String mtowCategories;

    @Column(name = "billing_centres")
    private String billingCentres;

    @Column(name = "accounts")
    private String accounts;

    @Column(name = "temporal_group")
    private String temporalGroup;

    @Column(name = "flight_types")
    private String flightTypes;

    @Column(name = "flight_scopes")
    private String flightScopes;

    @Column(name = "flight_categories")
    private String flightCategories;

    @Column(name = "flight_rules")
    private String flightRules;

    @Column(name = "flight_levels")
    private String flightLevels;

    @Column(name = "routes")
    private String routes;

    @Column(name = "sort")
    private String sort;

    @Column(name = "group_by")
    private String groupBy;

    @Column(name = "value")
    private String value;

    @Column(name = "revenue_category")
    private String revenueCategory;

    @Column(name = "chart_type")
    private String chartType;

    @Column(name = "fiscal_year")
    private Boolean fiscalYear;

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

    public String getAerodromes() {
        return aerodromes;
    }

    public void setAerodromes(String aerodromes) {
        this.aerodromes = aerodromes;
    }

    public String getAircraftTypes() {
        return aircraftTypes;
    }

    public void setAircraftTypes(String aircraftTypes) {
        this.aircraftTypes = aircraftTypes;
    }

    public String getMtowFactorClass() {
        return mtowFactorClass;
    }

    public void setMtowFactorClass(String mtowFactorClass) {
        this.mtowFactorClass = mtowFactorClass;
    }

    public String getMtowCategories() {
        return mtowCategories;
    }

    public void setMtowCategories(String mtowCategories) {
        this.mtowCategories = mtowCategories;
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

    public String getTemporalGroup() {
        return temporalGroup;
    }

    public void setTemporalGroup(String temporalGroup) {
        this.temporalGroup = temporalGroup;
    }

    public String getFlightTypes() {
        return flightTypes;
    }

    public void setFlightTypes(String flightTypes) {
        this.flightTypes = flightTypes;
    }

    public String getFlightScopes() {
        return flightScopes;
    }

    public void setFlightScopes(String flightScopes) {
        this.flightScopes = flightScopes;
    }

    public String getFlightCategories() {
        return flightCategories;
    }

    public void setFlightCategories(String flightCategories) {
        this.flightCategories = flightCategories;
    }

    public String getFlightRules() {
        return flightRules;
    }

    public void setFlightRules(String flightRules) {
        this.flightRules = flightRules;
    }

    public String getFlightLevels() {
        return flightLevels;
    }

    public void setFlightLevels(String flightLevels) {
        this.flightLevels = flightLevels;
    }

    public String getRoutes() {
        return routes;
    }

    public void setRoutes(String routes) {
        this.routes = routes;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getGroupBy() {
        return groupBy;
    }

    public void setGroupBy(String groupBy) {
        this.groupBy = groupBy;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getRevenueCategory() {
        return revenueCategory;
    }

    public void setRevenueCategory(String revenueCategory) {
        this.revenueCategory = revenueCategory;
    }

    public String getChartType() {
        return chartType;
    }

    public void setChartType(String chartType) {
        this.chartType = chartType;
    }

    public Boolean getFiscalYear() {
        return fiscalYear;
    }

    public void setFiscalYear(Boolean fiscalYear) {
        this.fiscalYear = fiscalYear;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        AirTrafficStatistic that = (AirTrafficStatistic) o;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "AirTrafficStatistic{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", aerodromes='" + aerodromes + '\'' +
            ", aircraftTypes='" + aircraftTypes + '\'' +
            ", mtowFactorClass='" + mtowFactorClass + '\'' +
            ", mtowCategories='" + mtowCategories + '\'' +
            ", billingCentres='" + billingCentres + '\'' +
            ", accounts='" + accounts + '\'' +
            ", temporalGroup='" + temporalGroup + '\'' +
            ", flightTypes='" + flightTypes + '\'' +
            ", flightScopes='" + flightScopes + '\'' +
            ", flightCategories='" + flightCategories + '\'' +
            ", flightRules='" + flightRules + '\'' +
            ", flightLevels='" + flightLevels + '\'' +
            ", routes='" + routes + '\'' +
            ", sort='" + sort + '\'' +
            ", groupBy='" + groupBy + '\'' +
            ", value='" + value + '\'' +
            ", revenueCategory='" + revenueCategory + '\'' +
            ", chartType='" + chartType + '\'' +
            ", fiscalYear='" + fiscalYear + '\'' +
            '}';
    }
}
