package ca.ids.abms.modules.exemptions.account;

import ca.ids.abms.config.db.ABMSRepository;
import ca.ids.abms.modules.accounts.Account;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountExemptionRepository extends ABMSRepository<AccountExemption, Integer> {

    AccountExemption findOneByAccount(Account account);

    AccountExemption findOneByAccountId(Integer accountId);
}
