package ca.ids.abms.modules.exemptions.account;

import ca.ids.abms.config.db.FiltersSpecification;
import ca.ids.abms.modules.accounts.AccountRepository;
import ca.ids.abms.modules.accounts.Account;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;

public class AccountExemptionTest {
    private AccountExemptionRepository accountExemptionRepository;
    private AccountExemptionService accountExemptionService;
    private AccountRepository accountRepository;

    @Before
    public void setup() {
        accountExemptionRepository = mock(AccountExemptionRepository.class);
        accountRepository = mock(AccountRepository.class);
        accountExemptionService = new AccountExemptionService(accountExemptionRepository, accountRepository);
    }

    @Test
    public void getAllItems() {
        List<AccountExemption> accountExemptions = Collections.singletonList(new AccountExemption());

        when(accountExemptionRepository.findAll(any(FiltersSpecification.class), any(Pageable.class)))
        .thenReturn(new PageImpl<>(accountExemptions));

        Page<AccountExemption> results = accountExemptionService.findAll(mock(Pageable.class), null);

        assertThat(results.getTotalElements()).isEqualTo(accountExemptions.size());
    }

    @Test
    public void getItemById() {
        Account account = new Account();
        account.setId(2);
        AccountExemption accountExemption = new AccountExemption();
        accountExemption.setId(1);
        accountExemption.setAccount(account);

        when(accountExemptionRepository.getOne(any()))
        .thenReturn(accountExemption);
        when(accountRepository.getOne(any()))
                .thenReturn(account);

        AccountExemption result = accountExemptionService.getOne(1);
        assertThat(result).isEqualTo(accountExemption);
        assertThat(result.getAccount().getId()).isEqualTo(account.getId());
    }

    @Test
    public void getItemByAccountId() {
        Account account = new Account();
        account.setId(2);
        AccountExemption accountExemption = new AccountExemption();
        accountExemption.setId(1);
        accountExemption.setAccount(account);

        when(accountExemptionRepository.findOneByAccountId(any()))
            .thenReturn(accountExemption);
        when(accountRepository.getOne(any()))
            .thenReturn(account);

        AccountExemption result = accountExemptionService.findOneByAccountId(2);
        assertThat(result).isEqualTo(accountExemption);
        assertThat(result.getAccount().getId()).isEqualTo(account.getId());
    }

    @Test
    public void createItem() {
        Account account = new Account();
        account.setId(2);
        AccountExemption accountExemption = new AccountExemption();
        accountExemption.setId(1);
        accountExemption.setAccount(account);
        when(accountExemptionRepository.save(any(AccountExemption.class)))
        .thenReturn(accountExemption);
        when(accountRepository.getOne(any()))
                .thenReturn(account);

        AccountExemption result = accountExemptionService.create(accountExemption, 2);
        Assert.assertEquals(result.getId(), accountExemption.getId());
        Assert.assertEquals(result.getAccount().getId(), accountExemption.getAccount().getId());
    }

    @Test
    public void updateItem() {
        Account firstAccount = new Account();
        firstAccount.setId(1);
        Account secondAccount = new Account();
        secondAccount.setId(2);
        AccountExemption existingAccountExemption = new AccountExemption();
        existingAccountExemption.setId(0);
        existingAccountExemption.setAccount(firstAccount);

        AccountExemption accountExemption = new AccountExemption();
        accountExemption.setId(0);
        accountExemption.setAccount(secondAccount);

        when(accountExemptionRepository.getOne(any()))
        .thenReturn(existingAccountExemption);

        when(accountExemptionRepository.save(any(AccountExemption.class)))
        .thenReturn(existingAccountExemption);

        when(accountRepository.getOne(any()))
                .thenReturn(secondAccount);

        AccountExemption result = accountExemptionService.update(1, accountExemption, 2);

        Assert.assertEquals(result.getId(), accountExemption.getId());
        Assert.assertEquals(result.getAccount().getId(), accountExemption.getAccount().getId());
    }

    @Test
    public void deleteItem() {
        accountExemptionService.delete(1);
        verify(accountExemptionRepository).delete(any(Integer.class));
    }
}
