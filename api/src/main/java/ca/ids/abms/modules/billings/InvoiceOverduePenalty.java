package ca.ids.abms.modules.billings;

import ca.ids.abms.modules.util.models.AuditedEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
public class InvoiceOverduePenalty extends AuditedEntity {

	/**
	 *
	 */
	private static final long serialVersionUID = 269563993419226313L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

	@NotNull
    @ManyToOne
    @JoinColumn(name = "penalized_invoice_id")
    private BillingLedger penalizedInvoice;

    @ManyToOne
    @JoinColumn(name = "penalty_added_to_invoice_id")
    private BillingLedger penaltyAddedToInvoice;

    @NotNull
    private LocalDateTime penaltyAppliedDate;

	@NotNull
	private LocalDateTime penaltyPeriodEndDate;

	@NotNull
	private Double defaultPenaltyAmount;

    @NotNull
    private Double punitivePenaltyAmount;

	@NotNull
	private Integer penaltyNumberOfMonths;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BillingLedger getPenalizedInvoice() {
        return penalizedInvoice;
    }

    public void setPenalizedInvoice(BillingLedger penalizedInvoice) {
        this.penalizedInvoice = penalizedInvoice;
    }

    public BillingLedger getPenaltyAddedToInvoice() {
        return penaltyAddedToInvoice;
    }

    public void setPenaltyAddedToInvoice(BillingLedger penaltyAddedToInvoice) {
        this.penaltyAddedToInvoice = penaltyAddedToInvoice;
    }

    public LocalDateTime getPenaltyAppliedDate() {
        return penaltyAppliedDate;
    }

    public void setPenaltyAppliedDate(LocalDateTime penaltyAppliedDate) {
        this.penaltyAppliedDate = penaltyAppliedDate;
    }

    public LocalDateTime getPenaltyPeriodEndDate() {
        return penaltyPeriodEndDate;
    }

    public void setPenaltyPeriodEndDate(LocalDateTime penaltyPeriodEndDate) {
        this.penaltyPeriodEndDate = penaltyPeriodEndDate;
    }

    public Double getDefaultPenaltyAmount() {
        return defaultPenaltyAmount;
    }

    public void setDefaultPenaltyAmount(Double defaultPenaltyAmount) {
        this.defaultPenaltyAmount = defaultPenaltyAmount;
    }

    public Double getPunitivePenaltyAmount() {
        return punitivePenaltyAmount;
    }

    public void setPunitivePenaltyAmount(Double punitivePenaltyAmount) {
        this.punitivePenaltyAmount = punitivePenaltyAmount;
    }

    public Integer getPenaltyNumberOfMonths() {
        return penaltyNumberOfMonths;
    }

    public void setPenaltyNumberOfMonths(Integer penaltyNumberOfMonths) {
        this.penaltyNumberOfMonths = penaltyNumberOfMonths;
    }

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InvoiceOverduePenalty other = (InvoiceOverduePenalty) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

    @Override
    public String toString() {
        return "InvoiceOverduePenalty{" +
            "id=" + id +
            ", penalizedInvoice=" + penalizedInvoice +
            ", penaltyAddedToInvoice=" + penaltyAddedToInvoice +
            ", penaltyAppliedDate=" + penaltyAppliedDate +
            ", penaltyPeriodEndDate=" + penaltyPeriodEndDate +
            ", defaultPenaltyAmount=" + defaultPenaltyAmount +
            ", punitivePenaltyAmount=" + punitivePenaltyAmount +
            ", penaltyNumberOfMonths=" + penaltyNumberOfMonths +
            '}';
    }
}
