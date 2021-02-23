package ca.ids.abms.modules.reports2.invoices.overdueinvoice;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement (name = "overdueInvoice")
public class OverdueInvoiceData {

    /** Global/singleton properties (totals, etc) */
    public static final class Global {
        public @XmlElement(nillable = true) String realInvoiceNumber;
        public @XmlElement(nillable = true) String invoiceNumber;
        public @XmlElement(nillable = true) String invoiceName;
        public @XmlElement(nillable = true) String invoiceIssueLocation;
        public @XmlElement(nillable = true) String invoiceDateStr;
        public @XmlElement(nillable = true) Integer accountId;
        public @XmlElement(nillable = true) String accountName;

        // Billing info
        public @XmlElement(nillable = true) String billingName;
        public @XmlElement(nillable = true) String billingAddress;
        public @XmlElement(nillable = true) String billingContactTel;

        // Total: sum of all line items, excluding credit and penalties
        public @XmlElement(nillable = true) Double totalAmount;
        public @XmlElement(nillable = true) String totalAmountStr;
        public @XmlElement(nillable = true) String totalAmountStrWithCurrencySymbol;
        public @XmlElement(nillable = true) Double totalAmountAnsp;
        public @XmlElement(nillable = true) String totalAmountAnspStr;

        // Amount due: total - credit
        public @XmlElement(nillable = true) Double amountDue;
        public @XmlElement(nillable = true) String amountDueStr;
        public @XmlElement(nillable = true) String amountDueStrWithCurrencySymbol;
        public @XmlElement(nillable = true) String invoiceCurrencyCode;

    }
    public Global global;

}
