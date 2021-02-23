package ca.ids.abms.modules.billingcenters;

import ca.ids.abms.modules.util.models.VersionedViewModel;

import java.util.Collection;

import javax.persistence.Id;

/**
 * Created by s.menotti on 09/01/2017.
 */
public class BillingCenterViewModel extends VersionedViewModel {

    @Id
    private Integer id;

    private String name;

    private String prefixInvoiceNumber;

    private Integer invoiceSequenceNumber;

    private String prefixReceiptNumber;

    private Integer receiptSequenceNumber;

    private Collection<String> aerodromes;

    private Collection<String> users;

    private boolean isHq;

    private String externalAccountingSystemIdentifier;
    
    private Integer iataInvoiceSequenceNumber;
    
    private Integer receiptChequeSequenceNumber;
    
    private Integer receiptWireSequenceNumber;
    
    public Collection<String> getAerodromes() {
        return aerodromes;
    }

    public boolean getHq() {
        return this.isHq;
    }

    public Integer getId() {
        return id;
    }

    public Integer getInvoiceSequenceNumber() {
        return invoiceSequenceNumber;
    }

    public String getName() {
        return name;
    }

    public String getPrefixInvoiceNumber() {
        return prefixInvoiceNumber;
    }

    public String getPrefixReceiptNumber() {
        return prefixReceiptNumber;
    }

    public Integer getReceiptSequenceNumber() {
        return receiptSequenceNumber;
    }

    public Integer getIataInvoiceSequenceNumber() {
        return iataInvoiceSequenceNumber;
    }

    public Integer getReceiptChequeSequenceNumber() {
        return receiptChequeSequenceNumber;
    }

    public Integer getReceiptWireSequenceNumber() {
        return receiptWireSequenceNumber;
    }

    public Collection<String> getUsers() {
        return users;
    }

    public void setAerodromes(Collection<String> aAerodromes) {
        aerodromes = aAerodromes;
    }

    public void setHq(boolean hq) {
        this.isHq = hq;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setInvoiceSequenceNumber(Integer invoiceSequenceNumber) {
        this.invoiceSequenceNumber = invoiceSequenceNumber;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrefixInvoiceNumber(String prefixInvoiceNumber) {
        this.prefixInvoiceNumber = prefixInvoiceNumber;
    }

    public void setPrefixReceiptNumber(String aPrefixReceiptNumber) {
        prefixReceiptNumber = aPrefixReceiptNumber;
    }

    public void setReceiptSequenceNumber(Integer aReceiptSequenceNumber) {
        receiptSequenceNumber = aReceiptSequenceNumber;
    }

    public void setUsers(Collection<String> aUsers) {
        users = aUsers;
    }

    public String getExternalAccountingSystemIdentifier() {
        return externalAccountingSystemIdentifier;
    }

    public void setExternalAccountingSystemIdentifier(String externalAccountingSystemIdentifier) {
        this.externalAccountingSystemIdentifier = externalAccountingSystemIdentifier;
    }


    public void setIataInvoiceSequenceNumber(Integer aIATAInvoiceSequenceNumber) {
    	iataInvoiceSequenceNumber = aIATAInvoiceSequenceNumber;
    }

    public void setReceiptChequeSequenceNumber(Integer aReceiptChequeSequenceNumber) {
    	receiptChequeSequenceNumber = aReceiptChequeSequenceNumber;
    }

    public void setReceiptWireSequenceNumber(Integer aReceiptWireSequenceNumber) {
    	receiptWireSequenceNumber = aReceiptWireSequenceNumber;
    }
    
    @Override
    public String toString() {
        return "BillingCenterViewModel [id=" + id + ", name=" + name + ", prefixInvoiceNumber=" + prefixInvoiceNumber
                + ", invoiceSequenceNumber=" + invoiceSequenceNumber + ", prefixReceiptNumber=" + prefixReceiptNumber
                + ", receiptSequenceNumber=" + receiptSequenceNumber + ", isHq=" + isHq
                + ", iataInvoiceSequenceNumer=" + iataInvoiceSequenceNumber
                + ", receiptChequeSequnceNumber=" + receiptChequeSequenceNumber
                + ", receiptWireChequeSequenceNumber=" + receiptWireSequenceNumber +
                "]";
    }
}
