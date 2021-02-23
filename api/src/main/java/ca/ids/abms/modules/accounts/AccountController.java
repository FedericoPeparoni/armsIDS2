package ca.ids.abms.modules.accounts;

import ca.ids.abms.modules.common.controllers.MediaDocumentComponent;
import ca.ids.abms.modules.reports2.common.ReportDocument;
import ca.ids.abms.modules.reports2.common.ReportDocumentCreator;
import ca.ids.abms.modules.util.models.PageImplCustom;
import ca.ids.abms.util.StringUtils;
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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/accounts")
@SuppressWarnings({"unused", "squid:S1452"})
public class AccountController extends MediaDocumentComponent {

    private static final Logger LOG = LoggerFactory.getLogger(AccountController.class);

    private final AccountService accountService;
    private final AccountMapper accountMapper;
    private final ReportDocumentCreator reportDocumentCreator;

    public AccountController(final AccountService accountService, final AccountMapper accountMapper, final ReportDocumentCreator reportDocumentCreator) {
        this.accountService = accountService;
        this.accountMapper = accountMapper;
        this.reportDocumentCreator = reportDocumentCreator;
    }

    /**
     * Create a new account
     *
     * @param accountDto the DTO for the acount to be created
     * @return ResponseEntity<AccountViewModel> the DTO for the account created
     *
     */
    @PostMapping
    @PreAuthorize("hasAuthority('account_modify')")
    public ResponseEntity<AccountViewModel> createAccount(@Valid @RequestBody AccountViewModel accountDto) throws URISyntaxException {

        LOG.debug("REST request to save Account : {}", accountDto);

        if (accountDto.getId() != null) {
            return ResponseEntity.badRequest().body(null);
        }

        final Account account = accountMapper.toModel(accountDto);
        account.setBlackListedIndicator(Boolean.FALSE);
        final AccountViewModel result = accountMapper.toViewModel(accountService.save(account));

        return ResponseEntity.created(new URI("/api/accounts/" + result.getId())).body(result);
    }

    /**
     * Delete an existing account
     *
     * @param id the id of the account to delete
     * @return ResponseEntity<Void> success/failure message
     *
     */
    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('account_modify')")
    public ResponseEntity<Void> deleteAccount(@PathVariable Integer id) {
        LOG.debug("REST request to delete Account : {}", id);

        accountService.delete(id);

        return ResponseEntity.ok().build();
    }

