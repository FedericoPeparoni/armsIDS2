package ca.ids.abms.modules.revenueprojection;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RevenueProjection {

    // applied percentage increase/descrease to revenue charges
    @JsonProperty("charges_aerodrome")
    private Double chargesAerodrome;

    @JsonProperty("charges_approach")
    private Double chargesApproach;

    @JsonProperty("charges_late_arrival")
    private Double chargesLateArrival;

    @JsonProperty("charges_late_departure")
    private Double chargesLateDeparture;

    @JsonProperty("charges_passenger")
    private Double chargesPassenger;

    // applied percentage increase/descrease to volume
    @JsonProperty("charges_vol_flights")
    private Double chargesVolFlights;

    @JsonProperty("charges_vol_passengers")
    private Double chargesVolPassengers;

    // applied formula to charges
    @JsonProperty("domestic_formula")
    private String domesticFormula;

    @JsonProperty("regional_departure_formula")
    private String regionalDepartureFormula;

    @JsonProperty("regional_arrival_formula")
    private String regionalArrivalFormula;

    @JsonProperty("regional_overflight_formula")
    private String regionalOverflightFormula;

    @JsonProperty("international_departure_formula")
    private String internationalDepartureFormula;

    @JsonProperty("international_arrival_formula")
    private String internationalArrivalFormula;

    @JsonProperty("international_overflight_formula")
    private String internationalOverflightFormula;

    @JsonProperty("domestic_d_factor_formula")
    private String domesticDFactorFormula;

    @JsonProperty("reg_dep_d_factor_formula")
    private String regDepDFactorFormula;

    @JsonProperty("reg_arr_d_factor_formula")
    private String  regArrDFactorFormula;

    @JsonProperty("reg_ovr_d_factor_formula")
    private String regOvrDFactorFormula;

    @JsonProperty("int_dep_d_factor_formula")
    private String intDepDFactorFormula;

    @JsonProperty("int_arr_d_factor_formula")
    private String intArrDFactorFormula;

    @JsonProperty("int_ovr_d_factor_formula")
    private String intOvrDFactorFormula;

    @JsonProperty("w_factor_formula")
    private String wFactorFormula;

    @JsonProperty("upper_limit")
    private Double upperLimit;

    // report parameters
    @JsonProperty("time_period")
    private String timePeriod;

    @JsonProperty("modified_only")
    private String modifiedOnly;

    @JsonProperty("format")
    private String format;

    public Double getChargesAerodrome() {
        return chargesAerodrome;
    }

    public void setChargesAerodrome(Double chargesAerodrome) {
        this.chargesAerodrome = chargesAerodrome;
    }

    public Double getChargesApproach() {
        return chargesApproach;
    }

    public void setChargesApproach(Double chargesApproach) {
        this.chargesApproach = chargesApproach;
    }

    public Double getChargesLateArrival() {
        return chargesLateArrival;
    }

    public void setChargesLateArrival(Double chargesLateArrival) {
        this.chargesLateArrival = chargesLateArrival;
    }

    public Double getChargesLateDeparture() {
        return chargesLateDeparture;
    }

    public void setChargesLateDeparture(Double chargesLateDeparture) {
        this.chargesLateDeparture = chargesLateDeparture;
    }

    public Double getChargesPassenger() {
        return chargesPassenger;
    }

    public void setChargesPassenger(Double chargesPassenger) {
        this.chargesPassenger = chargesPassenger;
    }

    public Double getChargesVolFlights() {
        return chargesVolFlights;
    }

    public void setChargesVolFlights(Double chargesVolFlights) {
        this.chargesVolFlights = chargesVolFlights;
    }

    public Double getChargesVolPassengers() {
        return chargesVolPassengers;
    }

    public void setChargesVolPassengers(Double chargesVolPassengers) {
        this.chargesVolPassengers = chargesVolPassengers;
    }

    public String getDomesticFormula() {
        return domesticFormula;
    }

    public void setDomesticFormula(String domesticFormula) {
        this.domesticFormula = domesticFormula;
    }

    public String getDomesticDFactorFormula() {
        return domesticDFactorFormula;
    }

    public void setDomesticDFactorFormula(String domesticDFactorFormula) {
        this.domesticDFactorFormula = domesticDFactorFormula;
    }

    public String getInternationalArrivalFormula() {
        return internationalArrivalFormula;
    }

    public void setInternationalArrivalFormula(String internationalArrivalFormula) {
        this.internationalArrivalFormula = internationalArrivalFormula;
    }

    public String getIntArrDFactorFormula() {
        return intArrDFactorFormula;
    }

    public void setIntArrDFactorFormula(String intArrDFactorFormula) {
        this.intArrDFactorFormula = intArrDFactorFormula;
    }

    public String getInternationalDepartureFormula() {
        return internationalDepartureFormula;
    }

    public void setInternationalDepartureFormula(String internationalDepartureFormula) {
        this.internationalDepartureFormula = internationalDepartureFormula;
    }

    public String getIntDepDFactorFormula() {
        return intDepDFactorFormula;
    }

    public void setIntDepDFactorFormula(String intDepDFactorFormula) {
        this.intDepDFactorFormula = intDepDFactorFormula;
    }

    public String getInternationalOverflightFormula() {
        return internationalOverflightFormula;
    }

    public void setInternationalOverflightFormula(String internationalOverflightFormula) {
        this.internationalOverflightFormula = internationalOverflightFormula;
    }

    public String getIntOvrDFactorFormula() {
        return intOvrDFactorFormula;
    }

    public void setIntOvrDFactorFormula(String intOvrDFactorFormula) {
        this.intOvrDFactorFormula = intOvrDFactorFormula;
    }

    public Double getUpperLimit() {
        return upperLimit;
    }

    public void setUpperLimit(Double upperLimit) {
        this.upperLimit = upperLimit;
    }

    public String getRegionalArrivalFormula() {
        return regionalArrivalFormula;
    }

    public void setRegionalArrivalFormula(String regionalArrivalFormula) {
        this.regionalArrivalFormula = regionalArrivalFormula;
    }

    public String getRegArrDFactorFormula() {
        return regArrDFactorFormula;
    }

    public void setRegArrDFactorFormula(String regArrDFactorFormula) {
        this.regArrDFactorFormula = regArrDFactorFormula;
    }

    public String getRegionalDepartureFormula() {
        return regionalDepartureFormula;
    }

    public void setRegionalDepartureFormula(String regionalDepartureFormula) {
        this.regionalDepartureFormula = regionalDepartureFormula;
    }

    public String getRegDepDFactorFormula() {
        return regDepDFactorFormula;
    }

    public void setRegDepDFactorFormula(String regDepDFactorFormula) {
        this.regDepDFactorFormula = regDepDFactorFormula;
    }

    public String getRegionalOverflightFormula() {
        return regionalOverflightFormula;
    }

    public void setRegionalOverflightFormula(String regionalOverflightFormula) {
        this.regionalOverflightFormula = regionalOverflightFormula;
    }

    public String getRegOvrDFactorFormula() {
        return regOvrDFactorFormula;
    }

    public void setRegOvrDFactorFormula(String regOvrDFactorFormula) {
        this.regOvrDFactorFormula = regOvrDFactorFormula;
    }

    public String getwFactorFormula() {
        return wFactorFormula;
    }

    public void setwFactorFormula(String wFactorFormula) {
        this.wFactorFormula = wFactorFormula;
    }

    public String getTimePeriod() {
        return timePeriod;
    }

    public void setTimePeriod(String timePeriod) {
        this.timePeriod = timePeriod;
    }

    public String getModifiedOnly() {
        return modifiedOnly;
    }

    public void setModifiedOnly(String modifiedOnly) {
        this.modifiedOnly = modifiedOnly;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    @Override
    public String toString() {
        return "RevenueProjection {" +
            "charges_aerodrome=" + chargesAerodrome +
            ", charges_approach=" + chargesApproach +
            ", charges_late_arrival=" + chargesLateArrival +
            ", charges_late_departure=" + chargesLateDeparture +
            ", charges_passenger=" + chargesPassenger +
            ", charges_vol_flights=" + chargesVolFlights +
            ", charges_vol_passengers=" + chargesVolPassengers +
            ", charges_enroute_upper_limit=" + upperLimit +
            ", domestic_formula='" + domesticFormula + "'" +
            ", domestic_d_factor_formula='" + domesticDFactorFormula + "'" +
            ", international_arrival_formula='" + internationalArrivalFormula + "'" +
            ", int_arr_d_factor_formula='" + intArrDFactorFormula + "'" +
            ", international_departure_formula='" + internationalDepartureFormula + "'" +
            ", int_dep_d_factor_formula='" + intDepDFactorFormula + "'" +
            ", international_overflight_formula='" + internationalOverflightFormula + "'" +
            ", int_ovr_d_factor_formula='" + intOvrDFactorFormula + "'" +
            ", regional_arrival_formula='" + regionalArrivalFormula + "'" +
            ", reg_arr_d_factor_formula='" + regArrDFactorFormula + "'" +
            ", regional_departure_formula='" + regionalDepartureFormula + "'" +
            ", reg_dep_d_factor_formula='" + regDepDFactorFormula + "'" +
            ", regional_overflight_formula='" + regionalOverflightFormula + "'" +
            ", reg_ovr_d_factor_formula='" + regOvrDFactorFormula + "'" +
            ", w_factor_formula='" + wFactorFormula + "'" +
            ", time_period='" + timePeriod + "'" +
            ", modified_only='" + modifiedOnly + "'" +
            ", format='" + format + "'" +
        "}";
    }

}
