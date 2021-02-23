package ca.ids.abms.modules.bankaccount;

import ca.ids.abms.util.csv.annotations.CsvProperty;

public class BankAccountCsvExportModel {

    @CsvProperty(value = "Bank Name")
    private String name;

    @CsvProperty(value = "Account Number")
    private String number;

    private String currency;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
