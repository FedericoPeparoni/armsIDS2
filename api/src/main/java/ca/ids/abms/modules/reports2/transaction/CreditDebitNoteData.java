package ca.ids.abms.modules.reports2.transaction;

import ca.ids.abms.modules.reports2.invoices.AdditionalCharge;
import ca.ids.abms.modules.reports2.invoices.OverduePenaltyInvoice;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement (name = "creditDebitNote")
public class CreditDebitNoteData {
    /** Global/singleton properties (totals, etc) */

    public static final class Global {
        public @XmlElement(nillable = true) String realTransactionNumber;
        public @XmlElement(nillable = true) String transactionNumber;
        public @XmlElement(nillable = true) String transactionName;
        public @XmlElement(nillable = true) String transactionIssueLocation;
        public @XmlElement(nillable = true) String transactionDateStr;
        public @XmlElement(nillable = true) Integer accountId;
        public @XmlElement(nillable = true) String accountName;
        public @XmlElement(nillable = true) String accountAlias;
        public @XmlElement(nillable = true) String fromName;
        public @XmlElement(nillable = true) String fromPosition;
        public @XmlElement(nillable = true) Boolean isDebit;
        public @XmlElement(nillable = true) Boolean isAviation;

        public @XmlElement(nillable = true) String invoiceRefNumber;
        public @XmlElement(nillable = true) String invoiceRefDateStr;

        // Billing info
        public @XmlElement(nillable = true) String billingName;
        public @XmlElement(nillable = true) String billingAddress;
        public @XmlElement(nillable = true) String billingContactTel;

        // Total: sum of all line items
        public @XmlElement(nillable = true) Double totalAmount;
        public @XmlElement(nillable = true) String totalAmountStr;
        public @XmlElement(nillable = true) String totalAmountStrWithCurrencySymbol;
        public @XmlElement(nillable = true) Double totalAmountAnsp;
        public @XmlElement(nillable = true) String totalAmountAnspStr;
        public @XmlElement(nillable = true) String totalAmountInUSD;
        public @XmlElement(nillable = true) String amountDueInUSD;
        public @XmlElement(nillable = true) String amountDueInANSP;
        public @XmlElement(nillable = true) Double totalAmountAlt;
        public @XmlElement(nillable = true) String totalAmountAltStr;
        public @XmlElement(nillable = true) String totalAmountAltStrWithCurrencySymbol;

        // Amount due: total - credit
        public @XmlElement(nillable = true) Double creditAmount;
        public @XmlElement(nillable = true) String creditAmountStr;
        public @XmlElement(nillable = true) String creditAmountStrWithCurrencySymbol;

        public @XmlElement(nillable = true) Double amountDue;
        public @XmlElement(nillable = true) String amountDueStr;
        public @XmlElement(nillable = true) String amountDueStrWithCurrencySymbol;

        public @XmlElement(nillable = true) String invoiceCurrencyCode;
        public @XmlElement(nillable = true) String invoiceCurrencyAltCode;
    }
    public Global global;

    /** Line items */
    public static final class LineItem {
        //public static @XmlElement(nillable = true) String Discriminator;
        public static /*@XmlElement(nillable = true)*/ String headerFlightIDSpanish="ID de Vuelo";
        public static /*@XmlElement(nillable = true)*/ String headerFlightID="Flight ID";
        public static /*@XmlElement(nillable = true)*/ String headerRegistrationNumberSpanish="Registration Number";
        public static /*@XmlElement(nillable = true)*/ String headerRegistrationNumber="RegistrationNumber";
        public @XmlElement(nillable = true) String header;
        public @XmlElement(nillable = true) String headerSpanish;


        public @XmlElement(nillable = true) Integer id;
        public @XmlElement(nillable = true) String dateStr;
        public @XmlElement(nillable = true) String flightId;
        public @XmlElement(nillable = true) String registrationNumber;
        public @XmlElement(nillable = true) String aerodrome;
        public @XmlElement(nillable = true) String chargeDescription;
        public @XmlElement(nillable = true) String chargeDescriptionSpanish;

        public @XmlElement(nillable = true) Double amount;
        public @XmlElement(nillable = true) String amountStr;
        public @XmlElement(nillable = true) String amountStrWithCurrencySymbol;
        public @XmlElement(nillable = true) Double amountAnsp;
        public @XmlElement(nillable = true) String amountAnspStr;
    };

    @XmlElementWrapper
    @XmlElement (name="lineItem", nillable = true)
    public List <LineItem> lineItemList;

    /** Affected invoices */
    public static final class AffectedInvoice {
        public @XmlElement(nillable = true) Integer id;
        public @XmlElement(nillable = true) String affectedInvoiceNumber;
        public @XmlElement(nillable = true) String affectedInvoiceAmount;
    }

    @XmlElementWrapper
    @XmlElement (name="affectedInvoice", nillable = true)
    public List <AffectedInvoice> affectedInvoiceList;

    /** Additional charges */
    @XmlElementWrapper
    @XmlElement (name="additionalCharge", nillable = true)
    public List <AdditionalCharge> additionalCharges;

    /** Overdue invoice */
    @XmlElementWrapper
    @XmlElement (name="overduePenaltyInvoice", nillable = true)
    public List <OverduePenaltyInvoice> overduePenaltyInvoices;


    public enum  Discriminator {
        FLIGHT_ID,
        REGISTRATION_NUMBER,
        OTHER

    }
}