    /**
     * Return a single account by id
     *
     * @param id the id of the account to retrieve
     * @return ResponseEntity<AccountViewModel> the account DTO
     *
     */
    @GetMapping(value = "/{id}")
    public ResponseEntity<AccountViewModel> getAccount(@PathVariable Integer id) {
        LOG.debug("REST request to get Account : {}", id);

        AccountViewModel account = accountMapper.toViewModel(accountService.getOne(id));

        return Optional.ofNullable(account).map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Return all accounts in the database
     *
     * @return Page<AccountViewModel> list of accounts limited by system page size
     */
    @GetMapping
    @SuppressWarnings("squid:S00107")
    public ResponseEntity<?> getAllAccounts(
        @RequestParam(name = "search", required = false) final String textSearch,
        @RequestParam(required = false) Boolean filter,
        @RequestParam(required = false) String invoices,
        @RequestParam(required = false) Boolean credit,
        @RequestParam(required = false) Boolean cash,
        @RequestParam(required = false) Boolean selfCareAccounts,
        @RequestParam(required = false) String nationality,
        @RequestParam(required = false) Boolean flightSchedules,
        @RequestParam(required = false) String alias,
        @RequestParam(required = false) String externalAccountingSystemIdentifier,
        @RequestParam(required = false) Integer accountType,
        @SortDefault(sort = {"name"}, direction = Sort.Direction.ASC) Pageable pageable,
        @RequestParam(name = "csvExport", required = false) Boolean csvExport) {

        // log appropriate message based on text search value
        if (StringUtils.isNotBlank(textSearch)) {
            LOG.debug("REST request to get accounts that contain the text {} for filter {} invoices {} credit {}",
                textSearch, filter, invoices, credit);
        } else {
            LOG.debug("REST request to get accounts for filter {} invoices {} credit {}", filter, invoices, credit);
        }

        final Page<Account> page = accountService.findAllAccountsByFilter(filter, cash, invoices, credit, textSearch, pageable, selfCareAccounts,
                nationality, alias, externalAccountingSystemIdentifier, flightSchedules, accountType);

        if (csvExport != null && csvExport) {
            final List<Account> list = page.getContent();
            final List<AccountCsvExportModel> csvExportModel = accountMapper.toCsvModel(list);
            ReportDocument report = reportDocumentCreator.createCsvDocument("Accounts", csvExportModel, AccountCsvExportModel.class, true);
            return doCreateBinaryResponse(report);

        } else {
            final Page<AccountViewModel> resultPage = new PageImplCustom<>(accountMapper.toViewModel(page), pageable,
                page.getTotalElements(), accountService.countAll());
            return ResponseEntity.ok().body(resultPage);
        }
    }

    // EXCLUDES cash accounts
    @GetMapping(value = "/names")
    public Collection<AccountComboViewModel> getAllAccountNames() {
        LOG.debug("REST request to get all account names");
        return accountMapper.toComboViewModel(accountService.findAllExcludingCash());
    }

    // INCLUDES cash accounts
    @GetMapping(value = "/namesWithCash")
    public Collection<AccountComboViewModel> getAllAccountNamesWithCash() {
        LOG.debug("REST request to get all account names");
        return accountMapper.toComboViewModel(accountService.findAll());
    }

    /**
     * CASH accounts only
     */
    @GetMapping(value = "/cashMinimal")
    public Collection<AccountComboViewModel> findAllCashMinimalReturn(@RequestParam(required = false, defaultValue = "false") boolean active) {
        if (active) {
            LOG.debug("REST request to get all active cash accounts, with minimal return data");
        } else {
            LOG.debug("REST request to get all cash accounts, with minimal return data");
        }
        return accountMapper.toComboViewModel(accountService.findAllOrActiveCashOrCredit(active, false, true));
    }

    /**
     * CREDIT accounts only
     */
    @GetMapping(value = "/creditMinimal")
    public Collection<AccountComboViewModel> findAllCreditMinimalReturn(@RequestParam(required = false, defaultValue = "false") boolean active) {
        if (active) {
            LOG.debug("REST request to get all active credit accounts, with minimal return data");
        } else {
            LOG.debug("REST request to get all credit accounts, with minimal return data");
        }
        return accountMapper.toComboViewModel(accountService.findAllOrActiveCashOrCredit(active, true, false));
    }

    /**
     * ALL accounts, cash and credit
     */
    @GetMapping(value = "/allMinimal")
    public Collection<AccountComboViewModel> findAllMinimal(@RequestParam(required = false, defaultValue = "false") boolean active) {
        if (active) {
            LOG.debug("REST request to get all active accounts, with minimal return data");
        } else {
            LOG.debug("REST request to get all accounts, with minimal return data");
        }
        return accountMapper.toComboViewModel(accountService.findAllOrActiveCashOrCredit(active, false, false));
    }

    /**
     * Aviation CREDIT accounts only
     */
    @GetMapping(value = "/activeCreditMinimalAviation")
    public Collection<AccountComboViewModel> findAviationCreditMinimalReturn(@RequestParam(required = false) Integer accountType) {
        LOG.debug("REST request to get aviation credit accounts, with minimal return data");
        return accountMapper.toComboViewModel(accountService.findActiveAviationCreditMinimalReturn(accountType));
    }

    /**
     * SCHED accounts, only account with flight schedules.
     */
    @GetMapping(value = "/schedMinimal")
    public Collection<AccountComboViewModel> findSchedMinimal() {
        LOG.debug("REST request to get scheduled accounts, with minimal return data");
        return accountMapper.toComboViewModel(accountService
            .findAccountsWithFlightSchedules(null));
    }

    /**
     * Update an existing account
     *
     * @param accountDto new data to set for the account
     * @param id          id of the account to update
     * @return ResponseEntity<AccountViewModel> the updated account
     *
     */
    @PutMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('account_modify')")
    public ResponseEntity<AccountViewModel> updateAccount(
        @RequestBody AccountViewModel accountDto, @PathVariable Integer id
    ) {
        LOG.debug("REST request to update Account : {}", accountDto);
        final Account account = accountMapper.toModel(accountDto);
        final AccountViewModel result = accountMapper.toViewModel(accountService.update(id, account));
        return ResponseEntity.ok().body(result);
    }

    @GetMapping(value = "/getCustomerNotifications")
    public ResponseEntity<List<Object>> listCustomerNotifications() {
        LOG.debug("REST request to get customer notifications");

        List<Object> result = accountService.getCustomerNotifications();

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PutMapping("/{id}/account_events")
    @PreAuthorize("hasAuthority('account_modify')")
    public ResponseEntity<AccountViewModel> updateAccountEvents(@PathVariable Integer id,
            @RequestBody Collection<AccountEventMapViewModel> eventsDto) {
        LOG.debug("REST request to save/update accounts events");

        final AccountViewModel result = accountMapper.toViewModel(accountService.updateAccountEvents(id, eventsDto));
        return ResponseEntity.ok().body(result);
    }
}
