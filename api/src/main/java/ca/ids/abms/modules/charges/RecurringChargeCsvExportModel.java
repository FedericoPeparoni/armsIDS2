package ca.ids.abms.modules.charges;

import ca.ids.abms.util.csv.annotations.CsvProperty;

import java.time.LocalDateTime;

public class RecurringChargeCsvExportModel {

    private String serviceChargeCatalogue;

    private String account;

    @CsvProperty(date = true)
    private LocalDateTime startDate;

    @CsvProperty(value = "Expected End Date", date = true)
    private LocalDateTime endDate;

    public String getServiceChargeCatalogue() {
        return serviceChargeCatalogue;
    }

    public void setServiceChargeCatalogue(String serviceChargeCatalogue) {
        this.serviceChargeCatalogue = serviceChargeCatalogue;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }
}
