package ca.ids.abms.modules.aerodromes;

import ca.ids.abms.util.csv.annotations.CsvProperty;

public class AerodromeCategoryCsvExportModel {

    private String categoryName;

    @CsvProperty(value = "Int Pass Fee - Adult", precision = 2)
    private Double internationalPassengerFeeAdult;

    @CsvProperty(value = "Int Pass Fee - Child", precision = 2)
    private Double internationalPassengerFeeChild;

    @CsvProperty(value = "Dom Pass Fee - Adult", precision = 2)
    private Double domesticPassengerFeeAdult;

    @CsvProperty(value = "Dom Pass Fee - Child", precision = 2)
    private Double domesticPassengerFeeChild;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Double getInternationalPassengerFeeAdult() {
        return internationalPassengerFeeAdult;
    }

    public void setInternationalPassengerFeeAdult(Double internationalPassengerFeeAdult) {
        this.internationalPassengerFeeAdult = internationalPassengerFeeAdult;
    }

    public Double getInternationalPassengerFeeChild() {
        return internationalPassengerFeeChild;
    }

    public void setInternationalPassengerFeeChild(Double internationalPassengerFeeChild) {
        this.internationalPassengerFeeChild = internationalPassengerFeeChild;
    }

    public Double getDomesticPassengerFeeAdult() {
        return domesticPassengerFeeAdult;
    }

    public void setDomesticPassengerFeeAdult(Double domesticPassengerFeeAdult) {
        this.domesticPassengerFeeAdult = domesticPassengerFeeAdult;
    }

    public Double getDomesticPassengerFeeChild() {
        return domesticPassengerFeeChild;
    }

    public void setDomesticPassengerFeeChild(Double domesticPassengerFeeChild) {
        this.domesticPassengerFeeChild = domesticPassengerFeeChild;
    }
}
