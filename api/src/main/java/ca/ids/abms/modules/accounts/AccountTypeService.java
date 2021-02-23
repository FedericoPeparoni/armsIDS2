package ca.ids.abms.modules.accounts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AccountTypeService {

    private final Logger log = LoggerFactory.getLogger(AccountTypeService.class);
    private final AccountTypeRepository accountTypeRepository;

    public AccountTypeService(AccountTypeRepository accountTypeRepository) {
        this.accountTypeRepository = accountTypeRepository;
    }

    @Transactional(readOnly = true)
    public List<AccountType> findAll() {
        log.debug("Request to get all types");
        return accountTypeRepository.findAll();
    }

}
