package ca.ids.abms.modules.reports2.invoices.nonaviation;

import ca.ids.abms.modules.reports2.invoices.AccountCredit;
import ca.ids.abms.modules.reports2.invoices.AdditionalCharge;
import ca.ids.abms.modules.reports2.invoices.OverduePenaltyInvoice;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import ca.ids.abms.modules.reports2.invoices.BankAccount;

import java.util.List;

@XmlRootElement (name = "nonAviationInvoice")
public class NonAviationInvoiceData {

    /** Global/singleton properties (totals, etc) */
    public static final class Global {
        public @XmlElement(nillable = true) String realInvoiceNumber;
        public @XmlElement(nillable = true) String invoiceNumber;
        public @XmlElement(nillable = true) String invoiceName;
        public @XmlElement(nillable = true) String invoiceIssueLocation;
        public @XmlElement(nillable = true) String invoiceDateStr;
        public @XmlElement(nillable = true) String invoiceDueDateStr;
        public @XmlElement(nillable = true) String invoiceBillingPeriod;
        public @XmlElement(nillable = true) Integer accountId;
        public @XmlElement(nillable = true) String accountName;
        public @XmlElement(nillable = true) String fromName;
        public @XmlElement(nillable = true) String fromPosition;
        public @XmlElement(nillable = true) Boolean isCashAccount;
        public @XmlElement(nillable = true) Boolean fromPointOfSale;
        public @XmlElement(nillable = true) Boolean proforma;

        // Billing info
        public @XmlElement(nillable = true) String billingName;
        public @XmlElement(nillable = true) String billingAddress;
        public @XmlElement(nillable = true) String billingContactTel;
        public @XmlElement(nillable = true) String billingEmail;

        // Total: sum of all line items, excluding credit and penalties
        public @XmlElement(nillable = true) Double totalAmount;
        public @XmlElement(nillable = true) String totalAmountStr;
        public @XmlElement(nillable = true) String totalAmountStrWithCurrencySymbol;
        public @XmlElement(nillable = true) Double totalAmountAnsp;
        public @XmlElement(nillable = true) String totalAmountAnspStr;
        public @XmlElement(nillable = true) Double totalAmountUsd;
        public @XmlElement(nillable = true) String totalAmountUsdStr;
        public @XmlElement(nillable = true) String invoiceCurrencyInWords;
        public @XmlElement(nillable = true) String exchageRate;
        public @XmlElement(nillable = true) String exchageRateUsd;
        public @XmlElement(nillable = true) String totalAccountCredit;

        // Amount due: total - credit
        public @XmlElement(nillable = true) Double amountDue;
        public @XmlElement(nillable = true) String amountDueStr;
        public @XmlElement(nillable = true) String amountDueStrWithCurrencySymbol;
        public @XmlElement(nillable = true) String invoiceCurrencyCode;
        public @XmlElement(nillable = true) Double amountDueAnsp;
        public @XmlElement(nillable = true) String amountDueAnspStr;
        public @XmlElement(nillable = true) String invoiceCurrencyAnspCode;
        public @XmlElement(nillable = true) Double amountDueUsd;
        public @XmlElement(nillable = true) String amountDueUsdStr;
        public @XmlElement(nillable = true) String invoiceCurrencyUsdCode;

        // Total Amount In Words: total amount value spelled out in words
        public @XmlElement(nillable = true) String totalAmountStrInWords;
        public @XmlElement(nillable = true) String totalAmountStrInWordsSpanish;
        public @XmlElement(nillable = true) String totalAmountStrInWordsWithCurrencySymbol;
        public @XmlElement(nillable = true) String totalAmountStrInWordsWithCurrencySymbolSpanish;

        // Amount Due In Words: amount due value spelled out in words
        public @XmlElement(nillable = true) String amountDueStrInWords;
        public @XmlElement(nillable = true) String amountDueStrInWordsSpanish;
        public @XmlElement(nillable = true) String amountDueStrInWordsWithCurrencySymbol;
        public @XmlElement(nillable = true) String amountDueStrInWordsWithCurrencySymbolSpanish;

        /**
         * No longer use this property as it is misleading. This amount is actually the "amount due" and not the
         * "total amount". Leaving as is until it is safely removed from all existing invoices.
         *
         * @deprecated Use {@link #amountDueStrInWords} for amount due and {@link #totalAmountStrInWords}
         * for total amount.
         */
        @Deprecated
        public @XmlElement(nillable = true) String totalAmountInWords;

        /**
         * No longer use this property as it is misleading. This amount is actually the "amount due" and not the
         * "total amount". Leaving as is until it is safely removed from all existing invoices.
         *
         * @deprecated Use {@link #amountDueStrInWordsWithCurrencySymbol} for amount due and
         * {@link #totalAmountStrInWordsWithCurrencySymbol} for total amount.
         */
        @Deprecated
        public @XmlElement(nillable = true) String totalAmountInWordsWithCurrencySymbol;

        public @XmlElement(nillable = true) String totalOutstandingAmount;
        public @XmlElement(nillable = true) String totalPenalty;

        public @XmlElement(nillable = true) String kraClerkName;
        public @XmlElement(nillable = true) String kraReceiptNumber;

        public @XmlElement(nillable = true) String displayInverse;

        public @XmlElement(nillable = true) Double amountDueAlt;
        public @XmlElement(nillable = true) String amountDueAltStr;
        public @XmlElement(nillable = true) String amountDueAltStrWithCurrencySymbol;
        public @XmlElement(nillable = true) String invoiceCurrencyAltCode;

    }

    public Global global;

    /** Line items */
    public static final class LineItem {
        public @XmlElement(nillable = true) Integer id;
        public @XmlElement(nillable = true) String aerodromeName;
        public @XmlElement(nillable = true) String dateStr;
        public @XmlElement(nillable = true) String descr;
        public @XmlElement(nillable = true) Double amount;
        public @XmlElement(nillable = true) String amountStr;
        public @XmlElement(nillable = true) String amountStrWithCurrencySymbol;
        public @XmlElement(nillable = true) Double amountAnsp;
        public @XmlElement(nillable = true) String amountAnspStr;
        public @XmlElement(nillable = true) String userDescription;
    }

    @XmlElementWrapper
    @XmlElement (name="lineItem", nillable = true)
    public List <LineItem> lineItemList;

    /** Additional charges */
    @XmlElementWrapper
    @XmlElement (name="additionalCharge", nillable = true)
    public List <AdditionalCharge> additionalCharges;

    /** Account credits */
    @XmlElementWrapper
    @XmlElement (name="accountCredit", nillable = true)
    public List <AccountCredit> accountCredits;

    /** Overdue invoice */
    @XmlElementWrapper
    @XmlElement (name="overduePenaltyInvoice", nillable = true)
    public List <OverduePenaltyInvoice> overduePenaltyInvoices;

    /** Bank accounts */
    @XmlElementWrapper
    @XmlElement (name="bankAccount", nillable = true)
    public List <BankAccount> bankAccountList;
}
