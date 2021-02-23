package ca.ids.abms.modules.reports2.invoices;

import javax.xml.bind.annotation.XmlElement;

public final class AdditionalCharge {
    public @XmlElement(nillable = true) String dateStr;
    public @XmlElement(nillable = true) String description;
    public @XmlElement(nillable = true) Double amount;
    public @XmlElement(nillable = true) String amountStr;
    public @XmlElement(nillable = true) String amountStrWithCurrencySymbol;
    public @XmlElement(nillable = true) Double amountAnsp;
    public @XmlElement(nillable = true) String amountAnspStr;
}
