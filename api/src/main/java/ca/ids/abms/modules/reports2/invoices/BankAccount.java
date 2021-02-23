package ca.ids.abms.modules.reports2.invoices;

import javax.xml.bind.annotation.XmlElement;

public final class BankAccount {
    public @XmlElement(nillable = true) String accountNumber;
    public @XmlElement(nillable = true) String branchCode;
    public @XmlElement(nillable = true) String currencyCode;
}
