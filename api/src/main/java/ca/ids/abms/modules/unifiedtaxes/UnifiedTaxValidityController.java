package ca.ids.abms.modules.unifiedtaxes;

import java.net.URISyntaxException;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.ids.abms.modules.common.controllers.AbmsCrudController;
import ca.ids.abms.modules.reports2.common.ReportDocumentCreator;

@RestController
@RequestMapping(UnifiedTaxValidityController.ENDPOINT)
public class UnifiedTaxValidityController
        extends AbmsCrudController<UnifiedTaxValidity, UnifiedTaxValidityViewModel, UnifiedTaxValidityCsvExportModel, Integer> {

    static final String ENDPOINT = "/api/unified-tax-validities";

    private static final Logger LOG = LoggerFactory.getLogger(UnifiedTaxValidityController.class);

    private final UnifiedTaxValidityMapper unifiedTaxValidityMapper;
    private final UnifiedTaxValidityService unifiedTaxValidityService;

    public UnifiedTaxValidityController(final UnifiedTaxValidityMapper unifiedTaxValidityMapper, final UnifiedTaxValidityService unifiedTaxValidityService,
            final ReportDocumentCreator reportDocumentCreator) {
        super(ENDPOINT, unifiedTaxValidityMapper, unifiedTaxValidityService, reportDocumentCreator, "Unified_Tax Validity",
                UnifiedTaxValidityCsvExportModel.class);
        this.unifiedTaxValidityMapper = unifiedTaxValidityMapper;
        this.unifiedTaxValidityService = unifiedTaxValidityService;
    }

    @Override
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('unified_tax_view')")
    public ResponseEntity<UnifiedTaxValidityViewModel> getOne(@PathVariable final Integer id) {
        LOG.debug("REST request to get unifiedTax account with id '{}'", id);
        return super.doGetOne(id);
    }

    @Override
    @PostMapping
    @PreAuthorize("hasAuthority('unified_tax_modify')")
    public ResponseEntity<UnifiedTaxValidityViewModel> create(@Valid @RequestBody final UnifiedTaxValidityViewModel viewModel)
            throws URISyntaxException {
        LOG.debug("REST request to create unifiedTax account : {}", viewModel);
        return super.doCreate(viewModel);
    }

    @Override
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('unified_tax_modify')")
    public ResponseEntity<UnifiedTaxValidityViewModel> update(@PathVariable final Integer id,
            @Valid @RequestBody final UnifiedTaxValidityViewModel viewModel) {
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
    public ResponseEntity<List<UnifiedTaxValidityViewModel>> getAllUnifiedTaxes() {
        LOG.debug("REST request to get list of unified tax ");
        return ResponseEntity.ok(unifiedTaxValidityMapper.toViewModel(unifiedTaxValidityService.findAll()));
    }

    @Override
    @GetMapping
    @PreAuthorize("hasAuthority('unified_tax_view')")
    public ResponseEntity<?> getPage(String search, Pageable pageable, Boolean csvExport) {
        LOG.debug("REST request to get all unified tax validities with search '{}' for page '{}'", search, pageable);
        return super.doGetPage(search, pageable, csvExport);
    }

}