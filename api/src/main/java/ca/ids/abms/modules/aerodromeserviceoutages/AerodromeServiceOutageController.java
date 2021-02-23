package ca.ids.abms.modules.aerodromeserviceoutages;

import ca.ids.abms.modules.aerodromes.Aerodrome;
import ca.ids.abms.modules.aerodromes.AerodromeService;
import ca.ids.abms.modules.aerodromeservicetypes.*;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/aerodrome-service-outages")
@SuppressWarnings({"unused", "squid:S1452"})
public class AerodromeServiceOutageController extends MediaDocumentComponent {

    private static final Logger LOG = LoggerFactory.getLogger(AerodromeServiceOutageController.class);

    private final AerodromeServiceOutageService aerodromeServiceOutageService;
    private final AerodromeServiceOutageMapper aerodromeServiceOutageMapper;
    private final AerodromeServiceTypeMapService aerodromeServiceTypeMapService;
    private final AerodromeServiceTypeMapper aerodromeServiceTypeMapper;
    private final ReportDocumentCreator reportDocumentCreator;
    private final AerodromeService aerodromeService;
    private final AerodromeServiceTypeService aerodromeServiceTypeService;

    public AerodromeServiceOutageController(final AerodromeServiceOutageService aerodromeServiceOutageService,
                                            final AerodromeServiceOutageMapper aerodromeServiceOutageMapper,
                                            final AerodromeServiceTypeMapService aerodromeServiceTypeMapService,
                                            final AerodromeServiceTypeMapper aerodromeServiceTypeMapper,
                                            final ReportDocumentCreator reportDocumentCreator,
                                            final AerodromeService aerodromeService,
                                            final AerodromeServiceTypeService aerodromeServiceTypeService) {
        this.aerodromeServiceOutageService = aerodromeServiceOutageService;
        this.aerodromeServiceOutageMapper = aerodromeServiceOutageMapper;
        this.aerodromeServiceTypeMapService = aerodromeServiceTypeMapService;
        this.aerodromeServiceTypeMapper = aerodromeServiceTypeMapper;
        this.reportDocumentCreator = reportDocumentCreator;
        this.aerodromeService = aerodromeService;
        this.aerodromeServiceTypeService = aerodromeServiceTypeService;
    }

    @PreAuthorize("hasAuthority('aerodrome_service_outage_modify')")
    @PostMapping
    public ResponseEntity<AerodromeServiceOutageViewModel> createAerodromeServiceOutage(
        @Valid @RequestBody final AerodromeServiceOutageViewModel aerodromeServiceOutageViewModel) throws URISyntaxException {
        LOG.debug("REST request to save Aerodrome Service Outage : {}", aerodromeServiceOutageViewModel);

        if (aerodromeServiceOutageViewModel.getId() != null) {
            return ResponseEntity.badRequest().body(null);
        }

        setAerodromeServiceTypeMap(aerodromeServiceOutageViewModel);

        AerodromeServiceOutage model = aerodromeServiceOutageMapper.toModel(aerodromeServiceOutageViewModel);
        AerodromeServiceOutageViewModel result = aerodromeServiceOutageMapper.toViewModel(
            aerodromeServiceOutageService.save(model));

        return ResponseEntity.created(new URI("/api/aerodrome_service_outages/" + result.getId()))
            .body(result);
    }

