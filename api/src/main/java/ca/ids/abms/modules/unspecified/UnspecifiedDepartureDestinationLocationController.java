package ca.ids.abms.modules.unspecified;

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
@RequestMapping("/api/unspecified-departure-destination-locations")
@SuppressWarnings({"unused", "squid:S1452"})
public class UnspecifiedDepartureDestinationLocationController extends MediaDocumentComponent {

    private static final Logger LOG = LoggerFactory.getLogger(UnspecifiedDepartureDestinationLocationController.class);

    private UnspecifiedDepartureDestinationLocationService unspecifiedDepartureDestinationLocationService;
    private UnspecifiedDepartureDestinationLocationMapper unspecifiedDepartureDestinationLocationMapper;
    private final ReportDocumentCreator reportDocumentCreator;

    public UnspecifiedDepartureDestinationLocationController(
        final UnspecifiedDepartureDestinationLocationService unspecifiedDepartureDestinationLocationService,
        final UnspecifiedDepartureDestinationLocationMapper unspecifiedDepartureDestinationLocationMapper,
        final ReportDocumentCreator reportDocumentCreator) {

        this.unspecifiedDepartureDestinationLocationService = unspecifiedDepartureDestinationLocationService;
        this.unspecifiedDepartureDestinationLocationMapper = unspecifiedDepartureDestinationLocationMapper;
        this.reportDocumentCreator = reportDocumentCreator;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('zzzz_locations_modify')")
    public ResponseEntity<UnspecifiedDepartureDestinationLocationViewModel> createUnspecifiedDepartureDestinationLocation(
            @Valid @RequestBody UnspecifiedDepartureDestinationLocationViewModel dto) throws URISyntaxException {
        LOG.debug("REST request to save UnspecifiedDepartureDestinationLocation : {}", dto);

        if (dto.getTextIdentifier() == null) {
            return ResponseEntity.badRequest().body(null);
        }

        dto.setStatus(UnspecifiedDepartureDestinationLocationStatus.MANUAL);

        UnspecifiedDepartureDestinationLocation unspecifiedDepartureDestinationLocation = unspecifiedDepartureDestinationLocationMapper
                .toModel(dto);
        UnspecifiedDepartureDestinationLocation unspecifiedDepartureDestinationLocationCreate = unspecifiedDepartureDestinationLocationService
                .create(unspecifiedDepartureDestinationLocation, dto.getAerodromeIdentifier());
        UnspecifiedDepartureDestinationLocationViewModel result = unspecifiedDepartureDestinationLocationMapper
                .toViewModel(unspecifiedDepartureDestinationLocationCreate);

        return ResponseEntity.created(new URI("/api/unspecified-departure-destination-locations/" + result.getId()))
                .body(result);
    }

    @PutMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('zzzz_locations_modify')")
    public ResponseEntity<UnspecifiedDepartureDestinationLocationViewModel> updateUnspecifiedDepartureDestinationLocation(
            @RequestBody UnspecifiedDepartureDestinationLocationViewModel dto, @PathVariable Integer id) {
        LOG.debug("REST request to update UnspecifiedDepartureDestinationLocation : {}", dto);

        dto.setStatus(UnspecifiedDepartureDestinationLocationStatus.MANUAL);

        UnspecifiedDepartureDestinationLocation unspecifiedDepartureDestinationLocation = unspecifiedDepartureDestinationLocationMapper
                .toModel(dto);
        UnspecifiedDepartureDestinationLocation unspecifiedDepartureDestinationLocationUpdate = unspecifiedDepartureDestinationLocationService
                .update(id, unspecifiedDepartureDestinationLocation, dto.getAerodromeIdentifier());
        UnspecifiedDepartureDestinationLocationViewModel result = unspecifiedDepartureDestinationLocationMapper
                .toViewModel(unspecifiedDepartureDestinationLocationUpdate);

        return ResponseEntity.ok().body(result);
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('zzzz_locations_modify')")
    public ResponseEntity<Void> deleteUnspecifiedDepartureDestinationLocation(@PathVariable Integer id) {
        LOG.debug("REST request to delete UnspecifiedDepartureDestinationLocation : {}", id);
        unspecifiedDepartureDestinationLocationService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<?> getAllUnspecifiedDepartureDestinationLocations(
        @RequestParam(name = "search", required = false) final String search,
        @SortDefault(sort = { "textIdentifier" }, direction = Sort.Direction.ASC) Pageable pageable,
        @RequestParam(name = "csvExport", required = false) Boolean csvExport) {

        LOG.debug("REST request to get all UnspecifiedDepartureDestinationLocation");
        final Page<UnspecifiedDepartureDestinationLocation> page = unspecifiedDepartureDestinationLocationService
                .findAll(search, pageable);
        long countAll = unspecifiedDepartureDestinationLocationService.countAllUnspecifiedLocations();

        if (csvExport != null && csvExport) {
            final List<UnspecifiedDepartureDestinationLocation> list = page.getContent();
            final List<UnspecifiedDepartureDestinationLocationCsvExportModel> csvExportModel = unspecifiedDepartureDestinationLocationMapper.toCsvModel(list);
            ReportDocument report = reportDocumentCreator.createCsvDocument("Unspecified_Departure_Destination_Locations", csvExportModel,
                UnspecifiedDepartureDestinationLocationCsvExportModel.class, true);
            return doCreateBinaryResponse(report);
        } else {
            final Page<UnspecifiedDepartureDestinationLocationViewModel> resultPage = new PageImplCustom<>(
                unspecifiedDepartureDestinationLocationMapper.toViewModel(page), pageable, page.getTotalElements(), countAll);
            return ResponseEntity.ok().body(resultPage);
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<UnspecifiedDepartureDestinationLocationViewModel> getUnspecifiedDepartureDestinationLocation(
            @PathVariable Integer id) {
        LOG.debug("REST request to get UnspecifiedDepartureDestinationLocation : {}", id);

        UnspecifiedDepartureDestinationLocation unspecifiedDepartureDestinationLocation = unspecifiedDepartureDestinationLocationService
                .findOne(id);

        return Optional.ofNullable(unspecifiedDepartureDestinationLocation)
                .map(result -> new ResponseEntity<>(unspecifiedDepartureDestinationLocationMapper.toViewModel(result),
                        HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));

    }

}
