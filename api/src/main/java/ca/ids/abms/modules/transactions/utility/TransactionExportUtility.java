package ca.ids.abms.modules.transactions.utility;

import ca.ids.abms.modules.common.utilities.AbstractExportUtility;
import ca.ids.abms.modules.transactions.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

@Component
public class TransactionExportUtility extends AbstractExportUtility<Transaction> {

    private final TransactionService transactionService;

    TransactionExportUtility(
        final PlatformTransactionManager transactionManager,
        final TransactionService transactionService
    ) {
        super(transactionManager);
        this.transactionService = transactionService;
    }

    protected void exportOne(Transaction transaction) {
        transactionService.export(transaction);
    }

    protected List<Transaction> findAllExportable() {
        return transactionService.findAllUnexported();
    }

    protected Transaction findOneById(Integer id) {
        return transactionService.getOne(id);
    }

    protected Class<Transaction> getEntityClass() {
        return Transaction.class;
    }

    protected String getReferenceNumber(Transaction transaction) {
        return transaction == null || transaction.getReceiptNumber() == null || transaction.getReceiptNumber().isEmpty()
            ? "Unknown Transaction" : transaction.getReceiptNumber();
    }
}
