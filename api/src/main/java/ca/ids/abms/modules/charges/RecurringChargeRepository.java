package ca.ids.abms.modules.charges;

import ca.ids.abms.config.db.ABMSRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface RecurringChargeRepository extends ABMSRepository<RecurringCharge, Integer> {

    @Query(nativeQuery = true, value=
            "SELECT DISTINCT recurring_charges.* " +
              "FROM recurring_charges " +
              "JOIN invoice_line_items ON invoice_line_items.recurring_charge_id = recurring_charges.id " +
              "JOIN billing_ledgers    ON invoice_line_items.invoice_id = billing_ledgers.id " +
             "WHERE billing_ledgers.account_id = :accountId " +
               "AND billing_ledgers.invoice_period_or_date >= :invoicedFromInclusive " +
               "AND billing_ledgers.invoice_period_or_date < :invoicedToExclusive")
    Set<RecurringCharge> findChargesIncludedInAccountInvoiceForPeriod (
            @Param("accountId") Integer accountId,
            @Param("invoicedFromInclusive") LocalDateTime invoicedFromInclusive,
            @Param("invoicedToExclusive") LocalDateTime invoicedToExclusive);

    @Query (nativeQuery = true, value =
            "SELECT DISTINCT rc.* " +
              "FROM recurring_charges rc " +
                    "LEFT JOIN service_charge_catalogues scc ON scc.id = rc.service_charge_catalogue_id " +
             "WHERE scc.invoice_category in (:invoiceCategories) " +
               "AND rc.start_date < :invoicedToExclusive " +
               "AND rc.end_date >= :invoicedFromInclusive " +
               "AND rc.account_id = :accountId " +
               "AND rc.id NOT IN (" +
                      "SELECT distinct recurring_charges.id " +
                        "FROM recurring_charges " +
                        "JOIN invoice_line_items ON invoice_line_items.recurring_charge_id = recurring_charges.id " +
                        "JOIN billing_ledgers    ON invoice_line_items.invoice_id = billing_ledgers.id " +
                       "WHERE billing_ledgers.account_id = :accountId " +
                         "AND billing_ledgers.invoice_period_or_date >= :invoicedFromInclusive " +
                         "AND billing_ledgers.invoice_period_or_date < :invoicedToExclusive)"
    )
    List<RecurringCharge> findChargesNotIncludedInAccountInvoiceForPeriod (
            @Param("accountId") Integer accountId,
            @Param("invoicedFromInclusive") LocalDateTime invoicedFromInclusive,
            @Param("invoicedToExclusive") LocalDateTime invoicedToExclusive,
            @Param("invoiceCategories") List <String> invoiceCategories);

    @Query (nativeQuery = true, value =
        "SELECT DISTINCT rc.* " +
            "FROM recurring_charges rc " +
            "LEFT JOIN service_charge_catalogues scc ON scc.id = rc.service_charge_catalogue_id " +
            "WHERE scc.invoice_category in (:invoiceCategories) " +
            "AND scc.external_charge_category_id = :externalChargeCategoryId " +
            "AND rc.start_date < :invoicedToExclusive " +
            "AND rc.end_date >= :invoicedFromInclusive " +
            "AND rc.account_id = :accountId " +
            "AND rc.id NOT IN (" +
            "SELECT distinct recurring_charges.id " +
            "FROM recurring_charges " +
            "JOIN invoice_line_items ON invoice_line_items.recurring_charge_id = recurring_charges.id " +
            "JOIN billing_ledgers    ON invoice_line_items.invoice_id = billing_ledgers.id " +
            "WHERE billing_ledgers.account_id = :accountId " +
            "AND billing_ledgers.invoice_period_or_date >= :invoicedFromInclusive " +
            "AND billing_ledgers.invoice_period_or_date < :invoicedToExclusive)"
    )
    List<RecurringCharge> findChargesNotIncludedInAccountInvoiceForPeriodByExternalChargeCategory (
        @Param("accountId") Integer accountId,
        @Param("invoicedFromInclusive") LocalDateTime invoicedFromInclusive,
        @Param("invoicedToExclusive") LocalDateTime invoicedToExclusive,
        @Param("externalChargeCategoryId") Integer externalChargeCategoryId,
        @Param("invoiceCategories") List <String> invoiceCategories);
}
