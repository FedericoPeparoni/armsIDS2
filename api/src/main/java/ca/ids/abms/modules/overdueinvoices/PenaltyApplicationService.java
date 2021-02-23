package ca.ids.abms.modules.overdueinvoices;

import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.billings.BillingLedger;
import ca.ids.abms.modules.billings.InvoiceOverduePenalty;
import ca.ids.abms.modules.billings.InvoiceOverduePenaltyRepository;
import ca.ids.abms.modules.transactions.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class PenaltyApplicationService {

    private final Logger log = LoggerFactory.getLogger(PenaltyApplicationService.class);

    private final TransactionService transactionService;
    private final InvoiceOverduePenaltyRepository invoiceOverduePenaltyRepository;
    private final OverdueBaseService overdueBaseService;

    public PenaltyApplicationService(final TransactionService transactionService,
                                     final InvoiceOverduePenaltyRepository invoiceOverduePenaltyRepository,
                                     final OverdueBaseService overdueBaseService) {
        this.transactionService = transactionService;
        this.invoiceOverduePenaltyRepository = invoiceOverduePenaltyRepository;
        this.overdueBaseService = overdueBaseService;
    }

    public void applyPenaltyToAccount(final Account account) {
        final List<BillingLedger> existingOverdueInvoiceList = overdueBaseService.findExistingOverdueInvoices(account);

        if (existingOverdueInvoiceList != null && !existingOverdueInvoiceList.isEmpty()) {
            for (BillingLedger existingOverdueInvoice : existingOverdueInvoiceList) {
                final InvoiceOverduePenalty invoiceOverduePenalty = invoiceOverduePenaltyRepository
                    .findMostRecentInvoiceOverduePenaltyByInvoiceId(existingOverdueInvoice.getId());
                final LocalDate now = LocalDate.now();
                final LocalDate dueDate = existingOverdueInvoice.getPaymentDueDate().toLocalDate();
                final Integer penaltyNumberOfMonth = OverdueBaseService.calculateNumberOfMonth(dueDate, now) + 1;
                final LocalDate newPenaltyPeriodEndDate = dueDate.plusMonths(penaltyNumberOfMonth);

                if (invoiceOverduePenalty == null) {
                    // A new penalty will be added for a given invoice through a debit transaction
                    // Create an existing Invoice Overdue Penalty
                    // Create a debit transaction

                    final InvoiceOverduePenalty newInvoiceOverduePenalty = overdueBaseService.createInvoiceOverduePenalty(existingOverdueInvoice, null, newPenaltyPeriodEndDate, penaltyNumberOfMonth);
                    if (newInvoiceOverduePenalty != null) {
                        log.debug("Create a new penalty for the Overdue invoice #{}, invoice amount:{} owing:{} account:{} penalty:{} and date:{}",
                            existingOverdueInvoice.getInvoiceNumber(), existingOverdueInvoice.getInvoiceAmount(),
                            existingOverdueInvoice.getAmountOwing(), account.getName(),
                            newInvoiceOverduePenalty.getDefaultPenaltyAmount() + newInvoiceOverduePenalty.getPunitivePenaltyAmount(),
                            newInvoiceOverduePenalty.getPenaltyPeriodEndDate());

                        transactionService.createPenaltiesTransaction(newInvoiceOverduePenalty);
                    }
                } else {
                    // There is already a penalty for a given invoice
                    final LocalDate lastPenaltyPeriodEndDate = invoiceOverduePenalty.getPenaltyPeriodEndDate().toLocalDate();
                    if (lastPenaltyPeriodEndDate.isBefore(now)) {
                        final InvoiceOverduePenalty newInvoiceOverduePenalty = overdueBaseService.createInvoiceOverduePenalty
                            (existingOverdueInvoice, null, newPenaltyPeriodEndDate, penaltyNumberOfMonth);
                        if (newInvoiceOverduePenalty != null) {
                            log.debug("Add an additional penalty for the Overdue invoice #{}, invoice amount:{} owing:{} account:{} penalty:{} and date:{}",
                                existingOverdueInvoice.getInvoiceNumber(), existingOverdueInvoice.getInvoiceAmount(),
                                existingOverdueInvoice.getAmountOwing(), account.getName(),
                                newInvoiceOverduePenalty.getDefaultPenaltyAmount() + newInvoiceOverduePenalty.getPunitivePenaltyAmount(),
                                newInvoiceOverduePenalty.getPenaltyPeriodEndDate());

                            transactionService.createPenaltiesTransaction(newInvoiceOverduePenalty);
                        }
                    }
                }
            }
        }
    }
}
