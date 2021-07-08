package ca.ids.abms.modules.transactions;

import ca.ids.abms.config.db.ABMSRepository;
import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.currencies.Currency;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends ABMSRepository<Transaction, Integer> {

    @Modifying
    @Query(nativeQuery = true, value = "LOCK TABLE transactions IN EXCLUSIVE MODE")
    void lockExclusive();

    Page<Transaction> findAll(Pageable pageable);

    Transaction findTopByAccountIdAndCurrencyIdOrderByTransactionDateTimeDesc(Integer accountId, Integer currency);

    Transaction findOneByDescription(String description);

    /**
     * Find all credit transactions that has unused account credit:
     * 1. all credit transactions without transaction payments. In this case: account credit = transaction amount
     * 2. all credit transactions that have transaction payments. In this case: account credit = transaction amount - total transaction payments amount for this transaction
     * 3. all credit transactions that have transaction payments with the same amount for PROFORMA invoice
     *
     * @return list of transactions
     */
    @Query("SELECT DISTINCT tr FROM Transaction tr JOIN tr.transactionPayments tp " +
        "WHERE ((tr.id NOT IN (SELECT tp.transaction.id FROM TransactionPayment tp)) OR " +
            "(ABS(tr.amount) > (SELECT ABS(SUM(tp.amount)) FROM tr.transactionPayments tp)) OR " +
            "(ABS(tr.amount) <= (SELECT ABS(SUM(tp.amount)) FROM tr.transactionPayments tp) AND tp.billingLedger.proforma = TRUE)) " +
        "AND tr.transactionType.name = 'credit' AND " +
        "tr.currency.id = :currencyId AND " +
        "tr.account.id = :accountId " +
        "ORDER BY tr.createdAt")
    List<Transaction> findCreditTransactionsWithUnusedAccountCredit(@Param("accountId") Integer accountId,@Param("currencyId") Integer currencyId);

    @Query(nativeQuery = true, value =
        "SELECT tr.balance FROM transactions tr WHERE " +
            "        tr.currency_id=:currencyId AND tr.account_id=:accountId " +
            "        order by tr.created_at desc LIMIT 1"
    )
    Double getBalance (@Param("accountId") Integer accountId, @Param("currencyId") Integer currencyId);

    /**
     * Calculate total amount of all transaction payments for the transaction.
     * If no payments found, the method returns 0.
     *
     * @return Double
     */
    @Query(nativeQuery = true, value =
    "SELECT COALESCE(SUM(tp.amount),0) FROM transaction_payments tp where tp.transaction_id=:transactionId")
    Double getTransactionPaymentsTotalAmount(@Param("transactionId") Integer transactionId);

    Transaction findOneByTransactionTypeAndPaymentMechanismAndPaymentReferenceNumberAndAccount(
        TransactionType transactionType,
        TransactionPaymentMechanism paymentMechanism,
        String paymentReferenceNumber,
        Account account);

    @Query("SELECT t FROM Transaction t JOIN t.transactionPayments tp WHERE tp.billingLedger.id = ?1 ORDER BY COALESCE(t.paymentDate, t.transactionDateTime)")
    List<Transaction> getTransactionsByInvoiceId(Integer invoiceId);

    @Query("SELECT DISTINCT t.currency FROM Transaction t WHERE t.account = :account")
    List<Currency> findAllActiveAccountCurrencies(@Param("account") Account account);

    @Query (value = "SELECT COUNT(tr) FROM Transaction tr JOIN tr.account ac JOIN ac.accountUsers au WHERE au.id = :userId")
    long countAllForSelfCareUser(@Param ("userId") final int userId);

    @Query (value = "SELECT COUNT(tr) FROM Transaction tr JOIN tr.account ac JOIN ac.accountUsers au")
    long countAllForSelfCareAccounts();

}
