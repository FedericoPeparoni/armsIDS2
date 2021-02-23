package ca.ids.abms.modules.exemptions.account;

import ca.ids.abms.config.db.FiltersSpecification;
import ca.ids.abms.modules.exemptions.ExemptionType;
import ca.ids.abms.modules.exemptions.ExemptionTypeProvider;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import com.google.common.base.Preconditions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.config.error.ExceptionFactory;
import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.accounts.AccountRepository;
import ca.ids.abms.modules.util.models.ModelUtils;

import java.util.ArrayList;
import java.util.Collection;

@Service
@Transactional
public class AccountExemptionService implements ExemptionTypeProvider {

    private AccountExemptionRepository accountExemptionRepository;
    private AccountRepository accountRepository;

    AccountExemptionService(AccountExemptionRepository accountExemptionRepository, AccountRepository accountRepository) {
        this.accountExemptionRepository = accountExemptionRepository;
        this.accountRepository = accountRepository;
    }

    public AccountExemption create(AccountExemption item, Integer accountId) {
        final Account account = this.accountRepository.getOne(accountId);
        item.setAccount(account);
        return accountExemptionRepository.save(item);
    }

    @Transactional(readOnly = true)
    public AccountExemption getOne(Integer id) {
        return accountExemptionRepository.getOne(id);
    }

    @Transactional(readOnly = true)
    public AccountExemption findOneByAccountId (Integer accountId) {
        return accountExemptionRepository.findOneByAccountId(accountId);
    }

    @Transactional(readOnly = true)
    public Page<AccountExemption> findAll(Pageable pageable, final String textSearch) {
        final FiltersSpecification.Builder filterBuilder = new FiltersSpecification.Builder().lookFor(textSearch);
        return accountExemptionRepository.findAll(filterBuilder.build(), pageable);
    }

    public AccountExemption update(Integer id, AccountExemption item, Integer accountId) {
        final AccountExemption existingItem = accountExemptionRepository.getOne(id);
        try {
            ModelUtils.merge(item, existingItem, "id", "account");
            final Account account = this.accountRepository.getOne(accountId);
            existingItem.setAccount(account);
            return accountExemptionRepository.save(existingItem);
        } catch (RuntimeException e) {
            throw ExceptionFactory.persistenceDataManagement(e,ErrorConstants.ERR_UPDATE_NO_LONGER_EXISTS);
        }
    }

    public void delete(Integer id) {
        try {
            accountExemptionRepository.delete(id);
        } catch (RuntimeException e) {
            throw ExceptionFactory.persistenceDataManagement(e,ErrorConstants.ERR_DELETE_NO_LONGER_EXISTS);
        }
    }

    /**
     * Return applicable AccountExemption by provided flight movement.
     */
    @Override
    @Transactional(readOnly = true)
    public Collection<ExemptionType> findApplicableExemptions(FlightMovement flightMovement) {
        Preconditions.checkArgument(flightMovement != null);

        Collection<ExemptionType> exemptions = new ArrayList<>();
        if (flightMovement.getAccount() != null) {

            // find exemption by flight movement account
            AccountExemption exemption = accountExemptionRepository
                .findOneByAccount(flightMovement.getAccount());

            // only add to collection if not null
            if (exemption != null) {
                exemptions.add(exemption);
            }
        }

        return exemptions;
    }

    public long countAll() {
        return accountExemptionRepository.count();
    }
}
