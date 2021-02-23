package ca.ids.abms.modules.exemptions.account;

import ca.ids.abms.modules.common.controllers.MediaDocumentComponent;
import ca.ids.abms.modules.reports2.common.ReportDocument;
import ca.ids.abms.modules.reports2.common.ReportDocumentCreator;
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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/account-exemptions")
@SuppressWarnings({"unused", "squid:S1452"})
public class AccountExemptionController extends MediaDocumentComponent {

    private static final Logger LOG = LoggerFactory.getLogger(AccountExemptionController.class);

    private final AccountExemptionService accountExemptionService;
    private final AccountExemptionMapper accountExemptionMapper;
    private final ReportDocumentCreator reportDocumentCreator;

    public AccountExemptionController(final AccountExemptionService accountExemptionService,
                                      final AccountExemptionMapper accountExemptionMapper,
                                      final ReportDocumentCreator reportDocumentCreator) {
        this.accountExemptionService = accountExemptionService;
        this.accountExemptionMapper = accountExemptionMapper;
        this.reportDocumentCreator = reportDocumentCreator;
    }

    @GetMapping
    public ResponseEntity<?> findAll(@RequestParam(name = "searchFilter", required = false) final String textSearch,
                                     @SortDefault(sort = {"account.name"}, direction = Sort.Direction.ASC) final Pageable pageable,
                                     @RequestParam(name = "csvExport", required = false) Boolean csvExport) {
        LOG.debug("REST request to get all account exemptions");
        final Page<AccountExemption> page = accountExemptionService.findAll(pageable, textSearch);

        if (csvExport != null && csvExport) {
            final List<AccountExemption> list = page.getContent();
            final List<AccountExemptionCsvExportModel> csvExportModel = accountExemptionMapper.toCsvModel(list);
            ReportDocument report = reportDocumentCreator.createCsvDocument("Account_Exemptions", csvExportModel,
                AccountExemptionCsvExportModel.class, true);
            return doCreateBinaryResponse(report);
        } else {
            final Page<AccountExemptionViewModel> resultPage = new PageImplCustom<>(
                accountExemptionMapper.toViewModel(page), pageable, page.getTotalElements(), accountExemptionService.countAll());
            return ResponseEntity.ok().body(resultPage);
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<AccountExemptionViewModel> getOne(@PathVariable final Integer id) {
        LOG.debug("REST request to get the account exemption with id {}", id);
        final AccountExemption model = accountExemptionService.getOne(id);
        final AccountExemptionViewModel formulaDto = accountExemptionMapper.toViewModel(model);
        return Optional.ofNullable(formulaDto)
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping(value = "/account/{accountId}")
    public ResponseEntity<AccountExemptionViewModel> findOneByAccountId(@PathVariable final Integer accountId) {
        LOG.debug("REST request to get the account exemption with account id {}", accountId);
        final AccountExemption model = accountExemptionService.findOneByAccountId(accountId);
        final AccountExemptionViewModel formulaDto = accountExemptionMapper.toViewModel(model);
        return Optional.ofNullable(formulaDto)
            .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasAuthority('exempt_account_modify')")
    @PostMapping
    public ResponseEntity<AccountExemptionViewModel> create(@Valid @RequestBody final AccountExemptionViewModel dto) throws URISyntaxException {
        LOG.debug("REST request to create a new account exemption");
        if (dto.getId() != null) {
            return ResponseEntity.badRequest().body(null);
        }
        final AccountExemption model = accountExemptionMapper.toModel(dto);
        final AccountExemption result = accountExemptionService.create(model, dto.getAccountId());
        final AccountExemptionViewModel resultDto = accountExemptionMapper.toViewModel(result);

        return ResponseEntity.created(new URI("/api/account-exemptions/" + result.getId())).body(resultDto);
    }

    @PreAuthorize("hasAuthority('exempt_account_modify')")
    @PutMapping(value = "/{id}")
    public ResponseEntity<AccountExemptionViewModel> update(@PathVariable final Integer id, @RequestBody final AccountExemptionViewModel dto) {
        LOG.debug("REST request to update a account exemption with id {}", id);
        final AccountExemption model = accountExemptionMapper.toModel(dto);
        AccountExemption result = accountExemptionService.update(id, model, dto.getAccountId());
        final AccountExemptionViewModel resultDto = accountExemptionMapper.toViewModel(result);
        return ResponseEntity.ok().body(resultDto);
    }

    @PreAuthorize("hasAuthority('exempt_account_modify')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable final Integer id) {
        LOG.debug("REST request to remove the account exemption with id {}", id);
        accountExemptionService.delete(id);
        return ResponseEntity.ok().build();
    }
}
