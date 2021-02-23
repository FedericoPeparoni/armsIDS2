package ca.ids.abms.modules.accounts;

import ca.ids.abms.util.csv.annotations.CsvProperty;

public class AccountCsvExportModel {

    @CsvProperty(value = "Account Name")
    private String name;

    @CsvProperty(value = "ICAO Code")
    private String icaoCode;

    @CsvProperty(value = "IATA Code")
    private String iataCode;

    @CsvProperty(value = "Contact Name")
    private String aviationBillingContactPersonName;

    private String nationality;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcaoCode() {
        return icaoCode;
    }

    public void setIcaoCode(String icaoCode) {
        this.icaoCode = icaoCode;
    }

    public String getIataCode() {
        return iataCode;
    }

    public void setIataCode(String iataCode) {
        this.iataCode = iataCode;
    }

    public String getAviationBillingContactPersonName() {
        return aviationBillingContactPersonName;
    }

    public void setAviationBillingContactPersonName(String aviationBillingContactPersonName) {
        this.aviationBillingContactPersonName = aviationBillingContactPersonName;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }
}
