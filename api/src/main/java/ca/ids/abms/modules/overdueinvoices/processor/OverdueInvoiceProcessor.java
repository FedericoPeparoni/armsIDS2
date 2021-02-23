package ca.ids.abms.modules.overdueinvoices.processor;

import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.overdueinvoices.OverdueInvoiceService;
import ca.ids.abms.modules.overdueinvoices.PenaltyApplicationService;
import ca.ids.abms.modules.system.SystemConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class OverdueInvoiceProcessor {

    private final OverdueInvoiceService overdueInvoiceProcessorService;
    private static final Logger log = LoggerFactory.getLogger(OverdueInvoiceProcessor.class);
    private final SystemConfigurationService systemConfigurationService;
    private final PenaltyApplicationService penaltyApplicationService;

    public OverdueInvoiceProcessor(
        final OverdueInvoiceService overdueInvoiceProcessorService,
        final SystemConfigurationService systemConfigurationService,
        final PenaltyApplicationService penaltyApplicationService
    ) {
        this.overdueInvoiceProcessorService = overdueInvoiceProcessorService;
        this.systemConfigurationService = systemConfigurationService;
        this.penaltyApplicationService = penaltyApplicationService;
    }

    /*
     * Overdue invoice processor for automating penalty application.
     *
     * Runs at STARTING_TIME
     * Retrieves list of invoices flagged as overdue.
     *
     * An overdue invoice is defined as:
     *  1. Issued more than CREDIT TERM days ago
     *  2. Invoice status is either PUBLISHED or APPROVED (not paid)
     *
     * Currently in use only for KCAA
     */
    @Scheduled(cron = "${app.kcaa.overdue-invoice.schedule}")
    protected void doProcess() throws InterruptedException {
        log.debug("Started automated processing of overdue invoices.");

        if (!systemConfigurationService.getBoolean("Calculate overdue invoice penalties daily")) {
            log.debug("Halted automated processing of overdue invoices: organisation not supported.");
            return;
        }

        List<Account> accountsWithUnpaidInvoices = overdueInvoiceProcessorService.findAllAccountsWithOverdueInvoices();

        if (accountsWithUnpaidInvoices.size() > 0) {
            log.debug("Processing {} accounts with unpaid invoices ...", accountsWithUnpaidInvoices.size());

            for (final Account account : accountsWithUnpaidInvoices) {
                createBillingLedgerAndPenalty(account);
            }
        } else {
            log.debug("There are no accounts with overdue invoices to process at this time.");
        }
    }

    @Transactional
    private void createBillingLedgerAndPenalty(Account account) {
        log.debug("Processing the account {} with unpaid invoices ...", account.getName());
        penaltyApplicationService.applyPenaltyToAccount(account);
    }
}
