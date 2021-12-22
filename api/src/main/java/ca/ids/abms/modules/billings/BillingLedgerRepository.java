package ca.ids.abms.modules.billings;

import ca.ids.abms.config.db.ABMSRepository;
import ca.ids.abms.modules.common.enumerators.InvoiceStateType;
import ca.ids.abms.modules.common.enumerators.InvoiceType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Repository
public interface BillingLedgerRepository extends ABMSRepository<BillingLedger, Integer> {

    Page<BillingLedger> findAll(Pageable pageable);

    @Query(value="select bl.* from billing_ledgers bl where bl.account_id =:accountId and "
    		+"bl.invoice_type =:invoiceType and bl.invoice_state_type ='PUBLISHED' and bl.invoice_period_or_date < :invoicePeriodOrDate Order By invoice_period_or_date desc",nativeQuery = true)
    List<BillingLedger> findByAccountIdAndInvoiceTypeAndInvoicePeriodOrDate(@Param("accountId") final Integer accountId,  @Param("invoiceType") final String invoiceType, @Param("invoicePeriodOrDate") final LocalDateTime invoicePeriodOrDate);

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

    @Modifying
    @Query("UPDATE BillingLedger SET invoiceAmount = :invoiceAmount where id = :id")
    void updateBillingLedgerByIdAndInvoiceAmount(@Param("id") Integer id,
                                             @Param("invoiceAmount") Double invoiceAmount);

    @Query("SELECT bl FROM Transaction t, BillingLedger bl WHERE t.id = :transactionId AND t.paymentReferenceNumber=bl.invoiceNumber AND t.paymentMechanism='adjustment'")
	BillingLedger getDebitNoteBillingLedgerByTransactionId(@Param("transactionId") Integer transactionId);

    List<BillingLedger> findByAccountIdIn(List<Integer> accountid);

    @Query(nativeQuery = true, value = "SELECT * FROM billing_ledgers as bl WHERE bl.invoice_type LIKE :invoiceType AND bl.invoice_date_of_issue >= :fromDate AND bl.invoice_date_of_issue <= :toDate")
    List<BillingLedger> findIssuedInvoicesAccountsIdsByTypeAndDate(@Param("invoiceType")String invoiceType, @Param("fromDate")Date fromDate, @Param("toDate")Date toDate );

    @Query("SELECT bl.account.id FROM BillingLedger bl WHERE bl.invoiceType LIKE :invoiceType AND bl.invoiceDateOfIssue >= :fromDate AND bl.invoiceDateOfIssue <= :toDate")
    List<Integer> findAccountsIdByTypeAndDate(@Param("invoiceType")String invoiceType, @Param("fromDate")LocalDateTime fromDate, @Param("toDate")LocalDateTime toDate );

}
