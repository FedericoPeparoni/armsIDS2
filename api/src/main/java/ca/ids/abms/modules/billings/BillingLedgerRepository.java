package ca.ids.abms.modules.billings;

import ca.ids.abms.config.db.ABMSRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BillingLedgerRepository extends ABMSRepository<BillingLedger, Integer> {

    Page<BillingLedger> findAll(Pageable pageable);

    // every time transaction payment is made, amountOwing is deducted
    //if invoice is fully paid, amountOwing should equal 0
    Page<BillingLedger> findByPaymentDueDateBeforeAndAmountOwingGreaterThan(final LocalDateTime localDateTime, final Double amountOwning, final Pageable pageable);

    @Query("select bl from BillingLedger bl where bl.invoiceStateType in :invoiceStateTypeSet")
    Page<BillingLedger> findByInvoiceStateTypeInList(@Param("invoiceStateTypeSet") String[] invoiceStateTypeSet, Pageable pageable);

    Page<BillingLedger> findByAccountIdAndInvoiceCurrencyIdAndInvoiceStateType(Integer accountId, Integer currencyId, String invoiceStateTypeName, Pageable pageable);

    @Query(value="select bl.id from billing_ledgers bl where bl.account_id=:accountId and "
        + "bl.invoice_currency=:invoiceCurrencyId and bl.invoice_state_type ='PUBLISHED'", nativeQuery = true)
    List<Integer> findUnpaidInvoiceIds(@Param("accountId") Integer accountId, @Param("invoiceCurrencyId") Integer invoiceCurrencyId);

    @Query(value="select bl.* from billing_ledgers bl where " +
        "bl.payment_due_date\\:\\:date < NOW()\\:\\:date and  " +
        "bl.amount_owing > 0 and " +
        "bl.account_id=:accountId and " +
        "bl.invoice_state_type='PUBLISHED' ", nativeQuery = true)
    List<BillingLedger> findOverdueInvoicesByAccountId(@Param("accountId") Integer accountId);

    @Query(value="select bl.* from billing_ledgers bl where " +
        "bl.payment_due_date\\:\\:date < NOW()\\:\\:date and  " +
        "bl.amount_owing > 0 and " +
        "bl.invoice_currency=:invoiceCurrencyId and " +
        "bl.account_id=:accountId and " +
        "bl.invoice_state_type='PUBLISHED' ", nativeQuery = true)
    List<BillingLedger> findOverdueInvoicesByAccountIdAndCurrencyId(@Param("accountId") Integer accountId, @Param("invoiceCurrencyId") Integer invoiceCurrencyId);

    @Query("SELECT count(bl.id) FROM BillingLedger bl WHERE bl.account.id = :accountId")
    Integer howManyInvoicesByAccountId (@Param("accountId") Integer accountId);

    @Query("SELECT SUM(bl.amountOwing) " +
        "FROM BillingLedger bl " +
        "WHERE bl.account.id = :accountId " +
        "AND bl.invoiceCurrency.id = :invoiceCurrencyId " +
        "AND bl.invoiceStateType = 'PUBLISHED'")
    Double getTotalAmountForInvoicesByAccountIdAndCurrency (@Param("accountId") Integer accountId, @Param("invoiceCurrencyId") Integer invoiceCurrencyId);

    Page<BillingLedger> findByAccountIdAndInvoiceCurrencyIdAndInvoiceStateTypeInAndInvoiceTypeNot(
        Integer accountId, Integer currencyId, String[] invoiceStateType, String invoiceType, Pageable pageable);

    BillingLedger findByInvoiceNumber(String invoiceNumber);

    @Query(value="select bl.* from billing_ledgers bl where " +
        "bl.invoice_date_of_issue\\:\\:date >= :invoiceDate\\:\\:date"
        , nativeQuery = true)
    List<BillingLedger> findByAfterInvoiceDate(@Param("invoiceDate") LocalDate invoiceDate);

    @Query (value = "SELECT COUNT(bl) FROM BillingLedger bl JOIN bl.account ac JOIN ac.accountUsers au")
    long countAllForSelfCareAccounts();

    @Query (value = "SELECT COUNT(bl) FROM BillingLedger bl JOIN bl.account ac JOIN ac.accountUsers au WHERE au.id = :userId")
    long countAllForSelfCareUser(@Param ("userId") final int userId);
}
