package ca.ids.abms.modules.billings;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceOverduePenaltyRepository extends JpaRepository<InvoiceOverduePenalty, Integer> {

	/**
	 * Find InvoiceOverduePenalty with most recent penalty_period_end_date by penalized invoice id.
	 * Penalized invoice records are sorted by penalty_period_end_date DESC.
	 *
	 * @param penalizedInvoiceId
	 * @return InvoiceOverduePenalty
	 */
	@Query(value="select iop.* from " +
	"invoice_overdue_penalties iop " +
	"where penalized_invoice_id=:penalizedInvoiceId " +
	"order by iop.penalty_period_end_date desc " +
	"limit 1",
	nativeQuery = true)
    InvoiceOverduePenalty findMostRecentInvoiceOverduePenaltyByInvoiceId(@Param("penalizedInvoiceId") Integer penalizedInvoiceId);

	@Query(value="select sum(abs(iop.default_penalty_amount + iop.punitive_penalty_amount)) from " +
	"invoice_overdue_penalties iop " +
	"where penalty_added_to_invoice_id=:penaltyAddedToInvoiceId",
	nativeQuery = true)
	Double getTotalPenaltyAmountAppliedToInvoice(@Param("penaltyAddedToInvoiceId") Integer penaltyAddedToInvoiceId);

	@Query("SELECT iop FROM InvoiceOverduePenalty iop WHERE iop.penaltyAddedToInvoice.id = :penaltyAddedToInvoiceId ORDER BY iop.createdAt")
    List<InvoiceOverduePenalty> getAllPenaltiesAppliedToInvoice(@Param("penaltyAddedToInvoiceId") Integer penaltyAddedToInvoiceId);

	@Query(value="select iop.* from invoice_overdue_penalties iop, billing_ledgers bl " +
        "where iop.penalized_invoice_id = bl.id and iop.penalty_added_to_invoice_id is null " +
        "and bl.invoice_state_type = 'PUBLISHED' " +
        "and bl.account_id=:accountId and bl.invoice_currency=:currencyId ", nativeQuery = true)
	List<InvoiceOverduePenalty> getAllPenaltiesToApply(@Param("accountId") Integer accountId, @Param("currencyId") Integer currencyId);

    @Modifying
	@Query("UPDATE InvoiceOverduePenalty p SET p.penaltyAddedToInvoice = null WHERE p.penaltyAddedToInvoice.id = :penaltyAddedToInvoiceId ")
	int removePenaltiesAttachedDaily (@Param("penaltyAddedToInvoiceId") Integer penaltyAddedToInvoiceId);

    @Modifying
    @Query(value="DELETE FROM InvoiceOverduePenalty p WHERE p.penaltyAddedToInvoice.id = :penaltyAddedToInvoiceId")
    int removePenaltiesAppliedAtInvoiceCreationTime (@Param("penaltyAddedToInvoiceId") Integer penaltyAddedToInvoiceId);
}
