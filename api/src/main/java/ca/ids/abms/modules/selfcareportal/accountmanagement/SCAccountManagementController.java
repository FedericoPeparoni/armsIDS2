package ca.ids.abms.modules.selfcareportal.accountmanagement;

import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.config.security.SecurityUtils;
import ca.ids.abms.modules.accounts.*;
import ca.ids.abms.modules.common.controllers.MediaDocumentComponent;
import ca.ids.abms.modules.reports2.common.ReportDocument;
import ca.ids.abms.modules.reports2.common.ReportDocumentCreator;
import ca.ids.abms.modules.selfcareportal.approvalrequests.SelfCarePortalApprovalRequestService;
import ca.ids.abms.modules.selfcareportal.approvalrequests.enumerate.RequestDataset;
import ca.ids.abms.modules.selfcareportal.approvalrequests.enumerate.RequestStatus;
import ca.ids.abms.modules.selfcareportal.approvalrequests.enumerate.RequestType;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;
import ca.ids.abms.modules.users.User;
import ca.ids.abms.modules.users.UserService;
import ca.ids.abms.modules.util.models.PageImplCustom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

@RestController
@RequestMapping("/api/sc-account-management")
@SuppressWarnings({"unused", "squid:S1452"})
public class SCAccountManagementController extends MediaDocumentComponent {

    private static final Logger LOG = LoggerFactory.getLogger(SCAccountManagementController.class);

    private final AccountService accountService;
    private final AccountMapper accountMapper;
    private final UserService userService;
    private final SystemConfigurationService systemConfigurationService;
    private final SelfCarePortalApprovalRequestService selfCarePortalApprovalRequestService;
    private final ReportDocumentCreator reportDocumentCreator;

    private static final String NO_ACCOUNT = "Account not found";

    public SCAccountManagementController(final AccountService accountService,
                                         final AccountMapper accountMapper,
                                         final UserService userService,
                                         final SystemConfigurationService systemConfigurationService,
                                         final SelfCarePortalApprovalRequestService selfCarePortalApprovalRequestService,
                                         final ReportDocumentCreator reportDocumentCreator) {
        this.accountService = accountService;
        this.accountMapper = accountMapper;
        this.userService = userService;
        this.systemConfigurationService = systemConfigurationService;
        this.selfCarePortalApprovalRequestService = selfCarePortalApprovalRequestService;
        this.reportDocumentCreator = reportDocumentCreator;
    }

    @PreAuthorize("hasAnyAuthority('self_care_admin','self_care_access')")
    @PostMapping
    public ResponseEntity<AccountViewModel> createSCAccount(@Valid @RequestBody AccountViewModel accountDto) throws URISyntaxException, IOException {
        LOG.debug("REST request to save Account from self-care portal : {}", accountDto);

        if (accountDto == null || accountDto.getId() != null) {
            LOG.debug("Bad request: Account already exist");
            throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION,
                new Exception("Account already exist"));
        }

        final Account account = accountMapper.toModel(accountDto);
        account.setBlackListedIndicator(Boolean.FALSE);
        account.setAccountUsers(Collections.singletonList(this.getCurrentUser()));
        account.setIsSelfCare(true);
        account.setCashAccount(true);

        AccountViewModel result;

        User us = getCurrentUser();
        boolean needApproval = systemConfigurationService.getBoolean(SystemConfigurationItemName.REQUIRE_ADMIN_APPROVAL_FOR_SC_ACCOUNTS);

        if (needApproval && us.getIsSelfcareUser()) {
            result = accountMapper.toViewModel(selfCarePortalApprovalRequestService.createNewApprovalRequest(account, null, null, Account.class, RequestDataset.ACCOUNT, RequestType.CREATE));
        } else {
            result = accountMapper.toViewModel(accountService.save(account));
        }

