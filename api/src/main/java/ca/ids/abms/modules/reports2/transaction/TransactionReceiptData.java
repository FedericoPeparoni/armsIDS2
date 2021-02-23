package ca.ids.abms.modules.reports2.transaction;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * Raw data that represents a transaction receipt.
 * This should be further formatted into a PDF, CSV, etc.
 * <p>
 * The correspoinding BIRT report template expects this class to be formatted
 * as XML using the Java 8 JAXB library; see the example file in the BIRT report directory.
 *
 */
@XmlRootElement (name = "transactionReceipt")
public class TransactionReceiptData {

    /** Global properties */
    public static final class Global {

        public @XmlElement(nillable = true) String receiptName;
        public @XmlElement(nillable = true) Boolean isDebit;

        public @XmlElement(nillable = true) String transactionIssueLocation;
        public @XmlElement(nillable = true) String transactionNumber;
        public @XmlElement(nillable = true) String transactionRealNumber;
        public @XmlElement(nillable = true) String transactionPayerName;
        public @XmlElement(nillable = true) String transactionDescription;

        public @XmlElement(nillable = true) Double paymentAmount;
        public @XmlElement(nillable = true) String paymentAmountStr;
        public @XmlElement(nillable = true) String paymentAmountStrWithCurrencySymbol;
        public @XmlElement(nillable = true) String paymentAmountStrInWordsLine1;
        public @XmlElement(nillable = true) String paymentAmountStrInWordsLine2;
        public @XmlElement(nillable = true) String paymentAmountStrInWordsLine3;

        public @XmlElement(nillable = true) String paymentDateStr;

        public @XmlElement(nillable = true) Double localAmount;
        public @XmlElement(nillable = true) String localAmountStr;
        public @XmlElement(nillable = true) String localAmountStrWithCurrencySymbol;

        public @XmlElement(nillable = true) Double transactionPaymentExchangeRate;
        public @XmlElement(nillable = true) String transactionPaymentExchangeRateStr;

        public @XmlElement(nillable = true) String transactionReferenceNumber;
        public @XmlElement(nillable = true) String transactionDateStr;

        public @XmlElement(nillable = true) String bankName;
        public @XmlElement(nillable = true) String bankBranch;
        public @XmlElement(nillable = true) String bankAccount;

        public @XmlElement(nillable = true) String transactionOfficerName;
        public @XmlElement(nillable = true) String transactionOfficerPosition;

        public @XmlElement(nillable = true) String paymentCurrencyCode;
        public @XmlElement(nillable = true) String localCurrencyCode;

        public @XmlElement(nillable = true) String kraClerkName;
        public @XmlElement(nillable = true) String kraReceiptNumber;
    }
    public Global global;

    /** Information about each payment */
    public static final class PaymentInfo {
        public @XmlElement(nillable = true) Integer billingLedgerId;
        public @XmlElement(nillable = true) String invoiceNumber;
        public @XmlElement(nillable = true) String invoiceDateStr;

        public @XmlElement(nillable = true) Double paymentAmount;
        public @XmlElement(nillable = true) String paymentAmountStr;
        public @XmlElement(nillable = true) String paymentAmountStrWithCurrencySymbol;

        public @XmlElement(nillable = true) Double localAmount;
        public @XmlElement(nillable = true) String localAmountStr;
        public @XmlElement(nillable = true) String localAmountStrWithCurrencySymbol;

        public @XmlElement(nillable = true) Double paymentExchangeRate;
        public @XmlElement(nillable = true) String paymentExchangeRateStr;
    }

    @XmlElementWrapper
    @XmlElement (name="paymentInfo", nillable = true)
    public List <PaymentInfo> paymentInfoList;

}
