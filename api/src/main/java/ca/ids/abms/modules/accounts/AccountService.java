package ca.ids.abms.modules.accounts;

import ca.ids.abms.config.db.Filter;
import ca.ids.abms.config.db.FiltersSpecification;
import ca.ids.abms.config.db.JoinFilter;
import ca.ids.abms.config.error.*;
import ca.ids.abms.modules.accounts.enumerate.WhitelistState;
import ca.ids.abms.modules.billings.BillingLedgerRepository;
import ca.ids.abms.modules.exemptions.account.AccountExemption;
import ca.ids.abms.modules.exemptions.account.AccountExemptionRepository;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.flightmovements.FlightMovementRepository;
import ca.ids.abms.modules.flightmovements.enumerate.FlightmovementCategoryNationality;
import ca.ids.abms.modules.notifications.NotificationEventType;
import ca.ids.abms.modules.notifications.NotificationEventTypeRepository;
import ca.ids.abms.modules.selfcareportal.inactivityexpirynotices.SelfCarePortalInactivityExpiryNotice;
import ca.ids.abms.modules.selfcareportal.inactivityexpirynotices.SelfCarePortalInactivityExpiryNoticesService;
import ca.ids.abms.modules.selfcareportal.inactivityexpirynotices.enumerate.NoticeType;
import ca.ids.abms.modules.selfcareportal.querysubmission.QuerySubmissionService;
import ca.ids.abms.modules.system.SystemConfiguration;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;
import ca.ids.abms.modules.transactions.Transaction;
import ca.ids.abms.modules.users.User;
import ca.ids.abms.modules.util.models.ModelUtils;
import ca.ids.abms.modules.utilities.flights.FlightUtility;
import ca.ids.abms.util.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@SuppressWarnings("WeakerAccess")
public class AccountService {

    private static final Logger LOG = LoggerFactory.getLogger(AccountService.class);

    private final AccountRepository accountRepository;
    private final AccountExemptionRepository accountExemptionRepository;
    private final FlightMovementRepository flightMovementRepository;
    private final BillingLedgerRepository billingLedgerRepository;
    private final AccountEventMapRepository accountEventMapRepository;
    private final NotificationEventTypeRepository notificationEventTypeRepository;
    private final SelfCarePortalInactivityExpiryNoticesService selfCarePortalInactivityExpiryNoticesService;
    private final QuerySubmissionService querySubmissionService;

    private static final String KEY_ACTIVE = "active";
    private static final String KEY_CASH_ACCOUNT = "cashAccount";
    private static final String KEY_CREDIT_LIMIT = "creditLimit";
    private static final String KEY_AMOUNT_OWING = "amountOwing";
    private static final String KEY_ALIAS = "alias";
    private static final String KEY_EXTERNAL_ACCOUNTING_SYSTEM_IDENTIFIER = "externalAccountingSystemIdentifier";
    private static final String KEY_BILLING_LEDGERS = "billingLedgers";
    private static final String KEY_FLIGHT_SCHEDULES = "flightSchedules";
    private static final String KEY_ACCOUNT = "account";
    private static final String KEY_ACCOUNT_USERS = "accountUsers";
    private static final String KEY_BLACK_LISTED = "blackListedIndicator";

    @Autowired
    private SystemConfigurationService systemConfigurationService;

    @SuppressWarnings("squid:S00107") //Methods should not have too many parameters
    public AccountService(final AccountRepository accountRepository,
                          final AccountExemptionRepository accountExemptionRepository,
                          final FlightMovementRepository flightMovementRepository,
                          final BillingLedgerRepository billingLedgerRepository,
                          final AccountEventMapRepository accountEventMapRepository,
                          final NotificationEventTypeRepository notificationEventTypeRepository,
                          final SelfCarePortalInactivityExpiryNoticesService selfCarePortalInactivityExpiryNoticesService,
                          @Lazy final QuerySubmissionService querySubmissionService) {
        this.accountRepository = accountRepository;
        this.accountExemptionRepository = accountExemptionRepository;
        this.flightMovementRepository = flightMovementRepository;
        this.billingLedgerRepository = billingLedgerRepository;
        this.accountEventMapRepository = accountEventMapRepository;
        this.notificationEventTypeRepository = notificationEventTypeRepository;
        this.selfCarePortalInactivityExpiryNoticesService = selfCarePortalInactivityExpiryNoticesService;
        this.querySubmissionService = querySubmissionService;
    }

    public SystemConfigurationService getSystemConfigurationService() {
        return systemConfigurationService;
    }

    public void setSystemConfigurationService(SystemConfigurationService systemConfigurationService) {
        this.systemConfigurationService = systemConfigurationService;
    }

    @Transactional(readOnly = true)
    public Account findAccountByIdwithBillingLedgerAndAircraft (final Integer userId) {
        LOG.debug("Request to get Accounts with BillingLedger and AircraftRegistration for user id={}", userId);
        return accountRepository.findAccountByIdwithBillingLedgerAndAircraft(userId);
    }

