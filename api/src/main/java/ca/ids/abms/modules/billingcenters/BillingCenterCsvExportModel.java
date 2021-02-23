package ca.ids.abms.modules.billingcenters;

import ca.ids.abms.util.csv.annotations.CsvProperty;

public class BillingCenterCsvExportModel {

    private String name;

    @CsvProperty(value = "Headquarters")
    private boolean hq;

    @CsvProperty(value = "Invoice Prefix")
    private String prefixInvoiceNumber;

    private Integer invoiceSequenceNumber;

    @CsvProperty(value = "Receipt Prefix")
    private String prefixReceiptNumber;

    private Integer receiptSequenceNumber;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isHq() {
        return hq;
    }

    public void setHq(boolean hq) {
        this.hq = hq;
    }

    public String getPrefixInvoiceNumber() {
        return prefixInvoiceNumber;
    }

    public void setPrefixInvoiceNumber(String prefixInvoiceNumber) {
        this.prefixInvoiceNumber = prefixInvoiceNumber;
    }

    public Integer getInvoiceSequenceNumber() {
        return invoiceSequenceNumber;
    }

    public void setInvoiceSequenceNumber(Integer invoiceSequenceNumber) {
        this.invoiceSequenceNumber = invoiceSequenceNumber;
    }

    public String getPrefixReceiptNumber() {
        return prefixReceiptNumber;
    }

    public void setPrefixReceiptNumber(String prefixReceiptNumber) {
        this.prefixReceiptNumber = prefixReceiptNumber;
    }

    public Integer getReceiptSequenceNumber() {
        return receiptSequenceNumber;
    }

    public void setReceiptSequenceNumber(Integer receiptSequenceNumber) {
        this.receiptSequenceNumber = receiptSequenceNumber;
    }
}
