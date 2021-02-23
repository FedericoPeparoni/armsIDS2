package ca.ids.abms.modules.bankaccount;

import ca.ids.abms.modules.common.services.AbmsCrudService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@SuppressWarnings("WeakerAccess")
class BankAccountService extends AbmsCrudService<BankAccount, Integer> {

    private final BankAccountRepository bankAccountRepository;

    BankAccountService(final BankAccountRepository bankAccountRepository) {
        super(bankAccountRepository);
        this.bankAccountRepository = bankAccountRepository;
    }

    /**
     * Find all BankAccount records.
     */
    @Transactional(readOnly = true)
    public List<BankAccount> findAll() {
        return bankAccountRepository.findAll();
    }

    /**
     * Bank accounts are supported if one or more configured
     * in the system.
     *
     * @return true if bank accounts are supported
     */
    @Transactional(readOnly = true)
    public Boolean isSupported() {
        return bankAccountRepository.count() > 0;
    }
}