    @PreAuthorize("hasAuthority('aerodrome_service_outage_modify')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteAerodromeServiceOutage(@PathVariable final Integer id) {
        LOG.debug("REST request to delete Aerodrome Service Outage : {}", id);

        aerodromeServiceOutageService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('aerodrome_service_outage_view')")
    @GetMapping(value = "/{id}")
    public ResponseEntity<AerodromeServiceOutageViewModel> getAerodromeServiceOutage(@PathVariable final Integer id) {
        LOG.debug("REST request to get Aerodrome Service Outage : {}", id);

        AerodromeServiceOutage aerodromeServiceOutage = aerodromeServiceOutageService.getOne(id);

        return Optional.ofNullable(aerodromeServiceOutage)
            .map(result -> new ResponseEntity<>(aerodromeServiceOutageMapper.toViewModel(result), HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasAuthority('aerodrome_service_outage_modify')")
    @PutMapping(value = "/{id}")
    public ResponseEntity<AerodromeServiceOutageViewModel> updateAerodromeServiceOutage(
        @Valid @RequestBody AerodromeServiceOutageViewModel aerodromeServiceOutageViewModel,
        @PathVariable final Integer id) {

        LOG.debug("REST request to update Aerodrome Service Outage : {}", aerodromeServiceOutageViewModel);

        setAerodromeServiceTypeMap(aerodromeServiceOutageViewModel);

        final AerodromeServiceOutage aerodromeServiceOutage = aerodromeServiceOutageMapper.toModel(aerodromeServiceOutageViewModel);
        final AerodromeServiceOutageViewModel result = aerodromeServiceOutageMapper
            .toViewModel(aerodromeServiceOutageService.update(id, aerodromeServiceOutage));

        return ResponseEntity.ok().body(result);
    }

    @PreAuthorize("hasAuthority('aerodrome_service_outage_view')")
    @GetMapping
    @SuppressWarnings("squid:S00107")
    public ResponseEntity<?> getAllAerodromeServiceTypeMap(
        @SortDefault(sort = {"aerodrome.aerodromeName", "aerodromeServiceType.serviceName"}, direction = Sort.Direction.ASC) final Pageable pageable,
        @RequestParam(name = "search", required = false) final String textSearch,
        @RequestParam(name = "serviceType", required = false) final String serviceType,
        @RequestParam(name = "aerodromeStatus", required = false) final String aerodromeStatus,
        @RequestParam(name = "aerodromeName", required = false) final String aerodromeName,
        @RequestParam(name = "startDateTime", required = false)@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime startDateTime,
        @RequestParam(name = "endDateTime", required = false)@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime endDateTime,
        @RequestParam(name = "csvExport", required = false) Boolean csvExport) {

        LOG.debug("REST request to get Aerodrome Service Outages that contain the text: {}", textSearch);

        final Page<AerodromeServiceTypeMap> page = aerodromeServiceTypeMapService.
            getAllAerodromeServiceTypeMapByFilter(textSearch, pageable, serviceType, aerodromeStatus, aerodromeName, startDateTime, endDateTime);

        if (csvExport != null && csvExport) {
            final List<AerodromeServiceTypeMap> list = page.getContent();
            final List<AerodromeServiceTypeMapCsvExportModel> csvExportModel = aerodromeServiceTypeMapper.toCsvModel(list);
            ReportDocument report = reportDocumentCreator.createCsvDocument("Aerodrome_Service_Outages", csvExportModel, AerodromeServiceTypeMapCsvExportModel.class, true);
            return doCreateBinaryResponse(report);
        } else {
            final Page<AerodromeServiceTypeMapViewModel> result =
                new PageImplCustom<>(aerodromeServiceTypeMapper.toMapViewModel(page.getContent()),
                    pageable, page.getTotalElements(), aerodromeServiceTypeMapService.countAll());
            return ResponseEntity.ok().body(result);
        }
    }

    private void setAerodromeServiceTypeMap(AerodromeServiceOutageViewModel aerodromeServiceOutageViewModel) {
        Aerodrome aerodrome = aerodromeService.findAeroDromeByAeroDromeName(aerodromeServiceOutageViewModel.getAerodrome());
        AerodromeServiceType aerodromeServiceType = aerodromeServiceTypeService.findByServiceName(aerodromeServiceOutageViewModel.getAerodromeServiceType());
        AerodromeServiceTypeMap map = new AerodromeServiceTypeMap();
        AerodromeServiceTypeKey key = new AerodromeServiceTypeKey(aerodrome, aerodromeServiceType);
        map.setId(key);
        aerodromeServiceOutageViewModel.setAerodromeServiceTypeMap(map);
    }
}