    public void delete(Integer id) {
        LOG.debug("Request to delete Account : {}", id);
        try {
            final Account account = accountRepository.getOne(id);
            if (account != null) {
                final Set<Transaction> transactions = account.getTransactions();
                if (CollectionUtils.isNotEmpty(transactions)) {
                    ErrorVariables errorVariables = new ErrorVariables();

                    errorVariables.addEntry("name", account.getName());
                    errorVariables.addEntry("size", Integer.toString(transactions.size()));

                    final ErrorDTO errorDto = new ErrorDTO.Builder()
                        .setErrorMessage("Some transactions have been found for the specified account.")
                        .appendDetails("The account {{name}} has {{size}} transactions and cannot be deleted")
                        .setDetailMessageVariables(errorVariables)
                        .build();

                    throw ExceptionFactory.getInvalidDataException(errorDto);
                } else {
                    validateDelete(id);
                    deleteAccountEvents(id);
                    accountRepository.delete(id);
                }
            }
        } catch (RuntimeException e) {
            throw ExceptionFactory.persistenceDataManagement(e,ErrorConstants.ERR_DELETE_NO_LONGER_EXISTS);
        }
    }

    private void validateDelete(Integer id) {
        LOG.debug("Validate delete : {}", id);

        AccountExemption accountExemptionExists = accountExemptionRepository.findOneByAccountId(id);
        List<FlightMovement> flightMovementExists = flightMovementRepository.findAllByAccount(id);
        Integer billingLedgerExists = billingLedgerRepository.howManyInvoicesByAccountId(id);

        if (accountExemptionExists != null) {
            LOG.debug("Bad request: The Account is used in Exempt Accounts: {}", accountExemptionExists);
            throw new CustomParametrizedException(ErrorConstants.ERR_DEPENDENCY_VIOLATION,
                new Exception("The Account cannot be deleted, it is used in Exempt Accounts"));
        }
        if (!flightMovementExists.isEmpty()) {
            LOG.debug("Bad request: The Account is used in Flight Movements: {}", flightMovementExists);
            throw new CustomParametrizedException(ErrorConstants.ERR_DEPENDENCY_VIOLATION,
                new Exception("The Account cannot be deleted, it is used in Flight Movements"));
        }
        if (billingLedgerExists > 0) {
            LOG.debug("Bad request: The Account is used in Invoices: {}", billingLedgerExists);
            throw new CustomParametrizedException(ErrorConstants.ERR_DEPENDENCY_VIOLATION,
                new Exception("The Account cannot be deleted, it is used in Invoices"));
        }
    }

