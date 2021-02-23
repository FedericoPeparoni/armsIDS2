package ca.ids.abms.modules.bankaccount;

import ca.ids.abms.modules.common.controllers.AbmsCrudController;
import ca.ids.abms.modules.reports2.common.ReportDocumentCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping(BankAccountController.ENDPOINT)
@SuppressWarnings({"unused", "squid:S1452"})
public class BankAccountController extends AbmsCrudController<BankAccount, BankAccountViewModel, BankAccountCsvExportModel, Integer> {

    static final String ENDPOINT = "/api/bank-accounts";

    private static final Logger LOG = LoggerFactory.getLogger(BankAccountController.class);

    private final BankAccountMapper bankAccountMapper;
    private final BankAccountService bankAccountService;

    public BankAccountController(final BankAccountMapper bankAccountMapper,
                                 final BankAccountService bankAccountService,
                                 final ReportDocumentCreator reportDocumentCreator) {
        super(ENDPOINT, bankAccountMapper, bankAccountService, reportDocumentCreator, "Bank_Accounts", BankAccountCsvExportModel.class);
        this.bankAccountMapper = bankAccountMapper;
        this.bankAccountService = bankAccountService;
    }

    @Override
    @GetMapping
    @PreAuthorize("hasAuthority('banking_information_view')")
    public ResponseEntity<?> getPage(@RequestParam(required = false) final String search,
                                     @SortDefault.SortDefaults({@SortDefault(sort = { "name" })}) final Pageable pageable,
                                     @RequestParam(name = "csvExport", required = false) Boolean csvExport) {
        LOG.debug("REST request to get all bank accounts with search '{}' for page '{}'", search, pageable);
        return super.doGetPage(search, pageable, csvExport);
    }

    @Override
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('banking_information_view')")
    public ResponseEntity<BankAccountViewModel> getOne(@PathVariable final Integer id) {
        LOG.debug("REST request to get bank account with id '{}'", id);
        return super.doGetOne(id);
    }

    @Override
    @PostMapping
    @PreAuthorize("hasAuthority('banking_information_modify')")
    public ResponseEntity<BankAccountViewModel> create(@Valid @RequestBody final BankAccountViewModel viewModel) throws URISyntaxException {
        LOG.debug("REST request to create bank account : {}", viewModel);
        return super.doCreate(viewModel);
    }

    @Override
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('banking_information_modify')")
    public ResponseEntity<BankAccountViewModel> update(@PathVariable final Integer id,
                                                       @Valid @RequestBody final BankAccountViewModel viewModel) {
        LOG.debug("REST request to update bank account with id '{}' : {}", id, viewModel);
        return super.doUpdate(id, viewModel);
    }

    @Override
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('banking_information_modify')")
    public ResponseEntity delete(@PathVariable final Integer id) {
        LOG.debug("REST request to delete bank account with id '{}'", id);
        return super.doDelete(id);
    }

    @GetMapping(path = "/list")
    @PreAuthorize("hasAuthority('banking_information_view') or hasAuthority('transaction_view')")
    public ResponseEntity<List<BankAccountViewModel>> getBankAccounts() {
        LOG.debug("REST request to get list of bank accounts");
        return ResponseEntity.ok(bankAccountMapper
            .toViewModel(bankAccountService.findAll()));
    }

    @GetMapping(path = "/is-supported")
    @PreAuthorize("hasAuthority('banking_information_view') or hasAuthority('transaction_view') or hasAuthority('point_of_sale_access')")
    public ResponseEntity<Boolean> getBankAccountsIsSupported() {
        LOG.debug("REST request to get bank accounts support");
        return ResponseEntity.ok(bankAccountService.isSupported());
    }
}
