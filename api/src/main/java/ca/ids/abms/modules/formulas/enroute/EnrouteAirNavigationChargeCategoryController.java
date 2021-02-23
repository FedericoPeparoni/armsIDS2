package ca.ids.abms.modules.formulas.enroute;

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
@RequestMapping("/api/enroute-airnavigation-charges")
@SuppressWarnings({"unused", "squid:S1452"})
public class EnrouteAirNavigationChargeCategoryController extends MediaDocumentComponent {

    private static final Logger LOG = LoggerFactory.getLogger(EnrouteAirNavigationChargeCategoryController.class);

    private EnrouteAirNavigationChargeCategoryService enrouteAirNavigationChargeCategoryService;
    private EnrouteAirNavigationChargeCategoryMapper enrouteAirNavigationChargeCategoryMapper;
    private final ReportDocumentCreator reportDocumentCreator;

    public EnrouteAirNavigationChargeCategoryController(final EnrouteAirNavigationChargeCategoryService enrouteAirNavigationChargeCategoryService,
                                                        final EnrouteAirNavigationChargeCategoryMapper enrouteAirNavigationChargeCategoryMapper,
                                                        final ReportDocumentCreator reportDocumentCreator) {
        this.enrouteAirNavigationChargeCategoryService = enrouteAirNavigationChargeCategoryService;
        this.enrouteAirNavigationChargeCategoryMapper = enrouteAirNavigationChargeCategoryMapper;
        this.reportDocumentCreator = reportDocumentCreator;
    }

    @PreAuthorize("hasAuthority('enroute_charges_modify')")
    @PostMapping
    public ResponseEntity<EnrouteAirNavigationChargeCategoryViewModel> createEnrouteAirNavigationChargeCategory(
        @Valid @RequestBody final EnrouteAirNavigationChargeCategoryViewModel enrouteAirNavigationChargeCategory) throws URISyntaxException {

        LOG.debug("REST request to save EnrouteAirNavigationChargeCategory : {}", enrouteAirNavigationChargeCategory);

        if (enrouteAirNavigationChargeCategory.getId() != null) {
            return ResponseEntity.badRequest().body(null);
        }
        final EnrouteAirNavigationChargeCategory itemToCreate = enrouteAirNavigationChargeCategoryMapper.toModel(enrouteAirNavigationChargeCategory);
        EnrouteAirNavigationChargeCategory result = enrouteAirNavigationChargeCategoryService.save(itemToCreate);
        final EnrouteAirNavigationChargeCategoryViewModel resultDto = enrouteAirNavigationChargeCategoryMapper.toViewModel(result);
        return ResponseEntity.created(new URI("/api/enroute-charge-categories/" + resultDto.getId()))
                .body(resultDto);
    }

    @PreAuthorize("hasAuthority('enroute_charges_modify')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteEnrouteAirNavigationChargeCategory(@PathVariable final Integer id) {
        LOG.debug("REST request to delete EnrouteAirNavigationChargeCategory : {}", id);

        enrouteAirNavigationChargeCategoryService.delete(id);

        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<?> getAllEnrouteAirNavigationChargeCategories(@SortDefault(sort = {"mtowCategoryUpperLimit"},
                                                                        direction = Sort.Direction.ASC) Pageable pageable,
                                                                        @RequestParam(name = "csvExport", required = false) Boolean csvExport) {

        LOG.debug("REST request to get all EnrouteAirNavigationChargeCategory");
        final Page<EnrouteAirNavigationChargeCategory> page = enrouteAirNavigationChargeCategoryService.findAll(pageable);

        if (csvExport != null && csvExport) {
            final List<EnrouteAirNavigationChargeCategory> list = page.getContent();
            final List<EnrouteAirNavigationChargeCategoryCsvExportModel> csvExportModel = enrouteAirNavigationChargeCategoryMapper.toCsvModel(list);
            ReportDocument report = reportDocumentCreator.createCsvDocument("Enroute_Air_Navigation_Charges", csvExportModel,
                EnrouteAirNavigationChargeCategoryCsvExportModel.class, true);
            return doCreateBinaryResponse(report);
        } else {
            final Page<EnrouteAirNavigationChargeCategoryViewModel> resultPage =
                new PageImplCustom<>(enrouteAirNavigationChargeCategoryMapper.toViewModel(page), pageable, page.getTotalElements(),
                enrouteAirNavigationChargeCategoryService.countAll());
            return ResponseEntity.ok().body(resultPage);
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<EnrouteAirNavigationChargeCategoryViewModel> getEnrouteAirNavigationChargeCategory(@PathVariable final Integer id) {
        LOG.debug("REST request to get EnrouteAirNavigationChargeCategory : {}", id);

        EnrouteAirNavigationChargeCategory enrouteAirNavigationChargeCategory = enrouteAirNavigationChargeCategoryService.getOne(id);

        return Optional.ofNullable(enrouteAirNavigationChargeCategory)
                .map(result -> new ResponseEntity<>(enrouteAirNavigationChargeCategoryMapper.toViewModel(result), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasAuthority('enroute_charges_modify')")
    @PutMapping(value = "/{id}")
    public ResponseEntity<EnrouteAirNavigationChargeCategoryViewModel> updateEnrouteAirNavigationChargeCategory(
        @RequestBody final EnrouteAirNavigationChargeCategoryViewModel enrouteAirNavigationChargeCategoryDto,
        @PathVariable final Integer id) {

        LOG.debug("REST request to update EnrouteAirNavigationChargeCategory : {}", enrouteAirNavigationChargeCategoryDto);

        final EnrouteAirNavigationChargeCategory enrouteAirNavigationChargeCategory = enrouteAirNavigationChargeCategoryMapper.toModel(enrouteAirNavigationChargeCategoryDto);
        final EnrouteAirNavigationChargeCategory result = enrouteAirNavigationChargeCategoryService.update(id, enrouteAirNavigationChargeCategory);
        final EnrouteAirNavigationChargeCategoryViewModel resultDto= enrouteAirNavigationChargeCategoryMapper.toViewModel(result);

        return ResponseEntity.ok().body(resultDto);
    }
    
    @PostMapping(value="/validate")
    public ResponseEntity<List<EnrouteAirNavigationChargeFormulaValidationViewModel> > validateFormula(
        @Valid @RequestBody final EnrouteAirNavigationChargeCategoryViewModel formulaDto) {

        LOG.debug("REST request to validate a formula");
        if (formulaDto.getId() != null) {
            return ResponseEntity.badRequest().body(null);
        }
        final EnrouteAirNavigationChargeCategory formula = enrouteAirNavigationChargeCategoryMapper.toModel(formulaDto);
        final List<EnrouteAirNavigationChargeFormulaValidationViewModel> result = enrouteAirNavigationChargeCategoryService.validateEnrouteAirNavigationChargeFormula(formula);
        return ResponseEntity.ok().body(result);
    }
}
