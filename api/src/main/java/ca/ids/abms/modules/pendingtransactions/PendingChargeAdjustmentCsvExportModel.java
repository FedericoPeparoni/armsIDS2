package ca.ids.abms.modules.pendingtransactions;

import ca.ids.abms.util.csv.annotations.CsvProperty;

import java.time.LocalDateTime;

public class PendingChargeAdjustmentCsvExportModel {

    @CsvProperty(value = "Date Of Flight", date = true)
    private LocalDateTime date;

    private String flightId;

    private String aerodrome;

    @CsvProperty(value = "Charge Type")
    private String invoiceType;

    @CsvProperty(value = "Details")
    private String chargeDescription;

    @CsvProperty(precision = 2)
    private Double chargeAmount;

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getFlightId() {
        return flightId;
    }

    public void setFlightId(String flightId) {
        this.flightId = flightId;
    }

    public String getAerodrome() {
        return aerodrome;
    }

    public void setAerodrome(String aerodrome) {
        this.aerodrome = aerodrome;
    }

    public String getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(String invoiceType) {
        this.invoiceType = invoiceType;
    }

    public String getChargeDescription() {
        return chargeDescription;
    }

    public void setChargeDescription(String chargeDescription) {
        this.chargeDescription = chargeDescription;
    }

    public Double getChargeAmount() {
        return chargeAmount;
    }

    public void setChargeAmount(Double chargeAmount) {
        this.chargeAmount = chargeAmount;
    }
}
