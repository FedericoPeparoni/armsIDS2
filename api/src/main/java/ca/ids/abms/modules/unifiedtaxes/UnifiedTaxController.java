package ca.ids.abms.modules.unifiedtaxes;

import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ca.ids.abms.modules.common.controllers.AbmsCrudController;
import ca.ids.abms.modules.reports2.common.ReportDocumentCreator;
import ca.ids.abms.modules.util.models.PageImplCustom;

@RestController
@RequestMapping(UnifiedTaxController.ENDPOINT)
public class UnifiedTaxController
        extends AbmsCrudController<UnifiedTax, UnifiedTaxViewModel, UnifiedTaxCsvExportModel, Integer> {

    static final String ENDPOINT = "/api/unified-taxes";

    private static final Logger LOG = LoggerFactory.getLogger(UnifiedTaxController.class);

    private final UnifiedTaxMapper unifiedTaxMapper;
    private final UnifiedTaxService unifiedTaxService;

    public UnifiedTaxController(final UnifiedTaxMapper unifiedTaxMapper, final UnifiedTaxService unifiedTaxService,
            final ReportDocumentCreator reportDocumentCreator) {
        super(ENDPOINT, unifiedTaxMapper, unifiedTaxService, reportDocumentCreator, "Unified_Tax",
                UnifiedTaxCsvExportModel.class);
        this.unifiedTaxMapper = unifiedTaxMapper;
        this.unifiedTaxService = unifiedTaxService;
    }

    @Override
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('unified_tax_view')")
    public ResponseEntity<UnifiedTaxViewModel> getOne(@PathVariable final Integer id) {
        LOG.debug("REST request to get unifiedTax account with id '{}'", id);
        return super.doGetOne(id);
    }

    @Override
    @PostMapping
    @PreAuthorize("hasAuthority('unified_tax_modify')")
    public ResponseEntity<UnifiedTaxViewModel> create(@Valid @RequestBody final UnifiedTaxViewModel viewModel)
            throws URISyntaxException {
        LOG.debug("REST request to create unifiedTax account : {}", viewModel);
        return super.doCreate(viewModel);
    }

    @Override
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('unified_tax_modify')")
    public ResponseEntity<UnifiedTaxViewModel> update(@PathVariable final Integer id,
            @Valid @RequestBody final UnifiedTaxViewModel viewModel) {
        LOG.debug("REST request to update unifiedTax account with id '{}' : {}", id, viewModel);
        return super.doUpdate(id, viewModel);
    }

    @Override
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('unified_tax_modify')")
    public ResponseEntity<?> delete(@PathVariable final Integer id) {
        LOG.debug("REST request to delete unifiedTax account with id '{}'", id);
        return super.doDelete(id);
    }

    @GetMapping(path = "/list")
    @PreAuthorize("hasAuthority('unified_tax_view')")
    public ResponseEntity<List<UnifiedTaxViewModel>> getAllUnifiedTaxes() {
        LOG.debug("REST request to get list of unified tax ");
        return ResponseEntity.ok(unifiedTaxMapper.toViewModel(unifiedTaxService.findAll()));
    }

    @Override
    @GetMapping
    @PreAuthorize("hasAuthority('unified_tax_view')")
    public ResponseEntity<?> getPage(String search, Pageable pageable, Boolean csvExport) {
        LOG.debug("REST request to get all unified taxes with search '{}' for page '{}'", search, pageable);
        return super.doGetPage(search, pageable, csvExport);
    }

    @GetMapping("/validity-year/manufacture-year/")
    @PreAuthorize("hasAuthority('unified_tax_view')")
    public ResponseEntity<UnifiedTaxViewModel> getUnifiedTaxByValidityYearAndManufactureYear(
            @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime yearManufacture,
            @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime yearValidity) {
        LOG.debug("REST request to retrieve the unified tax for the specified manufacture year and validity date");
        UnifiedTax unifiedTaxEntity = unifiedTaxService.findUnifiedTaxByValidityYearAndManufactureYear(yearManufacture,
                yearValidity);
        return ResponseEntity.ok(unifiedTaxMapper.toViewModel(unifiedTaxEntity));
    }

    @GetMapping("/validity-year/current/manufacture-year/")
    @PreAuthorize("hasAuthority('unified_tax_view')")
    public ResponseEntity<UnifiedTaxViewModel> getCurrentUnifiedTaxByManufactureYear(
            @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime yearManufacture) {
        LOG.debug(
                "POST request to retrieve the unified tax to apply for the specified manifacture year at current date");
        LocalDateTime now = LocalDateTime.now();
        UnifiedTax unifiedTaxEntity = unifiedTaxService.findUnifiedTaxByValidityYearAndManufactureYear(yearManufacture,
                now);
        return new ResponseEntity<>(unifiedTaxMapper.toViewModel(unifiedTaxEntity), HttpStatus.OK);
    }

    @GetMapping(path = "/validity/{validityId}/list")
    @PreAuthorize("hasAuthority('unified_tax_view')")
    public ResponseEntity<?> getAllUnifiedTaxesPageByValidityId(@RequestParam(required = false) Integer validityId,
            String search, Pageable pageable, Boolean csvExport) {
        LOG.debug("REST request to get list of unified tax by the validity id '{}' with search '{}' for page '{}'",
                validityId, search, pageable);

        Page<UnifiedTax> page = unifiedTaxService.findAllByValidityId(validityId, search, pageable);
        return ResponseEntity.ok().body(new PageImplCustom<>(unifiedTaxMapper.toViewModel(page), pageable,
                page.getTotalElements(), unifiedTaxService.countAll()));
    }
}