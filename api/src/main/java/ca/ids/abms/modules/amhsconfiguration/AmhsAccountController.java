package ca.ids.abms.modules.amhsconfiguration;

import ca.ids.abms.modules.common.controllers.MediaDocumentComponent;
import ca.ids.abms.modules.reports2.common.ReportDocument;
import ca.ids.abms.modules.reports2.common.ReportDocumentCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping(AmhsAccountController.ENDPOINT)
public class AmhsAccountController extends MediaDocumentComponent {

    static final String ENDPOINT = "/api/amhs-accounts";

    private static final Logger LOG = LoggerFactory.getLogger(AmhsAccountController.class);

    private final AmhsAccountService amhsAccountService;
    private final AmhsAccountMapper amhsAccountMapper;
    private final ReportDocumentCreator reportDocumentCreator;

    public AmhsAccountController(
        final AmhsAccountService amhsAccountService,
        final AmhsAccountMapper amhsAccountMapper, final ReportDocumentCreator reportDocumentCreator) {
        this.amhsAccountService = amhsAccountService;
        this.amhsAccountMapper = amhsAccountMapper;
        this.reportDocumentCreator = reportDocumentCreator;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('amhs_config_view')")
    @SuppressWarnings("rawtypes")
    public ResponseEntity getPage(
            @RequestParam(name = "searchFilter", required = false) final String searchFilter,
            @SortDefault.SortDefaults({@SortDefault(sort = { "addr" })}) final Pageable pageable,
            @RequestParam(name = "csvExport", required = false) Boolean csvExport) {
        LOG.debug("REST request to get all AMHS accounts with search '{}' for page '{}'", searchFilter, pageable);
        Page <AmhsAccount> page = amhsAccountService.findAll (searchFilter, pageable);

        if (csvExport != null && csvExport) {
            ReportDocument report = reportDocumentCreator.createCsvDocument (
                    "amhs_accounts", page.getContent(), AmhsAccount.class, true);
            return doCreateBinaryResponse(report);
        }
        return ResponseEntity.ok().body(page);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('amhs_config_view')")
    public ResponseEntity<AmhsAccount> getOne(@PathVariable final Integer id) {
        LOG.debug("REST request to get AMHS Account with id '{}'", id);
        return ResponseEntity.ok().body(amhsAccountService.findOne(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('amhs_config_modify')")
    @SuppressWarnings ({ "squid:S4684", "squid:S1075" })
    public ResponseEntity<AmhsAccountViewModel> create(
            @Valid @RequestBody final AmhsAccountViewModel viewModel) throws URISyntaxException {
        LOG.debug("REST request to create AMHS Account : {}", viewModel);

        AmhsAccount account = amhsAccountMapper.toModel(viewModel);

        final AmhsAccount entity = amhsAccountService.create (account);

        final AmhsAccountViewModel resultDto = amhsAccountMapper.toViewModel(entity);

        return ResponseEntity.created(new URI(ENDPOINT + "/" + entity.getId()))
                .body(resultDto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('amhs_config_modify')")
    @SuppressWarnings ("squid:S4684")
    public ResponseEntity<AmhsAccountViewModel> update(
            @PathVariable final Integer id,
            @Valid @RequestBody final AmhsAccountViewModel viewModel) {
        LOG.debug("REST request to update AMHS Account with id '{}' : {}", id, viewModel);

        AmhsAccount updateAmhsAccount = amhsAccountService.update(id,amhsAccountMapper.toModel(viewModel));
        AmhsAccountViewModel result = amhsAccountMapper.toViewModel(updateAmhsAccount);

        return ResponseEntity.ok().body (result);
    }

    @SuppressWarnings("rawtypes")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('amhs_config_modify')")
    public ResponseEntity delete(@PathVariable final Integer id) {
        LOG.debug("REST request to delete AMHS Account with id '{}'", id);
        amhsAccountService.remove (id);
        return ResponseEntity.ok().body (null);
    }

    @GetMapping(path = "/list")
    @PreAuthorize("hasAuthority('amhs_config_view')")
    public ResponseEntity<List<AmhsAccount>> list() {
        LOG.debug("REST request to get list of AMHS Accounts");
        return ResponseEntity.ok(amhsAccountService.findAll());
    }
}
