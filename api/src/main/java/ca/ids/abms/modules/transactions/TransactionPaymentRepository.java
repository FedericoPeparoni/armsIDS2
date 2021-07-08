package ca.ids.abms.modules.transactions;

import java.util.List;

import ca.ids.abms.modules.billings.BillingLedger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransactionPaymentRepository extends JpaRepository<TransactionPayment, Integer> {

    Page<TransactionPayment> findByTransactionId(Integer transactionId, Pageable pageable);
    List<TransactionPayment> findByTransactionId(Integer transactionId);
    List<TransactionPayment> findByBillingLedgerId(Integer billingLedgerId);


    @Query("SELECT tp.transaction FROM TransactionPayment tp WHERE tp.billingLedger.id = :billingLedgerId")
    List<Transaction> getAllTransactionByBillingLedgerId(@Param("billingLedgerId") int billingLedgerId);

    @Query("SELECT tp.billingLedger FROM TransactionPayment tp WHERE tp.transaction.id = :transactionId")
    Page<BillingLedger> getAllBillingLedgerByTransactionId(@Param("transactionId") int transactionId, Pageable pageable);

    @Query("SELECT tp FROM TransactionPayment tp WHERE tp.transaction.id = :transactionId")
    Page<TransactionPayment> getAllTransactionPaymentsByTransactionId(@Param("transactionId") int transactionId, Pageable pageable);

    @Query("SELECT tp FROM TransactionPayment tp WHERE tp.transaction.id = :transactionId")
    List<TransactionPayment> getAllTransactionPaymentsByTransactionId(@Param("transactionId") int transactionId);

	@Query("SELECT tp FROM TransactionPayment tp WHERE tp.billingLedger.id = :invoiceId AND tp.isAccountCredit = TRUE ORDER BY tp.createdAt")
    List<TransactionPayment> getAllAccountCreditPaymentsForInvoice(@Param("invoiceId") Integer invoiceId);

	@Query("SELECT tp FROM TransactionPayment tp WHERE tp.transaction.id = :transactionId ORDER BY tp.createdAt")
    List<TransactionPayment> getAllPaymentsByTransactionId(@Param("transactionId") Integer transactionId);
}
