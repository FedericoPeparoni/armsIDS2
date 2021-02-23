package ca.ids.abms.plugins.prototype.modules.transactions;

import ca.ids.abms.modules.transactions.Transaction;
import org.springframework.stereotype.Component;

@Component
public class PrototypeTransactionMapper {

    public PrototypeTransaction toPrototypeTransaction(Transaction transaction) {

        PrototypeTransaction prototypeTransaction = new PrototypeTransaction();

        prototypeTransaction.setAmount(transaction.getAmount());
        prototypeTransaction.setDescription(transaction.getDescription());
        prototypeTransaction.setDate(transaction.getTransactionDateTime());
        prototypeTransaction.setAccountId(transaction.getAccount() != null ? transaction.getAccount().getId() : null);
        prototypeTransaction.setCurrencyId(transaction.getCurrency() != null ? transaction.getCurrency().getId() : null);
        prototypeTransaction.setTypeId(transaction.getTransactionType() != null ? transaction.getTransactionType().getId() : null);

        return prototypeTransaction;

    }
}
