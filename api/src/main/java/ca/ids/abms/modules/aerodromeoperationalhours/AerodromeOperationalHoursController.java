package ca.ids.abms.modules.aerodromeoperationalhours;

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
@RequestMapping("/api/aerodrome-operational-hours")
@SuppressWarnings({"unused", "squid:S1452"})
public class AerodromeOperationalHoursController extends MediaDocumentComponent {

    private static final Logger LOG = LoggerFactory.getLogger(AerodromeOperationalHoursController.class);

    private final AerodromeOperationalHoursService aerodromeOperationalHoursService;
    private final AerodromeOperationalHoursMapper aerodromeOperationalHoursMapper;
    private final ReportDocumentCreator reportDocumentCreator;

    public AerodromeOperationalHoursController(final AerodromeOperationalHoursService aerodromeOperationalHoursService,
                                               final AerodromeOperationalHoursMapper aerodromeOperationalHoursMapper,
                                               final ReportDocumentCreator reportDocumentCreator) {

        this.aerodromeOperationalHoursService = aerodromeOperationalHoursService;
        this.aerodromeOperationalHoursMapper = aerodromeOperationalHoursMapper;
        this.reportDocumentCreator = reportDocumentCreator;
    }

    @PreAuthorize("hasAuthority('aerodrome_operational_hours_modify')")
    @PostMapping
    public ResponseEntity<AerodromeOperationalHoursViewModel> createAerodromeOperationalHours(
        @Valid @RequestBody final AerodromeOperationalHoursViewModel aerodromeOperationalHours) throws URISyntaxException {

        LOG.debug("REST request to save Aerodrome Operational Hours : {}", aerodromeOperationalHours);

        if (aerodromeOperationalHours.getId() != null) {
            return ResponseEntity.badRequest().body(null);
        }

        AerodromeOperationalHours model = aerodromeOperationalHoursMapper.toModel(aerodromeOperationalHours);
        AerodromeOperationalHoursViewModel result = aerodromeOperationalHoursMapper.toViewModel(aerodromeOperationalHoursService.save(model));

        return ResponseEntity.created(new URI("/api/aerodrome-operational-hours/" + result.getId())).body(result);
    }

    @PreAuthorize("hasAuthority('aerodrome_operational_hours_modify')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteAerodromeOperationalHours(@PathVariable final Integer id) {
        LOG.debug("REST request to delete Aerodrome Operational Hours : {}", id);

        aerodromeOperationalHoursService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('aerodrome_operational_hours_view')")
    @GetMapping(value = "/{id}")
    public ResponseEntity<AerodromeOperationalHoursViewModel> getAerodromeOperationalHours(@PathVariable final Integer id) {
        LOG.debug("REST request to get Aerodrome Operational Hours by id : {}", id);

        AerodromeOperationalHours aerodromeOperationalHours = aerodromeOperationalHoursService.getOne(id);

        return Optional.ofNullable(aerodromeOperationalHours)
            .map(result -> new ResponseEntity<>(aerodromeOperationalHoursMapper.toViewModel(result), HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasAuthority('aerodrome_operational_hours_view')")
    @GetMapping
    public ResponseEntity<?> getAllAerodromeOperationalHours(@SortDefault(sort = {"aerodrome.aerodromeName"}, direction = Sort.Direction.ASC)
                                                                 final Pageable pageable,
                                                             @RequestParam(name = "search", required = false) final String textSearch,
                                                             @RequestParam(name = "csvExport", required = false) Boolean csvExport) {

        LOG.debug("REST request to get Aerodrome Operational Hours that contain the text: {}", textSearch);

        final Page<AerodromeOperationalHours> page = aerodromeOperationalHoursService.findAll(textSearch, pageable);
        if (csvExport != null && csvExport) {
            final List<AerodromeOperationalHours> list = page.getContent();
            final List<AerodromeOperationalHoursCsvExportModel> csvExportModel = aerodromeOperationalHoursMapper.toCsvModel(list);
            ReportDocument report = reportDocumentCreator.createCsvDocument("Aerodrome_Operational_Hours", csvExportModel,
                AerodromeOperationalHoursCsvExportModel.class, true);
            return doCreateBinaryResponse(report);
        } else {
            final Page<AerodromeOperationalHoursViewModel> resultPage = new PageImplCustom<>(aerodromeOperationalHoursMapper.toViewModel(page),
                pageable, page.getTotalElements(), aerodromeOperationalHoursService.countAll());
            return ResponseEntity.ok().body(resultPage);
        }
    }

    @PreAuthorize("hasAuthority('aerodrome_operational_hours_modify')")
    @PutMapping(value = "/{id}")
    public ResponseEntity<AerodromeOperationalHoursViewModel> updateAerodromeOperationalHours(
        @Valid @RequestBody final AerodromeOperationalHoursViewModel aerodromeOperationalHoursViewModel,
        @PathVariable final Integer id) {

        LOG.debug("REST request to update Aerodrome Operational Hours : {}", aerodromeOperationalHoursViewModel);

        final AerodromeOperationalHours aerodromeOperationalHours = aerodromeOperationalHoursMapper.toModel(aerodromeOperationalHoursViewModel);
        final AerodromeOperationalHoursViewModel result = aerodromeOperationalHoursMapper
            .toViewModel(aerodromeOperationalHoursService.update(id, aerodromeOperationalHours));

        return ResponseEntity.ok().body(result);
    }
}
