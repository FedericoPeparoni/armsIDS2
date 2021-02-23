package ca.ids.abms.modules.aerodromes;

import ca.ids.abms.modules.aerodromeservicetypes.AerodromeServiceType;
import ca.ids.abms.modules.aerodromeservicetypes.AerodromeServiceTypeService;
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
@RequestMapping("/api/aerodromes")
@SuppressWarnings({"unused", "squid:S1452"})
public class AerodromeController extends MediaDocumentComponent {

    private static final Logger LOG = LoggerFactory.getLogger(AerodromeController.class);
    private final AerodromeService aerodromeService;
    private final AerodromeMapper aerodromeMapper;
    private final AerodromeServiceTypeService aerodromeServiceTypeService;
    private final ReportDocumentCreator reportDocumentCreator;

    public AerodromeController(final AerodromeService aerodromeService,
                               final AerodromeMapper aerodromeMapper,
                               final AerodromeServiceTypeService aerodromeServiceTypeService,
                               final ReportDocumentCreator reportDocumentCreator) {
        this.aerodromeService = aerodromeService;
        this.aerodromeMapper = aerodromeMapper;
        this.aerodromeServiceTypeService = aerodromeServiceTypeService;
        this.reportDocumentCreator = reportDocumentCreator;
    }

    @GetMapping
    public ResponseEntity<?> getAllAerodromes(@RequestParam(name = "searchFilter", required = false) final String searchFilter,
                                              @SortDefault(sort = {"aerodromeName"}, direction = Sort.Direction.ASC) Pageable pageable,
                                              @RequestParam(name = "csvExport", required = false) Boolean csvExport) {
        LOG.debug("REST request to get all Aerodromes");
        final Page<Aerodrome> page = aerodromeService.findAll(pageable, searchFilter);

        if (csvExport != null && csvExport) {
            final List<Aerodrome> list = page.getContent();
            final List<AerodromeCsvExportModel> csvExportModel = aerodromeMapper.toCsvModel(list);
            ReportDocument report = reportDocumentCreator.createCsvDocument("Aerodromes", csvExportModel, AerodromeCsvExportModel.class, true);
            return doCreateBinaryResponse(report);
        } else {
            long countAll = aerodromeService.countAllAerodromes();

            final Page<AerodromeViewModel> resultPage = new PageImplCustom<>(
                aerodromeMapper.toViewModel(page), pageable, page.getTotalElements(), countAll);
            return ResponseEntity.ok().body(resultPage);
        }
    }

    /**
     * Get aerodromes managed by the billing center of the current user.
     * <p>
     * <b>Usage</b>
     * <code><pre>GET /api/aerodromes?current-billing-center</pre></code>
     */
    @GetMapping(params = { "current-billing-center" })
    public List<Aerodrome> getAerodromesForCurrentBillingCenter() {
        LOG.debug("REST request to get aerodromes for the current billing center");
        return aerodromeService.findAerodromesForCurrentBillingCenter();
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<AerodromeViewModel> getAerodrome(@PathVariable final Integer id) {
        LOG.debug("REST request to get AerodromeCategory : {}", id);

        Aerodrome aerodrome = aerodromeService.getOne(id);

        return Optional.ofNullable(aerodrome)
                .map(result -> new ResponseEntity<>(aerodromeMapper.toViewModel(result), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasAuthority('aerodrome_modify')")
    @PostMapping
    public ResponseEntity<AerodromeViewModel> createAerodrome(@Valid @RequestBody final AerodromeViewModel aerodromeDto) throws URISyntaxException {
        LOG.debug("REST request to save Aerodrome : {}", aerodromeDto);

        if (aerodromeDto.getId() != null) {
            return ResponseEntity.badRequest().body(null);
        }

        final Aerodrome aerodrome = aerodromeMapper.toModel(aerodromeDto);
        Aerodrome result = aerodromeService.save(aerodrome);
        final AerodromeViewModel resultDto = aerodromeMapper.toViewModel(result);

        return ResponseEntity.created(new URI("/api/aerodromes/" + result.getId()))
                .body(resultDto);
    }

    @PreAuthorize("hasAuthority('aerodrome_modify')")
    @PutMapping(value = "/{id}")
    public ResponseEntity<AerodromeViewModel> updateAerodrome(@Valid @RequestBody final AerodromeViewModel aerodromeDto, final @PathVariable Integer id) {
        LOG.debug("REST request to update Aerodrome : {}", aerodromeDto);

        final Aerodrome aerodrome = aerodromeMapper.toModel(aerodromeDto);
        final Aerodrome result = aerodromeService.update(id, aerodrome);
        final AerodromeViewModel resultDto = aerodromeMapper.toViewModel(result);

        return ResponseEntity.ok().body(resultDto);
    }

    @PreAuthorize("hasAuthority('aerodrome_modify')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteAerodrome(@PathVariable final Integer id) {
        LOG.debug("REST request to delete Aerodrome : {}", id);

        aerodromeService.delete(id);

        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "validate/{aerodromeIdentifier}")
    public ResponseEntity<Boolean> validateAerodromeIdentifier(@PathVariable final String aerodromeIdentifier) {
        LOG.debug("REST request validate AerodromeIdentifier : {}", aerodromeIdentifier);
        Boolean valid = aerodromeService.validateAeroDromeIdentifier(aerodromeIdentifier);
        return ResponseEntity.ok().body(valid);
    }

    @PreAuthorize("hasAuthority('aerodrome_view') or hasAuthority('aerodrome_service_outage_view')")
    @GetMapping(value = "aerodrome-service-types")
    public ResponseEntity<List<AerodromeServiceType>> getAllAerodromeServiceTypes() {
        LOG.debug("REST request to get all Aerodrome Service Types");
        return new ResponseEntity<>(aerodromeServiceTypeService.findAll(), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('aerodrome_view') or hasAuthority('aerodrome_service_outage_view')")
    @GetMapping(value = "/aerodrome-services")
    public List<AerodromeComboViewModel> getAllAerodromeServices() {
        LOG.debug("REST request to get Aerodrome Services");
        return aerodromeMapper.toComboViewModel(aerodromeService.getAllAerodromesWithServices());
    }
}
