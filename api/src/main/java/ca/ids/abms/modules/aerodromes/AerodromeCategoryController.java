package ca.ids.abms.modules.aerodromes;

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
@RequestMapping("/api/aerodromecategories")
@SuppressWarnings({"unused", "squid:S1452"})
public class AerodromeCategoryController extends MediaDocumentComponent {

    private static final Logger LOG = LoggerFactory.getLogger(AerodromeCategoryController.class);

    private AerodromeCategoryService aerodromeCategoryService;
    private AerodromeCategoryMapper aerodromeCategoryMapper;
    private final ReportDocumentCreator reportDocumentCreator;

    public AerodromeCategoryController(final AerodromeCategoryService aerodromeCategoryService,
                                       final AerodromeCategoryMapper aerodromeCategoryMapper,
                                       final ReportDocumentCreator reportDocumentCreator) {
        this.aerodromeCategoryService = aerodromeCategoryService;
        this.aerodromeCategoryMapper = aerodromeCategoryMapper;
        this.reportDocumentCreator = reportDocumentCreator;
    }

    @PreAuthorize("hasAuthority('aerodrome_category_modify')")
    @PostMapping
    public ResponseEntity<AerodromeCategoryViewModel> createAerodromeCategory(@Valid @RequestBody AerodromeCategoryViewModel aerodromeCategory)
        throws URISyntaxException {
        LOG.debug("REST request to save AerodromeCategory : {}", aerodromeCategory);

        if (aerodromeCategory.getId() != null) {
            return ResponseEntity.badRequest().body(null);
        }

        AerodromeCategory result = aerodromeCategoryService.save(aerodromeCategoryMapper.toModel(aerodromeCategory));
        AerodromeCategoryViewModel viewModel = aerodromeCategoryMapper.toViewModel(result);

        return ResponseEntity.created(new URI("/api/aerodromecategories/" + result.getId())).body(viewModel);
    }

    @PreAuthorize("hasAuthority('aerodrome_category_modify')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteAerodromeCategory(@PathVariable Integer id) {
        LOG.debug("REST request to delete AerodromeCategory : {}", id);

        aerodromeCategoryService.delete(id);

        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<AerodromeCategoryViewModel> getAerodromeCategory(@PathVariable Integer id) {
        LOG.debug("REST request to get AerodromeCategory : {}", id);

        AerodromeCategory aerodromeCategory = aerodromeCategoryService.getOne(id);

        return Optional.ofNullable(aerodromeCategory)
                .map(result -> new ResponseEntity<>(aerodromeCategoryMapper.toViewModel(result), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<?> getAllAerodromecategories(@SortDefault(sort = {"categoryName"}, direction = Sort.Direction.ASC) Pageable pageable,
                                                       @RequestParam(name = "search", required = false) final String textSearch,
                                                       @RequestParam(name = "csvExport", required = false) Boolean csvExport) {

        LOG.debug("REST request to get aerodrome categories that contain the text: {}", textSearch);

        final Page<AerodromeCategory> page = aerodromeCategoryService.findAll(textSearch, pageable);
        if (csvExport != null && csvExport) {
            final List<AerodromeCategory> list = page.getContent();
            final List<AerodromeCategoryCsvExportModel> csvExportModel = aerodromeCategoryMapper.toCsvModel(list);
            ReportDocument report = reportDocumentCreator.createCsvDocument("Aerodrome_Categories", csvExportModel,
                AerodromeCategoryCsvExportModel.class, true);
            return doCreateBinaryResponse(report);
        } else {
            final Page<AerodromeCategoryViewModel> resultPage = new PageImplCustom<>(aerodromeCategoryMapper.toViewModel(page),
                pageable, page.getTotalElements(), aerodromeCategoryService.countAll());
            return ResponseEntity.ok().body(resultPage);
        }
    }

    @PreAuthorize("hasAuthority('aerodrome_category_modify')")
    @PutMapping(value = "/{id}")
    public ResponseEntity<AerodromeCategoryViewModel> updateAerodromeCategory(@RequestBody AerodromeCategoryViewModel aerodromeCategoryDto,
                                                                              @PathVariable Integer id) {
        LOG.debug("REST request to update AerodromeCategory : {}", aerodromeCategoryDto);

        final AerodromeCategory aerodromeCategory = aerodromeCategoryMapper.toModel(aerodromeCategoryDto);
        final AerodromeCategory result = aerodromeCategoryService.update(id, aerodromeCategory);
        final AerodromeCategoryViewModel resultDto= aerodromeCategoryMapper.toViewModel(result);

        return ResponseEntity.ok().body(resultDto);
    }
}
