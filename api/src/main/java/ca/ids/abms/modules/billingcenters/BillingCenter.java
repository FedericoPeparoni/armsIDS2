package ca.ids.abms.modules.billingcenters;

import ca.ids.abms.config.db.SearchableText;
import ca.ids.abms.config.db.validators.UniqueKey;
import ca.ids.abms.modules.aerodromes.Aerodrome;
import ca.ids.abms.modules.billings.BillingLedger;
import ca.ids.abms.modules.users.User;
import ca.ids.abms.modules.util.models.VersionedAuditedEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "billing_centers")
@UniqueKey(columnNames = "externalAccountingSystemIdentifier")
public class BillingCenter extends VersionedAuditedEntity {

    private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", length = 100)
    @NotNull
    @SearchableText
    private String name;

    @Column(name = "prefix_invoice_number", length = 50)
    @SearchableText
    private String prefixInvoiceNumber;

    @Column(name = "invoice_sequence_number")
    private Integer invoiceSequenceNumber;

    @Column(name = "prefix_receipt_number", length = 50)
    @SearchableText
    private String prefixReceiptNumber;

    @Column(name = "receipt_sequence_number")
    private Integer receiptSequenceNumber;

    @JsonIgnore
    @OneToMany(mappedBy = "billingCenter")
    private Set<Aerodrome> aerodromes = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "billingCenter")
    private Set<User> users = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "billingCenter")
    private Set<BillingLedger> billingLedgers = new HashSet<>();

    @Column(name = "is_hq")
    @NotNull
    private boolean hq = false;

    @Column(name = "external_accounting_system_identifier", length = 20, unique = true)
    @SearchableText
    @Size(max = 20)
    private String externalAccountingSystemIdentifier;

    @Column(name = "iata_invoice_sequence_number")
    private Integer iataInvoiceSequenceNumber;

    @Column(name = "receipt_cheque_sequence_number")
    private Integer receiptChequeSequenceNumber;

    @Column(name = "receipt_wire_sequence_number")
    private Integer receiptWireSequenceNumber;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        BillingCenter that = (BillingCenter) o;

        return getId() != null ? getId().equals(that.id) : that.id == null;

    }

    public Set<Aerodrome> getAerodromes() {
        return aerodromes;
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

    public Set<User> getUsers() {
        return users;
    }

    public Set<BillingLedger> getBillingLedgers() {
        return billingLedgers;
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }

    public void setAerodromes(Set<Aerodrome> aAerodromes) {
        aerodromes = aAerodromes;
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

    public void setUsers(Set<User> aUsers) {
        users = aUsers;
    }

    public void setBillingLedgers(Set<BillingLedger> billingLedgers) {
        this.billingLedgers = billingLedgers;
    }

    public boolean getHq() {
        return hq;
    }

    public void setHq(boolean hq) {
        this.hq = hq;
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

    public String getExternalAccountingSystemIdentifier() {
        return externalAccountingSystemIdentifier;
    }

    public void setExternalAccountingSystemIdentifier(String externalAccountingSystemIdentifier) {
        this.externalAccountingSystemIdentifier = externalAccountingSystemIdentifier;
    }


    public Integer getIataInvoiceSequenceNumber() {
        return iataInvoiceSequenceNumber;
    }

    public void setIataInvoiceSequenceNumber(Integer receiptSequenceNumber) {
        this.iataInvoiceSequenceNumber = receiptSequenceNumber;
    }


    public Integer getReceiptChequeSequenceNumber() {
        return receiptChequeSequenceNumber;
    }

    public void setReceiptChequeSequenceNumber(Integer receiptSequenceNumber) {
        this.receiptChequeSequenceNumber = receiptSequenceNumber;
    }


    public Integer getReceiptWireSequenceNumber() {
        return receiptWireSequenceNumber;
    }

    public void setReceiptWireSequenceNumber(Integer receiptSequenceNumber) {
        this.receiptWireSequenceNumber = receiptSequenceNumber;
    }

    @Override
    public String toString() {
        return "BillingCenter{" + "id=" + getId() + ", name=" + name
                + '}';
    }
}
