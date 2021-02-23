package ca.ids.abms.modules.accounts;

import ca.ids.abms.config.db.FiltersSpecification;
import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.modules.billings.BillingLedgerRepository;
import ca.ids.abms.modules.countries.CountryRepository;
import ca.ids.abms.modules.exemptions.account.AccountExemptionRepository;
import ca.ids.abms.modules.flightmovements.FlightMovementRepository;
import ca.ids.abms.modules.notifications.NotificationEventTypeRepository;
import ca.ids.abms.modules.selfcareportal.inactivityexpirynotices.SelfCarePortalInactivityExpiryNoticesService;
import ca.ids.abms.modules.selfcareportal.querysubmission.QuerySubmissionService;
import ca.ids.abms.modules.system.SystemConfiguration;
import ca.ids.abms.modules.system.SystemConfigurationRepository;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;
import ca.ids.abms.modules.transactions.Transaction;
import ca.ids.abms.modules.transactions.TransactionRepository;
import ca.ids.abms.modules.users.User;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class AccountServiceTest {

    private AccountRepository accountRepository;
    private AccountService accountService;
    private SystemConfigurationService systemConfigurationService;

    @Before
    public void setup() {
        accountRepository = mock(AccountRepository.class);
        AccountExemptionRepository accountExemptionRepository = mock(AccountExemptionRepository.class);
        FlightMovementRepository flightMovementRepository = mock(FlightMovementRepository.class);
        BillingLedgerRepository billingLedgerRepository = mock(BillingLedgerRepository.class);
        AccountEventMapRepository accountEventMapRepository = mock(AccountEventMapRepository.class);
        NotificationEventTypeRepository notificationEventTypeRepository = mock(NotificationEventTypeRepository.class);
        CountryRepository countryRepository = mock(CountryRepository.class);
        SelfCarePortalInactivityExpiryNoticesService selfCarePortalInactivityExpiryNoticesService = mock(SelfCarePortalInactivityExpiryNoticesService.class);
        QuerySubmissionService querySubmissionService = mock(QuerySubmissionService.class);

        accountService = new AccountService(accountRepository, accountExemptionRepository, flightMovementRepository,
            billingLedgerRepository, accountEventMapRepository, notificationEventTypeRepository, selfCarePortalInactivityExpiryNoticesService, querySubmissionService);

        SystemConfigurationRepository sysConfRepo = mock(SystemConfigurationRepository.class);
        TransactionRepository transactionRepo= mock(TransactionRepository.class);
        FlightMovementRepository flightMovementRepo = mock(FlightMovementRepository.class);
        sysConfRepo.save(getSysConf(SystemConfigurationItemName.DFLT_ACCOUNT_MAX_CREDIT_LIMIT, "100"));
        sysConfRepo.save(getSysConf(SystemConfigurationItemName.DFLT_ACCOUNT_MIN_CREDIT_LIMIT, "1"));
        systemConfigurationService = new SystemConfigurationService(sysConfRepo, transactionRepo, flightMovementRepo, countryRepository);
        accountService.setSystemConfigurationService(systemConfigurationService);
    }

    private SystemConfiguration getSysConf(String name, String val) {
        SystemConfiguration sysConf = new SystemConfiguration();
        sysConf.setItemName(name);
        sysConf.setCurrentValue(val);
        return sysConf;
    }

    @Test
    public void getAllAccounts() {
        List<Account> accounts = Collections.singletonList(new Account());

        when(accountRepository.findAll(any(FiltersSpecification.class), any(Pageable.class)))
        .thenReturn(new PageImpl<>(accounts));

        Page<Account> results = accountService.findAllAccountsByFilter(null, null, "",null, null,
                mock(Pageable.class), null, null, null, null, null, null);

        assertThat(results.getTotalElements()).isEqualTo(accounts.size());
    }

    @Test
    public void getAccountById() {
        Account account = new Account();
        account.setId(1);

        when(accountRepository.getOne(any()))
        .thenReturn(account);

        Account result = accountService.getOne(1);
        assertThat(result).isEqualTo(account);
    }

    @Test
    public void createAccount() {
        Account account = new Account();
        account.setName("name");
        account.setCreditLimit((double) 10);
        account.setCashAccount(false);
        account.setIsSelfCare(false);

        List<String> existingAccountNames = new ArrayList<>();
        existingAccountNames.add("test");

        when(accountRepository.save(any(Account.class)))
        .thenReturn(account);

        when(accountRepository.findAllNames())
            .thenReturn(existingAccountNames);

        Account result = accountService.save(account);
        assertThat(result.getName()).isEqualTo(account.getName());
    }

    @Test
    public void updateAccount() {
        Account existingAccount = new Account();
        existingAccount.setId(1);
        existingAccount.setName("name");

        Account account = new Account();
        account.setName("new name");
        account.setCreditLimit((double) 10);
        account.setCashAccount(false);
        account.setIsSelfCare(false);
        when(accountRepository.getOne(any()))
        .thenReturn(existingAccount);

        when(accountRepository.save(any(Account.class)))
        .thenReturn(existingAccount);

        Account result = accountService.update(1, account);

        assertThat(result.getName()).isEqualTo("new name");
    }

    @Test
    public void deleteUserWithoutTransactions() {
        final Account accountWithoutTransactions = new Account();
        accountWithoutTransactions.setId(2);
        accountWithoutTransactions.setName("ACC_NOT");

        when(accountService.getOne(2)).thenReturn(accountWithoutTransactions);

        accountService.delete(2);
        verify(accountRepository).delete(any(Integer.class));
    }

    @Test(expected=CustomParametrizedException.class)

    public void deleteUserWithTransactions() {
        final Transaction transaction = new Transaction();
        transaction.setId(1);
        final HashSet<Transaction> transactions = new HashSet<>(1);
        transactions.add(transaction);

        final Account accountWithTransactions = new Account();
        accountWithTransactions.setId(1);
        accountWithTransactions.setName("ACC_TRA");
        accountWithTransactions.setTransactions(transactions);
        transaction.setAccount(accountWithTransactions);

        when(accountService.getOne(1)).thenReturn(accountWithTransactions);

        accountService.delete(1);
    }

    @Test
    public void setIndicatorFalseIfOverrideTrue() {
        Account account = new Account();
        account.setId(1);
        account.setName("test");
        account.setBlackListedIndicator(Boolean.TRUE);
        account.setBlackListedOverride(Boolean.TRUE);
        account.setCreditLimit((double) 10);
        account.setIsSelfCare(false);
        account.setCashAccount(false);
        accountService.save(account);

        when(accountRepository.getOne(1))
            .thenReturn(account);

        when(accountRepository.findAllNames())
            .thenReturn(new ArrayList<>());

        Account result = accountService.getOne(1);

        assertThat(result.getBlackListedIndicator()).isFalse();
    }

    @Test
    public void findAccountsWithCreditLimits() {
        Account account = new Account();
        account.setCreditLimit(5.00);

        List<Account> accountsList = new ArrayList<Account>();
        accountsList.add(account);

        when(accountRepository.findAll(any(FiltersSpecification.class), any(Pageable.class)))
            .thenReturn(new PageImpl<Account>(accountsList, null, accountsList.size()));

        Page<Account> results = accountService.findAllAccountsByFilter(false, null, "", true, null,
                mock(Pageable.class), null, null, null, null, null, null);

        assertThat(results.getContent().size()).isEqualTo(1);
    }

    @Test
    public void validateCreditLimitMin() {

        Account account = new Account();
        account.setName("test");
        account.setCreditLimit((double)20);
        account.setIsSelfCare(false);
        account.setCashAccount(false);

        SystemConfiguration min = new SystemConfiguration();
        min.setCurrentValue("10.00");

        when(systemConfigurationService.getOneByItemName(SystemConfigurationItemName.DFLT_ACCOUNT_MIN_CREDIT_LIMIT))
            .thenReturn(min);

        when(accountRepository.findAllNames()).thenReturn(new ArrayList<>());

        accountService.save(account);

        try {
            account.setCreditLimit((double)0);
            accountService.save(account);
        } catch (CustomParametrizedException e) {
            assertThat(e.getCause().getMessage()).isEqualTo("Credit limit 0 exceeds its minimum limit 10.");
        }
    }

    @Test
    public void validateCreditLimitMax() {

        Account account = new Account();
        account.setName("test");
        account.setCreditLimit((double)1);
        account.setIsSelfCare(false);
        account.setCashAccount(false);

        SystemConfiguration max = new SystemConfiguration();
        max.setCurrentValue("10.00");

        when(systemConfigurationService.getOneByItemName(SystemConfigurationItemName.DFLT_ACCOUNT_MAX_CREDIT_LIMIT))
            .thenReturn(max);

        when(accountRepository.findAllNames()).thenReturn(new ArrayList<>());

        accountService.save(account);

        try {
            account.setCreditLimit((double)20);
            accountService.save(account);
        } catch (CustomParametrizedException e) {
            assertThat(e.getCause().getMessage()).isEqualTo("Credit limit 20 exceeds its maximum limit 10.");
        }
    }
    
    @Test
    public void validateMaxUsersPerAccount() {
    	final SystemConfiguration maxAccountsPerUser = new SystemConfiguration();
    	maxAccountsPerUser.setCurrentValue("999");
        when (systemConfigurationService.getOneByItemName(SystemConfigurationItemName.MAX_ACCOUNTS_PER_USER))
    		.thenReturn (maxAccountsPerUser);
        
    	final SystemConfiguration maxUsersPerAccount = new SystemConfiguration();
        maxUsersPerAccount.setCurrentValue ("999");
        when (systemConfigurationService.getOneByItemName(SystemConfigurationItemName.MAX_USERS_PER_ACCOUNT))
    		.thenReturn (maxUsersPerAccount);
        
    	final SystemConfiguration dfltAccountMaxCreditLimit = new SystemConfiguration();
    	dfltAccountMaxCreditLimit.setCurrentValue("999999.00");
        when (systemConfigurationService.getOneByItemName(SystemConfigurationItemName.DFLT_ACCOUNT_MAX_CREDIT_LIMIT))
    		.thenReturn (dfltAccountMaxCreditLimit);
    	
    	final Account account = new Account();
        account.setName("name");
        account.setCreditLimit((double) 10);
        account.setCashAccount(false);
        account.setIsSelfCare(false);
        
    	final User u1 = new User();
    	u1.setId (1);
    	final User u2 = new User();
    	u2.setId (2);
    	
    	// max_users_per_account = 0
        maxUsersPerAccount.setCurrentValue("0");
        // ok
        accountService.save (account);
        // exception
        account.setAccountUsers (Collections.singletonList(u1));
        assertThatThrownBy(()->accountService.save (account)).hasMessageMatching(".*account cannot have more than 0 user.*");
        
    	// max_users_per_account = 1
        maxUsersPerAccount.setCurrentValue("1");
        // ok
        accountService.save (account);
        // ok
        account.setAccountUsers (Collections.singletonList(u1));
        accountService.save (account);
        // exception
        account.setAccountUsers (Arrays.asList(u1, u2));
        assertThatThrownBy(()->accountService.save (account)).hasMessageMatching(".*account cannot have more than 1 user.*");
    }
    
    @Test
    public void validateMaxAccountsPerUser() {
    	final SystemConfiguration maxAccountsPerUser = new SystemConfiguration();
        when (systemConfigurationService.getOneByItemName(SystemConfigurationItemName.MAX_ACCOUNTS_PER_USER))
    		.thenReturn (maxAccountsPerUser);
        
    	final SystemConfiguration maxUsersPerAccount = new SystemConfiguration();
        maxUsersPerAccount.setCurrentValue ("999");
        when (systemConfigurationService.getOneByItemName(SystemConfigurationItemName.MAX_USERS_PER_ACCOUNT))
    		.thenReturn (maxUsersPerAccount);
        
    	final SystemConfiguration dfltAccountMaxCreditLimit = new SystemConfiguration();
    	dfltAccountMaxCreditLimit.setCurrentValue("999999.00");
        when (systemConfigurationService.getOneByItemName(SystemConfigurationItemName.DFLT_ACCOUNT_MAX_CREDIT_LIMIT))
    		.thenReturn (dfltAccountMaxCreditLimit);
        
    	final Account a1 = new Account();
    	a1.setName("account 1");
    	a1.setCreditLimit((double) 10);
    	a1.setCashAccount(false);
    	a1.setIsSelfCare(false);
    	
    	final Account a2 = new Account();
    	a2.setName("account 2");
    	a2.setCreditLimit((double) 10);
    	a2.setCashAccount(false);
    	a2.setIsSelfCare(false);
    	
    	final Account a3 = new Account();
    	a3.setName("account 3");
    	a3.setCreditLimit((double) 10);
    	a3.setCashAccount(false);
    	a3.setIsSelfCare(false);
    	
    	final User u1 = new User();
    	u1.setId (1);
    	u1.setName ("user 1");
    	final User u2 = new User();
    	u2.setId (2);
    	u2.setName ("user 2");
    	
    	// ok - no users
    	maxAccountsPerUser.setCurrentValue("0");
    	a1.setId (null);
    	accountService.save (a1);
    	a1.setId (1);
    	accountService.update (1, a1);
    	
    	// one user, exceeds limit: insert
    	maxAccountsPerUser.setCurrentValue("0");
    	a1.setId (null);
    	a2.setId (2);
    	a1.setAccountUsers(Collections.singletonList(u1));
    	when (accountRepository.findByUserId(1))
    		.thenReturn (Collections.singletonList(a2));
    	assertThatThrownBy (()->accountService.save (a1)).hasMessageMatching(".*User 'user 1' exceeds the configured maximum of 0 associated account\\(s\\)");
    	// one user, exceeds limit: update
    	a1.setId (1);
    	when (accountRepository.findByUserId(1))
			.thenReturn (Collections.singletonList(a1));
    	assertThatThrownBy (()->accountService.update (1, a1)).hasMessageMatching(".*User 'user 1' exceeds the configured maximum of 0 associated account\\(s\\)");

    	// one user, within limit: insert
    	maxAccountsPerUser.setCurrentValue("1");
    	a1.setId (null);
    	a1.setAccountUsers(Collections.singletonList(u1));
    	when (accountRepository.findByUserId(1))
			.thenReturn (null);
    	accountService.save (a1);
    	// one user, within limit: update
    	a1.setId (1);
    	a1.setAccountUsers (Collections.singletonList(u1));
    	when (accountRepository.findByUserId(1))
			.thenReturn (Collections.singletonList(a1));
    	accountService.update (1, a1);
    	
    	// one user, exceeds limit: insert
    	maxAccountsPerUser.setCurrentValue("1");
    	a1.setId (null);
    	a1.setAccountUsers(Collections.singletonList(u1));
    	when (accountRepository.findByUserId(1))
			.thenReturn (Arrays.asList (a1, a2));
    	assertThatThrownBy (()->accountService.save (a1)).hasMessageMatching(".*User 'user 1' exceeds the configured maximum of 1 associated account\\(s\\)");
    	when (accountRepository.findByUserId(1))
			.thenReturn (Collections.singletonList(a2));
    	assertThatThrownBy (()->accountService.save (a1)).hasMessageMatching(".*User 'user 1' exceeds the configured maximum of 1 associated account\\(s\\)");
    	
    	// one user, exceeds limit: update
    	maxAccountsPerUser.setCurrentValue("1");
    	a1.setId (null);
    	a1.setAccountUsers(Collections.singletonList(u1));
    	when (accountRepository.findByUserId(1))
			.thenReturn (Arrays.asList (a1, a2));
    	assertThatThrownBy (()->accountService.update (1, a1)).hasMessageMatching(".*User 'user 1' exceeds the configured maximum of 1 associated account\\(s\\)");
    	when (accountRepository.findByUserId(1))
			.thenReturn (Collections.singletonList(a2));
    	assertThatThrownBy (()->accountService.update (1, a1)).hasMessageMatching(".*User 'user 1' exceeds the configured maximum of 1 associated account\\(s\\)");
    }

    @Test
    public void findAllActive() {

        List<Account> accounts = Collections.singletonList(new Account());

        when(accountRepository.findAll(any(FiltersSpecification.class), any(Pageable.class)))
            .thenReturn(new PageImpl<Account>(accounts, null, accounts.size()));

        Page<Account> results = accountService.findAllAccountsByFilter(true, null, null, null,"" ,
                mock(Pageable.class), null, null, null, null, null, null);

        assertThat(results.getTotalElements()).isEqualTo(1);
    }

    @Test
    public void findAllNotActive() {

        List<Account> accounts = Collections.singletonList(new Account());

        when(accountRepository.findAll(any(FiltersSpecification.class), any(Pageable.class)))
            .thenReturn(new PageImpl<Account>(accounts, null, accounts.size()));

        Page<Account> results = accountService.findAllAccountsByFilter(false, null, null, null,"",
                mock(Pageable.class), null, null, null, null, null, null);

        assertThat(results.getTotalElements()).isEqualTo(1);
    }

    @Test
    public void findAllBadFilter() {

        List<Account> accounts = Collections.singletonList(new Account());

        when(accountRepository.findAll(any(FiltersSpecification.class), any(Pageable.class)))
            .thenReturn(new PageImpl<Account>(accounts, null, accounts.size()));

        Page<Account> results = accountService.findAllAccountsByFilter(true, null, null, null,"",
                mock(Pageable.class), null, null, null, null, null, null); // invalid filter

        assertThat(results.getTotalElements()).isEqualTo(1);
    }

}