    @Transactional(readOnly = true)
    public Collection<Account> findAll() {
        LOG.debug("Request to get all Accounts");

        return accountRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Collection<Account> findAllExcludingCash() {
        LOG.debug("Request to get all Accounts excluding cash accounts");

        return accountRepository.findAllExcludingCash();
    }

    @Transactional(readOnly = true)
    public List<Account> findByAccountType (final String nameAccountType) {
        LOG.debug("Request to get Accounts for name of AccountType {}", nameAccountType);
        List<Account> accounts = accountRepository.findByAccountType (nameAccountType);
        return accounts;
    }

    @Transactional(readOnly = true)
    public List<Account> findByUserId (final Integer userId) {
        LOG.debug("Request to get Accounts for user id={}", userId);
        return accountRepository.findByUserId (userId);
    }

    @Transactional(readOnly = true)
    public Collection<Account> findActiveAviationCreditMinimalReturn(Integer accountType) {
        LOG.debug("Request to get name and id of active aviation credit accounts");
        if(accountType != null) {
            return accountRepository.findActiveCreditMinimalReturnAviationByType(accountType);
        } else {
            return accountRepository.findAllActiveCreditMinimalReturnAviation();
        }
    }

    @Transactional(readOnly = true)
    public Collection<Account> findAllOrActiveCashOrCredit(boolean onlyActive, boolean onlyCredit, boolean onlyCash) {
        String accounts = String.format("%s %s%s", onlyActive ? KEY_ACTIVE : "all", onlyCredit ? "credit" : "", onlyCash ? "cash" : "");
        LOG.debug("Request to get {} Accounts ordered by name", accounts);

        final FiltersSpecification.Builder filterBuilder = new FiltersSpecification.Builder();
        if (onlyActive) {
            filterBuilder.restrictOn(Filter.equals(KEY_ACTIVE, true));
        }

        if (onlyCredit && !onlyCash) {
            filterBuilder.restrictOn(Filter.equals(KEY_CASH_ACCOUNT, false));
        }

        if (onlyCash && !onlyCredit) {
            filterBuilder.restrictOn(Filter.equals(KEY_CASH_ACCOUNT, true));
        }

        return accountRepository.findAll(filterBuilder.build(), new Sort("name"));
    }

    @Transactional(readOnly = true)
    public List<Account> findAccountsWithFlightSchedules(Integer userId) {
        LOG.debug("Attempting to find accounts with flight schedules.");

        // build filter for accounts with filter schedules only
        final FiltersSpecification.Builder filterBuilder = new FiltersSpecification.Builder();
        filterBuilder.restrictOn(JoinFilter.isNotNullDistinct(KEY_FLIGHT_SCHEDULES, KEY_ACCOUNT));

        if (userId != null) {
            filterBuilder.restrictOn(JoinFilter.equal(KEY_ACCOUNT_USERS, "id", userId));
        }

        // find all accounts with flight schedules sorted by account name
        return accountRepository.findAll(filterBuilder.build(), new Sort("name"));
    }

    @Transactional(readOnly = true)
    public Collection<Account> findAllSelfCareMinimalReturn(final Integer userId, final boolean onlyActive) {

        // filter accounts by self care users or self care user id if supplied
        final FiltersSpecification.Builder filterBuilder = new FiltersSpecification.Builder();
        if (userId == null) {
            LOG.debug("Request to get name and id of all self care accounts");
            filterBuilder.restrictOn(JoinFilter.isNotNullDistinct(KEY_ACCOUNT_USERS, "id"));
        } else {
            LOG.debug("Request to get name and id of self care accounts for user id {}", userId);
            filterBuilder.restrictOn(JoinFilter.equal(KEY_ACCOUNT_USERS, "id", userId));
        }

        if (onlyActive) {
            filterBuilder.restrictOn(Filter.equals(KEY_ACTIVE, true));
        }

        // find all account with self care filter sorted by account name
        return accountRepository.findAll(filterBuilder.build(), new Sort("name"));
    }

    @Transactional(readOnly = true)
    public Collection<Account> findAllSelfCareWhitelistAccounts(final Integer userId) {

        // filter accounts by self care users or self care user id if supplied
        final FiltersSpecification.Builder filterBuilder = new FiltersSpecification.Builder();
        if (userId == null) {
            LOG.debug("Request to get name and id of all self care accounts");
            filterBuilder.restrictOn(JoinFilter.isNotNullDistinct(KEY_ACCOUNT_USERS, "id"));
        } else {
            LOG.debug("Request to get name and id of self care accounts for user id {}", userId);
            filterBuilder.restrictOn(JoinFilter.equal(KEY_ACCOUNT_USERS, "id", userId));
        }

        filterBuilder.restrictOn(Filter.equals(KEY_CASH_ACCOUNT, true));
        filterBuilder.restrictOn(Filter.equals(KEY_BLACK_LISTED, false));

        // find all account with self care filter sorted by account name
        return accountRepository.findAll(filterBuilder.build(), new Sort("name"));
    }

    @Transactional(readOnly = true)
    public Account getOne(Integer id) {
        LOG.debug("Request to get Account by id: {}", id);
        return accountRepository.getOne(id);
    }

    public String getAccountNameById(Integer id) {
        return accountRepository.getAccountNameById(id);
    }

    private void validateCreditLimit(Double creditLimit, boolean cashAccount) {
        LOG.debug("Validate credit limit:{} ", creditLimit);

        if (cashAccount) {
            if (creditLimit != 0) {
                throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION,
                    new Exception("Credit limit for cash accounts should be 0"));
            }
        } else {
            double max = Double.MAX_VALUE;
            double min= Double.MIN_VALUE;
            SystemConfiguration oMax = systemConfigurationService.getOneByItemName(SystemConfigurationItemName.DFLT_ACCOUNT_MAX_CREDIT_LIMIT);
            SystemConfiguration oMin = systemConfigurationService.getOneByItemName(SystemConfigurationItemName.DFLT_ACCOUNT_MIN_CREDIT_LIMIT);
            if (oMax!=null && oMax.getCurrentValue()!=null) {
                max = Double.parseDouble(oMax.getCurrentValue());
            }
            if (oMin!=null && oMin.getCurrentValue()!=null) {
                min = Double.parseDouble(oMin.getCurrentValue());
            }
            if (creditLimit > max) {
                throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION,
                    new Exception(
                        String.format(
                            "Credit limit" +
                            " %.0f " +
                            "exceeds its maximum limit" +
                            " %.0f.", creditLimit, max)));
            }
            else if (creditLimit < min) {
                throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION,
                    new Exception(
                        String.format(
                            "Credit limit" +
                            " %.0f " +
                            "exceeds its minimum limit" +
                            " %.0f.", creditLimit, min)));
            }
        }
    }

    // Make sure account/users relationships don't exceed limits in system configuration
    private void validateUserLimits (final Account account) {
        if (account.getAccountUsers() != null && !account.getAccountUsers().isEmpty()) {
        	// Check max users per account
            final Integer maxUsersPerAccount = systemConfigurationService.getIntOrZero(SystemConfigurationItemName.MAX_USERS_PER_ACCOUNT);
        	if (account.getAccountUsers().size() > maxUsersPerAccount) {
	            throw new IllegalArgumentException(new Exception (
	            		String.format ("An account cannot have more than %d user(s).", maxUsersPerAccount)));
        	}

            // Check max accounts per user for each user
            final Integer maxAccountsPerUser = systemConfigurationService.getIntOrZero(SystemConfigurationItemName.MAX_ACCOUNTS_PER_USER);
            for (final User u: account.getAccountUsers()) {
        		if (u != null) {
        			final List <Account> accountList = this.findByUserId(u.getId());
        			if (accountList != null) {
        				// This user is always associated with the account being saved, so the count has to be at least 1.
        				int countOfUserAccounts = 1;
        				// update: add all other accounts that are currently associated with the same user
        				if (account.getId() != null) {
        					countOfUserAccounts += accountList.stream()
        							.filter(x->!Objects.equals(x.getId(), account.getId()))
        							.collect(Collectors.counting()).intValue();
        				}
        				// insert: add all accounts that are currently associated with the same user
        				else {
        					countOfUserAccounts += accountList.size();
        				}
        				// we are adding a user to this account, but that user is already related to too many accounts
        				if (countOfUserAccounts > maxAccountsPerUser) {
        					throw new IllegalArgumentException (new Exception (
        							String.format ("User '%s' exceeds the configured maximum of %d associated account(s)", u.getName(), maxAccountsPerUser)));
        				}
        			} // accountList != null
        		} // u != null
        	} // for
        } // account.getAccountUsers() != null
    }

    private void validateNewAccount(Account account) {
        validateCreditLimit(account.getCreditLimit(), account.getCashAccount());
        validateUserLimits (account);
        setIndicatorFalseIfOverrideTrue(account);
        validateName(account);
        validateWhitelistingFields(account);
    }





    private void validateAccountTypeDiscount(Account account) {
    Double accountTypeDiscount = account.getAccountTypeDiscount();
    if (accountTypeDiscount ==null ) {
    return ;
    }

    if (accountTypeDiscount <0 || accountTypeDiscount >100 ) {
    throw new IllegalArgumentException("the AccountTypeDiscount shall have a value between 0 and 100 ");
    }
    }





    private void validateWhitelistingFields(Account account) {
        if (account.getWhitelistLastActivityDateTime() == null) {
            account.setWhitelistLastActivityDateTime(LocalDateTime.now());
        }

        if (account.getWhitelistState() == null) {
            account.setWhitelistState(WhitelistState.ACTIVE);
        }

        if (account.getWhitelistExpiryNoticeSentFlag() == null) {
            account.setWhitelistExpiryNoticeSentFlag(Boolean.FALSE);
        }

        if (account.getWhitelistInactivityNoticeSentFlag() == null) {
            account.setWhitelistInactivityNoticeSentFlag(Boolean.FALSE);
        }
    }

    private void validateName(Account account) {
        List<String> name;
        String accountName = account.getName().replaceAll("[^a-zA-Z0-9]", "");

        if (account.getId() != null) {
            name = accountRepository.findAllNames(account.getId());
        } else {
            name = accountRepository.findAllNames();
        }

        name.forEach(a -> {
            if (a.replaceAll("[^a-zA-Z0-9]", "").equalsIgnoreCase(accountName)) {
                LOG.debug("Bad request: An Account with the name {} already exists", account.getName());
                final String details = "An account with this name already exists";
                throw new CustomParametrizedException(ErrorConstants.ERR_UNIQUENESS_VIOLATION, new Exception(details), "name");
            }
        });
    }

    public Account save(Account account) {
        LOG.debug("Request to save Account : {}", account);

        validateNewAccount(account);
        return accountRepository.save(account);
    }

    public Account update(Integer id, Account account) {
        LOG.debug("Request to update Account : {}", account);

        if (account.getCreditLimit() != null) {
            validateCreditLimit(account.getCreditLimit(), account.getCashAccount());
        }

        validateName(account);
        validateUserLimits(account);
        validateAccountTypeDiscount(account);

        //update account last activity date/time for Whitelisting
        account.setWhitelistLastActivityDateTime(LocalDateTime.now());

        Account updated = null;
        try {
            final Account existingAccount = accountRepository.getOne(id);
            if (existingAccount != null && existingAccount.getId() != null) {
                ModelUtils.mergeExcept(account, existingAccount, KEY_BLACK_LISTED, "version", "createdAt", "createdBy", "updatedAt", "updatedBy");
                this.setIndicatorFalseIfOverrideTrue(existingAccount);
                updated = accountRepository.save(existingAccount);
            }
        } catch (RuntimeException e) {
            throw ExceptionFactory.persistenceDataManagement(e,ErrorConstants.ERR_UPDATE_NO_LONGER_EXISTS);
        }
        return updated;
    }

    @Transactional(readOnly = true)
    public Account findAccountByIcaoCode(String icaoCode){
        LOG.debug("Request Account by icaoCode: {}", icaoCode);
        Account account=null;
        if(icaoCode!= null && !icaoCode.isEmpty()){
            return accountRepository.findByIcaoCode(icaoCode);
        }
        return  account;
    }

    @Transactional(readOnly = true)
    public Account findAccountByIcaoOrIataCode(String code){
        LOG.debug("Request Account by icao or iata code: {}", code);
        Account account=null;
        if(code!= null && !code.isEmpty()){
            boolean nonStandardFlightId = systemConfigurationService.getBoolean(SystemConfigurationItemName.ALLOW_NON_STANDARD_FLIGHT_ID);

            // code can represent either ICAO or IATA code

            // ICAO code can be mapped only:
            // - nonStandardFlightId is FALSE - flightId has 3 characters following digits only
            // - nonStandardFlightId is TRUE - flightId has 3 characters following one digit (we don't care what is after first digit)

            String icaoCode = FlightUtility.getICAOCodePrefixBygetFlightId(code, nonStandardFlightId);
            // IATA code must only be 2 characters
            String iataCode = code.length() > 2 ? code.substring(0, 2) : code;

            List<Account> accounts = accountRepository.findByIcaoOrIataCode(icaoCode, iataCode);
            if (accounts != null && !accounts.isEmpty()) {
            	account = accounts.get(0);
            }
        }
        return  account;
    }

    @Transactional(readOnly = true)
    public Account findAccountByOprIdentifier(String oprIdentifier){
        LOG.debug("Request Account by oprIdentiier: {}", oprIdentifier);
        Account account=null;
        if(oprIdentifier!=null && !oprIdentifier.isEmpty()) {
            account=accountRepository.findByOprIdentifier(oprIdentifier);
        }
        return  account;
    }

    @Transactional(readOnly = true)
    public Account findAccountByNameOrAlias(String name){
        LOG.debug("Request Account by name: {}", name);
        Account account=null;
        if(name!=null && !name.isEmpty()) {
            account = accountRepository.findByNameOrAlias(name);
        }
        return account;
    }

    @Transactional(readOnly = true)
    public Account findAccountByNameOrAliasOrOperator(String name){
        LOG.debug("Request Account by name: {}", name);
        Account account=null;
        if(name!=null && !name.isEmpty()) {
            account = accountRepository.findByNameOrAliasOrOperator(name);
        }
        return account;
    }

    // if `blacklisted override` is true, we ensure that the `blacklisted indicator` is false
    private Account setIndicatorFalseIfOverrideTrue(Account account) {

        Boolean override = account.getBlackListedOverride();

        if (override != null && override.equals(Boolean.TRUE)) {
            account.setBlackListedIndicator(Boolean.FALSE);
        }

        return account;
    }

    @Scheduled(fixedRate=3600000) // 1 hour
     protected void recalculateBlackListedIndicatorForAllAccounts() {
        LOG.debug("Recalculating blacklisted indicator for all accounts");
        List<Account>allAccounts = accountRepository.findAll();
        List<Account>accountsOverdue = accountRepository.findAllWithOverdueInvoices();
        for (Account account: allAccounts) {
            boolean isChanged = false;
            if (accountsOverdue.contains(account)) {
                // set blacklisted
                if (!account.getBlackListedOverride() && !account.getBlackListedIndicator()) {
                    account.setBlackListedIndicator(Boolean.TRUE);
                    isChanged = true;
                }
            } else {
                // clear blacklisted
                if (account.getBlackListedIndicator()) {
                    account.setBlackListedIndicator(Boolean.FALSE);
                    isChanged = true;
                }
            }
            if(isChanged){
                accountRepository.save(account);
                LOG.debug("Setting blacklisted indicator to {} for account {}",
                    account.getBlackListedIndicator(), account.getName());
            }
        }
    }

    @Transactional(readOnly = true)
    @SuppressWarnings("squid:S00107")
    public Page<Account> findAllAccountsByFilter(Boolean aFilter, Boolean aCash, String aInvoices, Boolean aCredit,
                                                 String textSearch, Pageable aPageable, Boolean aSelfCareAccounts,
                                                 String nationality, String alias, String externalAccountingSystemIdentifier,
                                                 Boolean aFlightSchedules, Integer accountType) {

        final FiltersSpecification.Builder filterBuilder = new FiltersSpecification.Builder().lookFor(textSearch);

        if (aFilter != null) {
            if (aFilter) {
                filterBuilder.restrictOn(Filter.isTrue(KEY_ACTIVE));
            }
            else {
                filterBuilder.restrictOn(Filter.isFalse(KEY_ACTIVE));
            }
        }
        if (aCash != null) {
            if (aCash) {
                filterBuilder.restrictOn(Filter.isTrue(KEY_CASH_ACCOUNT));
            }
            else {
                filterBuilder.restrictOn(Filter.isFalse(KEY_CASH_ACCOUNT));
            }
        }
        if (aSelfCareAccounts != null && aSelfCareAccounts) {
            filterBuilder.restrictOn(JoinFilter.isNotNullDistinct(KEY_ACCOUNT_USERS, "id"));
        }
        if (aCredit != null && aCredit) {
            filterBuilder.restrictOn(Filter.greaterThan(KEY_CREDIT_LIMIT, 0));
        }
        if (nationality != null) {
            final FlightmovementCategoryNationality flightmovementCategoryNationality = FlightmovementCategoryNationality.forValue(nationality);
            filterBuilder.restrictOn(Filter.equals("nationality", flightmovementCategoryNationality));

        }

        if(accountType  != null) {
        	filterBuilder.restrictOn(Filter.equals("accountType", accountType));
        }

        // apply nullable filters, null will be skipped, empty will be treated as ISNULL,
        // and value will be treated as EQUAL
        applyNullableFilter(filterBuilder, KEY_ALIAS, alias);
        applyNullableFilter(filterBuilder, KEY_EXTERNAL_ACCOUNTING_SYSTEM_IDENTIFIER, externalAccountingSystemIdentifier);

        if (aInvoices != null && aInvoices.equals("outstanding")) {
            filterBuilder.restrictOn(JoinFilter.greaterThanDistinct(KEY_BILLING_LEDGERS, KEY_AMOUNT_OWING, 0));
        }
        if (aInvoices != null && aInvoices.equals("overdue")) {
            filterBuilder.restrictOn(JoinFilter.greaterThanDistinct(KEY_BILLING_LEDGERS, KEY_AMOUNT_OWING, 0));
            filterBuilder.restrictOn(JoinFilter.lessThanDistinct(KEY_BILLING_LEDGERS, "paymentDueDate", LocalDateTime.now()));
        }
        if (aFlightSchedules != null && aFlightSchedules) {
            filterBuilder.restrictOn(JoinFilter.isNotNullDistinct(KEY_FLIGHT_SCHEDULES, KEY_ACCOUNT));
        }
        LOG.debug("Attempting to find accounts by filters. Search: {}, Active: {}, Cash-Account: {}, Invoices: {}, Credit: {}",
            textSearch, aFilter, aCash, aInvoices, aCredit);
        return accountRepository.findAll(filterBuilder.build(), aPageable);

    }

    public List<Object> getCustomerNotifications() {
        return accountRepository.getCustomerNotifications();
    }

    public Account updateAccountEvents(Integer id, Collection<AccountEventMapViewModel> events){
        Account existingAccount = accountRepository.findById(id);
        List<AccountEventMap> accountEventsToAdd = new ArrayList<>();

        for (AccountEventMapViewModel event: events) {
            AccountEventMap existingEvent = accountEventMapRepository.findByAccountAndNotification(id, event.getNotificationEventType());

            if (existingEvent != null) {
                AccountEventMap eventToAdd = mapAccountEvent(existingAccount, notificationEventTypeRepository.findById(event.getNotificationEventType()), event);
                ModelUtils.merge(eventToAdd, existingEvent);
                accountEventsToAdd.add(existingEvent);
            }
            else {
                accountEventsToAdd.add(mapAccountEvent(existingAccount, notificationEventTypeRepository.findById(event.getNotificationEventType()), event));
            }
        }

        if (LOG.isDebugEnabled()) {
            for (AccountEventMapViewModel event: events) {
                LOG.debug("@Account: {} - Events to add: {}, notification for email: {}, notification for SMS: {} ", id, event.getNotificationEventType(), event.getNotificationEmail(), event.getNotificationSms());
            }
        }

        accountEventMapRepository.save(accountEventsToAdd);
        accountEventMapRepository.flush();
        existingAccount.setListOfEventsAccountNotified(accountEventsToAdd);
        accountRepository.refresh(existingAccount);
        return accountRepository.findOne(id);
    }

    public void deleteAccountEvents (Integer id) {
        List<AccountEventMap> accountEventsToDelete = accountEventMapRepository.findByAccountId(id);
        accountEventMapRepository.delete(accountEventsToDelete);
    }


    /**
     * Returns the top few account names that are currently using the specified currency
     */
    @Transactional (readOnly = true)
    public List <String> getTopNamesByInvoiceCurrency (final int currencyId, final int maxRows) {
        return accountRepository.getTopNamesByInvoiceCurrency (currencyId, maxRows);
    }

    /**
     * Returns the total number of accounts that are currently using the specified currency
     */
    @Transactional (readOnly = true)
    public int countByCurrency (final int currencyId) {
        return accountRepository.countByCurrency (currencyId);
    }

    @Transactional(readOnly = true)
    public Collection<Account> findAllSelfCareUserAccounts() {
        LOG.debug("Request to get all Accounts for all self-care users");

        return accountRepository.findAllSelfCareUserAccounts();
    }

    private AccountEventMap mapAccountEvent(Account account, NotificationEventType notification, AccountEventMapViewModel item) {
        final AccountEventMap accountEventMap = new AccountEventMap();
        accountEventMap.setAccount(account);
        accountEventMap.setNotificationEventType(notification);
        accountEventMap.setNotificationEmail(item.getNotificationEmail());
        accountEventMap.setNotificationSms(item.getNotificationSms());
        return accountEventMap;
    }

    @Transactional(readOnly = true)
    @SuppressWarnings("squid:S00107")
    public Page<Account> findSCAccountsByFilterUser(Boolean aFilter, Boolean aCash, String aInvoices,
                                                    Boolean aCredit, String textSearch, Pageable aPageable,
                                                    Integer userId, Boolean aFlightSchedules, String alias, String externalAccountingSystemIdentifier) {

        final FiltersSpecification.Builder filterBuilder = getSCAccountsByFilterUser(aFilter, aCash, aInvoices, aCredit, textSearch, userId, aFlightSchedules, alias, externalAccountingSystemIdentifier);

        return accountRepository.findAll(filterBuilder.build(), aPageable);
    }

    @Transactional(readOnly = true)
    public List<Account> findSCAccountsByFilterUser(Boolean filter, Boolean cash, String invoices, Boolean credit, String textSearch, Integer userId) {

        final FiltersSpecification.Builder filterBuilder = getSCAccountsByFilterUser(filter, cash, invoices, credit, textSearch, userId, null, null, null);

        return accountRepository.findAll(filterBuilder.build());
    }

    @SuppressWarnings("squid:S00107")
    private FiltersSpecification.Builder getSCAccountsByFilterUser(Boolean aFilter, Boolean aCash, String aInvoices,
                                                                   Boolean aCredit, String textSearch, Integer userId,
                                                                   Boolean aFlightSchedules,
                                                                   String alias, String externalAccountingSystemIdentifier) {

        final FiltersSpecification.Builder filterBuilder = new FiltersSpecification.Builder().lookFor(textSearch);

        if (aFilter != null) {
            if (aFilter) {
                filterBuilder.restrictOn(Filter.isTrue(KEY_ACTIVE));
            } else {
                filterBuilder.restrictOn(Filter.isFalse(KEY_ACTIVE));
            }
        }
        if (aCash != null) {
            if (aCash) {
                filterBuilder.restrictOn(Filter.isTrue(KEY_CASH_ACCOUNT));
            } else {
                filterBuilder.restrictOn(Filter.isFalse(KEY_CASH_ACCOUNT));
            }
        }

        filterBuilder.restrictOn(JoinFilter.equal(KEY_ACCOUNT_USERS, "id", userId));

        if (aCredit != null && aCredit) {
            filterBuilder.restrictOn(Filter.greaterThan(KEY_CREDIT_LIMIT, 0));
        }
        if (aInvoices != null && aInvoices.equals("outstanding")) {
            filterBuilder.restrictOn(JoinFilter.greaterThanDistinct(KEY_BILLING_LEDGERS, KEY_AMOUNT_OWING, 0));
        }
        if (aInvoices != null && aInvoices.equals("overdue")) {
            filterBuilder.restrictOn(JoinFilter.greaterThanDistinct(KEY_BILLING_LEDGERS, KEY_AMOUNT_OWING, 0));
            filterBuilder.restrictOn(JoinFilter.lessThanDistinct(KEY_BILLING_LEDGERS, "paymentDueDate", LocalDateTime.now()));
        }
        if (aFlightSchedules != null && aFlightSchedules) {
            filterBuilder.restrictOn(JoinFilter.isNotNullDistinct(KEY_FLIGHT_SCHEDULES, KEY_ACCOUNT));
        }

        // apply nullable filters, null will be skipped, empty will be treated as ISNULL,
        // and value will be treated as EQUAL
        applyNullableFilter(filterBuilder, KEY_ALIAS, alias);
        applyNullableFilter(filterBuilder, KEY_EXTERNAL_ACCOUNTING_SYSTEM_IDENTIFIER, externalAccountingSystemIdentifier);

        LOG.debug("Attempting to find accounts by filters. Search: {}, Active: {}, Cash-Account: {}, Invoices: {}, Credit: {}, Flight Schedules: {}",
            textSearch, aFilter, aCash, aInvoices, aCredit, aFlightSchedules);

        return filterBuilder;
    }

    /**
     * Skip filter if field value is null, else apply condition as IS NULL if empty or EQUALS if not.
     */
    private void applyNullableFilter(
        final FiltersSpecification.Builder filterBuilder, final String fieldName, final String fieldValue
    ) {
        if (filterBuilder == null || fieldName == null)
            throw new IllegalArgumentException("filterBuilder and fieldName arguments cannot be null");

        // skip if field value null
        if (fieldValue == null)
            return;

        // define as null if field value is empty
        // else look up for exact match
        if (fieldValue.isEmpty())
            filterBuilder.restrictOn(Filter.isNull(fieldName));
        else
            filterBuilder.restrictOn(Filter.equals(fieldName, fieldValue));
    }

    public long countAll() {
        return accountRepository.count();
    }

    public long countAllBySelfCareUser(int id) {
        return accountRepository.countAllBySelfCareUser(id);
    }

    /**
     * Account activity updated by following:
     * -	payments being made;
     * -    flights occurring; and
     * -	users logging in.
     * @param account Account
     */
    public void updateAccountLastActivityDateTimeForWhitelisting(Account account) {
        if (account == null) {
            return;
        }

        if (!account.getCashAccount()) {
            return;
        }

        int accountId = account.getId();

        LOG.debug("Update cash Account id: {}, name: {} with: Whitelist Last Activity Date/Time: {}, Whitelist State: ACTIVE, Whitelist Inactivity and Expiry Notice Flags: FALSE",
            account.getId(), account.getName(), LocalDateTime.now());

        accountRepository.updateWhitelistLastActivityDateTime(accountId, LocalDateTime.now());
        accountRepository.updateWhitelistState(accountId, WhitelistState.ACTIVE);
        accountRepository.updateWhitelistInactivityNoticeSentFlag(accountId, false);
        accountRepository.updateWhitelistExpiryNoticeSentFlag(accountId, false);
    }

    /**
     * An account expiry mechanism is provided which allows accounts to first become inactive and then expire.
     * An account’s inactivity date is calculated as the last activity time + a specified interval,
     * and when an account becomes inactive, a warning message is emailed to the operator.
     * An account’s expiry date is calculated as the inactivity date plus a second interval and when an account
     * becomes expired, an account expiry message is emailed to the operator.
     */
    @Scheduled(fixedRate=3600000) // 1 hour
    public void checkInactiveOrExpiredAccounts() {
        LOG.debug("Start scheduled task for checking Inactive or Expired Self-Care Accounts");
        boolean whitelistingEnabled = systemConfigurationService.getBoolean(SystemConfigurationItemName.WL_ENABLED);

        if (!whitelistingEnabled) {
            LOG.debug("End scheduled task 'checkInactiveOrExpiredAccounts' because Whitelisting is disabled");
            return;
        }

        int inactivityPeriod = systemConfigurationService.getIntOrZero(SystemConfigurationItemName.WL_INACTIVITY_PERIOD);
        int expiryPeriod = systemConfigurationService.getIntOrZero(SystemConfigurationItemName.WL_EXPIRY_PERIOD);

        String inactivityInterval = String.format("%d %s", inactivityPeriod, "month");
        String expiryInterval = String.format("%d %s", inactivityPeriod + expiryPeriod, "month");

        // check inactive accounts
        List<Account> inactiveAccounts = accountRepository.findInactiveCashAccounts(inactivityInterval, expiryInterval);

        if (inactiveAccounts != null && !inactiveAccounts.isEmpty()) {
            sendNoticeForInactiveAccounts(inactiveAccounts);
        } else {
            LOG.debug("There are no Inactive Self-Care Cash Accounts");
        }

        // check expired accounts
        List<Account> expiredAccounts = accountRepository.findExpiredCashAccounts(expiryInterval);

        if (expiredAccounts != null && !expiredAccounts.isEmpty()) {
            sendNoticeForExpiredAccounts(expiredAccounts);
        } else {
            LOG.debug("There are no Expired Self-Care cash Accounts");
        }

    }

    private void sendNoticeForInactiveAccounts(final List<Account> inactiveAccounts) {
        String messageText = systemConfigurationService.getCurrentValue(SystemConfigurationItemName.WL_INACTIVITY_NOTICE_TEXT);
        boolean updateWhitelistState = false;
        if (StringUtils.isNotBlank(messageText)) {
            updateWhitelistState = true;
            checkAndSendInactiveOrExpiredNotices(inactiveAccounts, WhitelistState.INACTIVE, NoticeType.INACTIVE, messageText);
        } else {
            LOG.debug("Can't send Notice For Inactive Self-Cate cash Accounts because 'Inactivity notice text' is not set");
        }

        if (!updateWhitelistState) {
            // update account's Whitelist State
            for (Account account : inactiveAccounts) {
                if (!account.getWhitelistState().equals(WhitelistState.INACTIVE)) {
                    LOG.debug("Set Whitelist State to INACTIVE for the Account with id: {}, name: {}", account.getId(), account.getName());
                    accountRepository.updateWhitelistState(account.getId(), WhitelistState.INACTIVE);
                }
            }
        }
    }

    private void sendNoticeForExpiredAccounts(final List<Account> expiredAccounts) {
        String messageText = systemConfigurationService.getCurrentValue(SystemConfigurationItemName.WL_EXPIRY_NOTICE_TEXT);
        boolean updateWhitelistState = false;
        if (StringUtils.isNotBlank(messageText)) {
            updateWhitelistState = true;
            checkAndSendInactiveOrExpiredNotices(expiredAccounts, WhitelistState.EXPIRED, NoticeType.EXPIRED, messageText);
        } else {
            LOG.debug("Can't send Notice For Expired Self-Cate cash Accounts because 'Expiry notice text' is not set");
        }

        if (!updateWhitelistState) {
            // update account's Whitelist State
            for (Account account : expiredAccounts) {
                if (!account.getWhitelistState().equals(WhitelistState.EXPIRED)) {
                    LOG.debug("Set Whitelist State to EXPIRED for the Account with id: {}, name: {}", account.getId(), account.getName());
                    accountRepository.updateWhitelistState(account.getId(), WhitelistState.EXPIRED);
                }
            }
        }
    }

    private void checkAndSendInactiveOrExpiredNotices(final List<Account> accounts,
                                                      final WhitelistState whitelistState,
                                                      final NoticeType noticeType,
                                                      final String messageText) {
        for (Account account: accounts) {
            int accountId = account.getId();
            String accountName = account.getName();

            // check account's whitelist state and notifications flag
            if (!account.getWhitelistState().equals(whitelistState)) {
                LOG.debug("Set Whitelist State to {} for the Account with id: {}, name: {}", whitelistState.getValue(), accountId, accountName);
                accountRepository.updateWhitelistState(accountId, whitelistState);
            }

            // if notification was sent, we skip this account and continue with the next one
            if ((noticeType.equals(NoticeType.INACTIVE) && account.getWhitelistInactivityNoticeSentFlag())
                || (noticeType.equals(NoticeType.EXPIRED) && account.getWhitelistExpiryNoticeSentFlag())) {
                LOG.debug("Skip {} Account with id: {}, name: {} because notification has been already sent", whitelistState.getValue(), accountId, accountName);
                continue;
            }

            List<User> accountUsers = account.getAccountUsers();
            List<String> userEmails = new ArrayList<>();
            for (User user: accountUsers) {
                userEmails.add(user.getEmail());
            }

            String emailSubject = String.format("Self-Care Portal account '%s' becomes %s", accountName, noticeType.getValue().toLowerCase());
            boolean emailSentSuccessfully = sendEmailNotification(emailSubject, messageText, userEmails, noticeType, account);
            if (emailSentSuccessfully) {
                // create a new record for SelfCarePortalInactivityExpiryNotice
                createNewSelfCarePortalInactivityExpiryNotice(account, messageText, noticeType);

                // update account Inactivity/Expiry Notice Flag
                updateAccountInactivityExpiryNoticeFlag(noticeType, accountId);
            }
        }
    }

    public boolean sendEmailNotification(final String emailSubject,
                                         final String messageText,
                                         final List<String> userEmails,
                                         final NoticeType noticeType,
                                         final Account account) {
        int accountId = account.getId();
        String accountName = account.getName();

        LOG.debug("Try to send email notification that Self-Care cash Account with id: {}, name: {} becomes {}", accountId, accountName, noticeType.getValue());

        boolean emailSentSuccessfully = querySubmissionService.send(emailSubject, messageText, userEmails, false);
        if (!emailSentSuccessfully) {
            LOG.debug("{} notice wasn't sent for the Account: id: {}, name: {} ", noticeType.getValue(), accountId, accountName);
        }
        return emailSentSuccessfully;
    }

    private void updateAccountInactivityExpiryNoticeFlag(final NoticeType noticeType,
                                                         final Integer accountId) {
        if (noticeType.equals(NoticeType.INACTIVE)){
            LOG.debug("Set Whitelist Inactivity Notice Sent Flag to TRUE for the Account with id: {}", accountId);
            accountRepository.updateWhitelistInactivityNoticeSentFlag(accountId, true);
        } else {
            LOG.debug("Set Whitelist Expiry Notice Sent Flag to TRUE for the Account with id: {}", accountId);
            accountRepository.updateWhitelistExpiryNoticeSentFlag(accountId, true);
        }

    }

    private void createNewSelfCarePortalInactivityExpiryNotice(final Account account,
                                                               final String messageText,
                                                               final NoticeType noticeType) {
        LOG.debug("Create a new SelfCarePortalInactivityExpiryNotice for the Account: id: {}, name: {} and Notification Type: {}",
            account.getId(), account.getName(), noticeType.getValue());

        SelfCarePortalInactivityExpiryNotice selfCarePortalInactivityExpiryNotice = new SelfCarePortalInactivityExpiryNotice();
        selfCarePortalInactivityExpiryNotice.setAccount(account);
        selfCarePortalInactivityExpiryNotice.setNoticeType(noticeType);
        selfCarePortalInactivityExpiryNotice.setMessageText(messageText);
        selfCarePortalInactivityExpiryNotice.setDateTime(LocalDateTime.now());
        selfCarePortalInactivityExpiryNoticesService.create(selfCarePortalInactivityExpiryNotice);
    }
}

