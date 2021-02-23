package ca.ids.abms.modules.billingcenters;

import ca.ids.abms.config.db.ABMSRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

public interface BillingCenterRepository extends ABMSRepository<BillingCenter, Integer> {

    @Query(value = "SELECT * FROM billing_centers WHERE is_hq ORDER BY id LIMIT 1", nativeQuery = true)
    BillingCenter findHq();

    @Query(value = "SELECT invoice_number FROM billing_ledgers WHERE invoice_number ~ :numberPrefixRegex ORDER BY invoice_number DESC LIMIT 1", nativeQuery = true)
    String lastInvoiceNumber(@Param("numberPrefixRegex") String numberPrefixRegex);

    @Query(value = "SELECT receipt_number FROM transactions WHERE receipt_number ~ :numberPrefixRegex ORDER BY receipt_number DESC LIMIT 1", nativeQuery = true)
    String lastReceiptNumber(@Param("numberPrefixRegex") String numberPrefixRegex);

    Collection<BillingCenter> findAllByOrderByName();
    
    @Query(value =" SELECT * FROM billing_centers WHERE id=:id FOR NO KEY UPDATE", nativeQuery = true)
    void lockNoKeyUpdate(@Param("id") Integer id);
}
