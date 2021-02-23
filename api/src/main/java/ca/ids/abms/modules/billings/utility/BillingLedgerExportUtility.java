package ca.ids.abms.modules.billings.utility;

import ca.ids.abms.modules.billings.BillingLedger;
import ca.ids.abms.modules.billings.BillingLedgerService;
import ca.ids.abms.modules.common.utilities.AbstractExportUtility;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

@Component
public class BillingLedgerExportUtility extends AbstractExportUtility<BillingLedger> {

    private final BillingLedgerService billingLedgerService;

    BillingLedgerExportUtility(
        final BillingLedgerService billingLedgerService,
        final PlatformTransactionManager transactionManager
    ) {
        super(transactionManager);
        this.billingLedgerService = billingLedgerService;
    }

    protected void exportOne(BillingLedger billingLedger) {
        billingLedgerService.export(billingLedger);
    }

    protected List<BillingLedger> findAllExportable() {
        return billingLedgerService.findAllUnexported();
    }

    protected BillingLedger findOneById(Integer id) {
        return billingLedgerService.findOne(id);
    }

    protected Class<BillingLedger> getEntityClass() {
        return BillingLedger.class;
    }

    protected String getReferenceNumber(BillingLedger billingLedger) {
        return billingLedger == null || billingLedger.getInvoiceNumber() == null || billingLedger.getInvoiceNumber().isEmpty()
            ? "Unknown Invoice" : billingLedger.getInvoiceNumber();
    }
}
