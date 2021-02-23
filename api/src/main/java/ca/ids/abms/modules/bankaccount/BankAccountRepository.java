package ca.ids.abms.modules.bankaccount;

import ca.ids.abms.config.db.ABMSRepository;
import org.springframework.stereotype.Repository;

@Repository
interface BankAccountRepository extends ABMSRepository<BankAccount, Integer> {
}
