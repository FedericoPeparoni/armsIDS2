package ca.ids.abms.modules.accounts;

import ca.ids.abms.modules.util.models.ModelUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AccountExternalChargeCategoryService {

    private final AccountExternalChargeCategoryRepository accountExternalChargeCategoryRepository;

    public AccountExternalChargeCategoryService(
        final AccountExternalChargeCategoryRepository accountExternalChargeCategoryRepository) {
        this.accountExternalChargeCategoryRepository = accountExternalChargeCategoryRepository;
    }

    @Transactional(readOnly = true)
    public List<AccountExternalChargeCategory> findByAccount(final Integer accountId) {
        return findByAccount(accountId, null);
    }

    @Transactional(readOnly = true)
    public List<AccountExternalChargeCategory> findByAccount(final Integer accountId, final Integer externalChargeCategoryId) {
        if (externalChargeCategoryId != null)
            return accountExternalChargeCategoryRepository
                .findByAccountIdAndExternalChargeCategoryId(accountId, externalChargeCategoryId);
        else
            return accountExternalChargeCategoryRepository.findByAccountId(accountId);
    }

    @Transactional(readOnly = true)
    public AccountExternalChargeCategory findOneByExternalSystemIdentifier(final String identifier) {
        if (identifier != null && !identifier.isEmpty())
            return accountExternalChargeCategoryRepository.findOneByExternalSystemIdentifier(identifier);
        else
            return null;
    }

    @Transactional
    public AccountExternalChargeCategory create(
        final AccountExternalChargeCategory accountExternalChargeCategory
    ) {
        return accountExternalChargeCategoryRepository.save(accountExternalChargeCategory);
    }

    @Transactional
    public AccountExternalChargeCategory update(
        final AccountExternalChargeCategory accountExternalChargeCategory
    ) {
        AccountExternalChargeCategory existing = accountExternalChargeCategoryRepository
            .findOne(accountExternalChargeCategory.getId());
        ModelUtils.merge(accountExternalChargeCategory, existing);
        return accountExternalChargeCategoryRepository.save(existing);
    }

    @Transactional
    public void delete(
        final Integer id
    ) {
        accountExternalChargeCategoryRepository.delete(id);
    }
}
