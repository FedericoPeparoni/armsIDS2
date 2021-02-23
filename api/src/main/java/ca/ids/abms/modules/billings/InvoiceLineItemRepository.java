package ca.ids.abms.modules.billings;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoiceLineItemRepository extends JpaRepository<InvoiceLineItem, Integer> {

    List<InvoiceLineItem> findByBillingLedger(BillingLedger billingLedger);

}
