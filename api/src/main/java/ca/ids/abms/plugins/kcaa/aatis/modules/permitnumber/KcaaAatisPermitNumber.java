package ca.ids.abms.plugins.kcaa.aatis.modules.permitnumber;

import ca.ids.abms.modules.billings.BillingLedger;
import ca.ids.abms.modules.common.enumerators.ExternalDatabaseForCharge;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.util.models.AuditedEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "kcaa_aatis_permit_numbers")
public class KcaaAatisPermitNumber extends AuditedEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "invoice_permit_number", unique = true)
    @NotNull
    private String invoicePermitNumber;

    @Column(name = "external_database_for_charge")
    @NotNull
    private ExternalDatabaseForCharge externalDatabaseForCharge;

    @ManyToOne
    @JoinColumn(name = "flight_movement_id")
    private FlightMovement flightMovement;

    @ManyToOne
    @JoinColumn(name = "billing_ledger_id")
    private BillingLedger billingLedger;

    @Column(name = "adhoc_total_fee_payment_amount")
    private Double adhocTotalFeePaymentAmount;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getInvoicePermitNumber() {
        return invoicePermitNumber;
    }

    public void setInvoicePermitNumber(String invoicePermitNumber) {
        this.invoicePermitNumber = invoicePermitNumber;
    }

    public ExternalDatabaseForCharge getExternalDatabaseForCharge() {
        return externalDatabaseForCharge;
    }

    public void setExternalDatabaseForCharge(ExternalDatabaseForCharge externalDatabaseForCharge) {
        this.externalDatabaseForCharge = externalDatabaseForCharge;
    }

    public BillingLedger getBillingLedger() {
        return billingLedger;
    }

    public void setBillingLedger(BillingLedger billingLedger) {
        this.billingLedger = billingLedger;
    }

    public Double getAdhocTotalFeePaymentAmount() {
        return adhocTotalFeePaymentAmount;
    }

    public void setAdhocTotalFeePaymentAmount(Double adhocTotalFeePaymentAmount) {
        this.adhocTotalFeePaymentAmount = adhocTotalFeePaymentAmount;
    }

    public FlightMovement getFlightMovement() {
        return flightMovement;
    }

    public void setFlightMovement(FlightMovement flightMovement) {
        this.flightMovement = flightMovement;
    }
}