        return ResponseEntity.created(new URI("/api/sc-account-management/" + result.getId())).body(result);
    }

    @PreAuthorize("hasAnyAuthority('self_care_admin','self_care_access')")
    @PutMapping(value = "/{id}")
    public ResponseEntity<AccountViewModel> updateSCAccount(@Valid @RequestBody AccountViewModel accountDto, @PathVariable Integer id) {
        LOG.debug("REST request to update self-care Account : {}", accountDto);

        final Account ac = accountMapper.toModel(accountDto);
        User us = getCurrentUser();

        if (ac == null || us == null) {
            LOG.debug(NO_ACCOUNT);
            throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION,
                new Exception(NO_ACCOUNT));
        }

        AccountViewModel result;
        if (!us.getIsSelfcareUser() || ac.containsAccountUser(us)) {
            result = accountMapper.toViewModel(accountService.update(id, ac));
        } else {
            LOG.debug("Bad request: Self-care user doesn't have permissions for this account.Account can't be updated");
            throw new CustomParametrizedException(ErrorConstants.ERR_ACCESS_DENIED,
                new Exception("Self-care user doesn't have permissions for this account.Account can't be updated"));
        }
        return ResponseEntity.ok().body(result);
    }

    @PreAuthorize("hasAnyAuthority('self_care_access')")
    @PutMapping
    public ResponseEntity<AccountViewModel> updateSCAccountRequest(@Valid @RequestBody AccountViewModel accountDto) throws IOException {
        LOG.debug("REST request to update self-care Account that is not approved : {}", accountDto);

        if (accountDto.getScRequestId() == null) {
            LOG.debug("Bad request: there is no Approval Request for this Account");
            throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION,
                new Exception("There is no Approval Request for this Account. Account can't be updated"));
        }

        final Integer requestId = accountDto.getScRequestId();
        final Account ac = accountMapper.toModel(accountDto);
        User us = getCurrentUser();
        if(ac == null || us == null) {
            LOG.debug(NO_ACCOUNT);
            throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION, new Exception(NO_ACCOUNT));
        }

        AccountViewModel result;
        if (!us.getIsSelfcareUser() || ac.containsAccountUser(us)) {
            result = accountMapper.toViewModel(selfCarePortalApprovalRequestService.updateUnprovenApprovalRequest(requestId, ac, Account.class));
        } else {
            LOG.debug("Bad request: Self-care user doesn't have permissions for this account. Account can't be updated");
            throw new CustomParametrizedException(ErrorConstants.ERR_ACCESS_DENIED,
                new Exception("Self-care user doesn't have permissions for this account. Account can't be updated"));
        }
        return ResponseEntity.ok().body(result);
    }

    @PreAuthorize("hasAnyAuthority('self_care_admin','self_care_access')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteSCAccount(@PathVariable Integer id) {
        LOG.debug("REST request to delete Account from self-care portal: {}", id);

        Account ac = accountService.getOne(id);
        User us = getCurrentUser();
        if(ac == null || us == null) {
            LOG.debug(NO_ACCOUNT);
            throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION,
                new Exception(NO_ACCOUNT));
        }

        // self_care_access user can not delete accounts
        // self_care_admin can delete any self-care account
        if(!ac.getIsSelfCare()) {//not self_care_account
            LOG.debug("Bad request: Not self-care account. Account can't be deleted");
            throw new CustomParametrizedException(ErrorConstants.ERR_ACCESS_DENIED,
                new Exception("Not self-care account. Account can't be deleted"));
        }

        if(!us.getIsSelfcareUser()) {
        	accountService.delete(id);
        }  else {
            LOG.debug("Bad request: Only admin can delete this account");
            throw new CustomParametrizedException(ErrorConstants.ERR_ACCESS_DENIED,
                new Exception("Only admin can delete this account"));
        }
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyAuthority('self_care_admin','self_care_access')")
    @GetMapping(value = "/{id}")
    public ResponseEntity<AccountViewModel> getSCAccount(@PathVariable Integer id) {
        LOG.debug("REST request to get Account from self-care portal: {}", id);

        final Account account = accountService.getOne(id);
        AccountViewModel accountViewModel = accountMapper.toViewModel(account);

        User us = getCurrentUser();

        if (us.getIsSelfcareUser() && !account.containsAccountUser(us)) {
            LOG.debug(NO_ACCOUNT);
            throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION,
                new Exception(NO_ACCOUNT));
        }

        return Optional.ofNullable(accountViewModel).map(result -> new ResponseEntity<>(result, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasAnyAuthority('self_care_admin','self_care_access')")
    @GetMapping
    @SuppressWarnings({"squid:S00107", "squid:S3776"})
    public ResponseEntity<?> getAllAccountsSC(@RequestParam(name = "search", required = false) final String textSearch,
                                              @RequestParam(required = false) Boolean filter,
                                              @RequestParam(required = false) String invoices,
                                              @RequestParam(required = false) Boolean credit,
                                              @RequestParam(required = false) Boolean cash,
                                              @RequestParam(required = false) Boolean selfCareAccounts,
                                              @RequestParam(required = false) String nationality,
                                              @RequestParam(required = false) String alias,
                                              @RequestParam(required = false) String externalAccountingSystemIdentifier,
                                              @RequestParam(required = false) Boolean flightSchedules,
                                              @RequestParam(required = false) Integer accountType,
                                              @SortDefault(sort = {"name"}, direction = Sort.Direction.ASC) Pageable pageable,
                                              @RequestParam(name = "csvExport", required = false) Boolean csvExport) throws IOException {

        if (textSearch == null) {
            LOG.debug("REST request to get self-care accounts for filter {} invoices {} credit {}", filter, invoices, credit);
        } else {
            LOG.debug("REST request to get self-care accounts that contain the text {} for filter {} invoices {} credit {}",
                textSearch, filter, invoices, credit);
        }

        User us = getCurrentUser();
        Page<Account> page;
        long count;

        if (!us.getIsSelfcareUser()) {
            page = accountService.findAllAccountsByFilter(filter, cash, invoices, credit, textSearch, pageable, selfCareAccounts,
            															nationality, alias, externalAccountingSystemIdentifier, flightSchedules, accountType);
            count = accountService.countAll();
        } else { // find accounts and approval requests for this user
            boolean needApproval = systemConfigurationService.getBoolean(SystemConfigurationItemName.REQUIRE_ADMIN_APPROVAL_FOR_SC_ACCOUNTS);
            if (needApproval && us.getIsSelfcareUser()) {
                List<AccountViewModel> accountList = accountMapper.toViewModel(accountService.findSCAccountsByFilterUser(filter, cash, invoices, credit, textSearch, us.getId()));
                long requestListSize = selfCarePortalApprovalRequestService.countAllinList(RequestStatus.OPEN.toValue(), RequestDataset.ACCOUNT.toValue(), us.getId());
                List<AccountViewModel> requestList = selfCarePortalApprovalRequestService.getAccountFromApprovalRequestList(
                    selfCarePortalApprovalRequestService.findAllinList(textSearch, RequestStatus.OPEN.toValue(),
                        RequestDataset.ACCOUNT.toValue(), null, null, null, us.getId()));
                accountList.addAll(requestList);
                accountList.sort(Comparator.comparing(AccountViewModel::getName));
                int start = pageable.getOffset();
                int end = (start + pageable.getPageSize()) > accountList.size() ? accountList.size() : (start + pageable.getPageSize());

                if (csvExport != null && csvExport) {
                    final List<AccountCsvExportModel> csvExportModel = accountMapper.toCsvModelFromViewModel(accountList);
                    ReportDocument report = reportDocumentCreator.createCsvDocument("Accounts", csvExportModel, AccountCsvExportModel.class, true);
                    return doCreateBinaryResponse(report);
                } else {
                    final Page<AccountViewModel> resultPage = new PageImplCustom<>(accountList.subList(start, end),
                        pageable, accountList.size(), accountService.countAllBySelfCareUser(us.getId()) + requestListSize);
                    return ResponseEntity.ok().body(resultPage);
                }
            } else {
                page = accountService.findSCAccountsByFilterUser(filter, cash, invoices, credit,
                    textSearch, pageable, us.getId(), flightSchedules, alias, externalAccountingSystemIdentifier);
                count = accountService.countAllBySelfCareUser(us.getId());
            }
        }

        if (csvExport != null && csvExport) {
            final List<Account> list = page.getContent();
            final List<AccountCsvExportModel> csvExportModel = accountMapper.toCsvModel(list);
            ReportDocument report = reportDocumentCreator.createCsvDocument("Accounts", csvExportModel, AccountCsvExportModel.class, true);
            return doCreateBinaryResponse(report);
        } else {
            final Page<AccountViewModel> resultPage = new PageImplCustom<>(accountMapper.toViewModel(page), pageable, page.getTotalElements(), count);
            return ResponseEntity.ok().body(resultPage);
        }
    }

    @PreAuthorize("hasAnyAuthority('self_care_admin','self_care_access')")
    @GetMapping(value = "/accounts-with-flight-schedules")
    public List<AccountViewModel> getAccountsWithFlightSchedules() {
        LOG.debug("REST request to get self-care accounts with flight schedules");

        User us = getCurrentUser();

        boolean needApproval = systemConfigurationService.getBoolean(SystemConfigurationItemName.REQUIRE_ADMIN_APPROVAL_FOR_SC_FLIGHT_SCHEDULES);

        if (!us.getIsSelfcareUser()) {
            return accountMapper.toViewModel(accountService.findAccountsWithFlightSchedules(null));
        } else {
            List<Account> list = accountService.findAccountsWithFlightSchedules(us.getId());
            if (needApproval) {
                List<Account> requestList = selfCarePortalApprovalRequestService.getAccountFromApprovalRequestListForFlightSchedule(us.getId());

                for (Account account: requestList) {
                    list.removeIf(a -> Objects.equals(a.getId(), account.getId()));
                }

                list.addAll(requestList);
                list.sort(Comparator.comparing(Account::getName));
            }
            return accountMapper.toViewModel(list);
        }
    }

    @PreAuthorize("hasAnyAuthority('self_care_admin','self_care_access')")
    @GetMapping(value = "/approved-only")
    public Collection<AccountComboViewModel> getListApprovedSCAccounts(@RequestParam(required = false, defaultValue = "false") boolean active) {
        LOG.debug("REST request to get all approved self-care accounts");

        // filter by user id if current user is a self care user
        User us = getCurrentUser();
        Integer userId = us.getIsSelfcareUser() ? us.getId() : null;

        // get all approved self care accounts by optional user id value
        Collection<Account> accounts = accountService.findAllSelfCareMinimalReturn(userId, active);
        return accountMapper.toComboViewModel(accounts);
    }

    @PreAuthorize("hasAnyAuthority('self_care_admin','self_care_access')")
    @GetMapping(value = "/cash-whitelist")
    public Collection<AccountComboViewModel> getListCashWhitelistAccounts() {
        LOG.debug("REST request to get all approved self care cash accounts that are not blacklisted");

        // filter by user id if current user is a self care user
        User us = getCurrentUser();
        Integer userId = us.getIsSelfcareUser() != null && us.getIsSelfcareUser() ? us.getId() : null;

        // get all approved self care accounts by optional user id value
        Collection<Account> accounts = accountService.findAllSelfCareWhitelistAccounts(userId);
        return accountMapper.toComboViewModel(accounts);
    }

    /**
	 * Return the currently logged-in user
	 */
	public User getCurrentUser() {
		return userService.getUserByLogin (SecurityUtils.getCurrentUserLogin());
	}
}
