package ca.ids.abms.modules.reports2.invoices;

import javax.xml.bind.annotation.XmlElement;

/**
 * Penalized invoice model for report
 *
 */
public final class OverduePenaltyInvoice {
	public @XmlElement(nillable = true) String invoiceNumber;
	public @XmlElement(nillable = true) String invoiceDateOfIssueStr;
    public @XmlElement(nillable = true) String paymentDueDateStr;
    public @XmlElement(nillable = true) Double amountOwing;
    public @XmlElement(nillable = true) String amountOwingStr;
    public @XmlElement(nillable = true) String amountOwingStrWithCurrencySymbol;
    public @XmlElement(nillable = true) Double amountOwingAnsp;
    public @XmlElement(nillable = true) String amountOwingAnspStr;
}
