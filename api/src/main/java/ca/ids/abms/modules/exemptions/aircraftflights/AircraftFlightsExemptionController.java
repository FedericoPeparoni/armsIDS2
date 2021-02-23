package ca.ids.abms.modules.exemptions.aircraftflights;

import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.config.error.ErrorConstants;
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
@RequestMapping("/api/exempt-aircraft-flights")
@SuppressWarnings({"unused", "squid:S1452"})
public class AircraftFlightsExemptionController extends MediaDocumentComponent {

    private static final Logger LOG = LoggerFactory.getLogger(AircraftFlightsExemptionController.class);

    private AircraftFlightsExemptionService aircraftFlightsExemptionService;
    private AircraftFlightsExemptionMapper aircraftFlightsExemptionMapper;
    private final ReportDocumentCreator reportDocumentCreator;

    public AircraftFlightsExemptionController(final AircraftFlightsExemptionService aircraftFlightsExemptionService,
                                              final AircraftFlightsExemptionMapper aircraftFlightsExemptiontMapper,
                                              final ReportDocumentCreator reportDocumentCreator) {
        this.aircraftFlightsExemptionService = aircraftFlightsExemptionService;
        this.aircraftFlightsExemptionMapper = aircraftFlightsExemptiontMapper;
        this.reportDocumentCreator = reportDocumentCreator;
    }

    @GetMapping
    public ResponseEntity<?> findAll (@RequestParam(name = "search", required = false) final String searchFilter,
                                      @SortDefault.SortDefaults({
                                          @SortDefault(sort = {"aircraftRegistration"}, direction = Sort.Direction.ASC),
                                          @SortDefault(sort = {"flightId"}, direction = Sort.Direction.ASC)}) Pageable pageable,
                                      @RequestParam(name = "csvExport", required = false) Boolean csvExport) {

        LOG.debug("REST request to get all exempt aircraft flights");

        final Page<AircraftFlightsExemption> page = aircraftFlightsExemptionService.findAll(pageable, searchFilter);

        if (csvExport != null && csvExport) {
            final List<AircraftFlightsExemption> list = page.getContent();
            final List<AircraftFlightsExemptionCsvExportModel> csvExportModel = aircraftFlightsExemptionMapper.toCsvModel(list);
            ReportDocument report = reportDocumentCreator.createCsvDocument("Aircraft_Flights_Exemptions", csvExportModel,
                AircraftFlightsExemptionCsvExportModel.class, true);
            return doCreateBinaryResponse(report);
        } else {
            final Page<AircraftFlightsExemptionViewModel> resultPage = new PageImplCustom<>(
                aircraftFlightsExemptionMapper.toViewModel(page), pageable, page.getTotalElements(), aircraftFlightsExemptionService.countAll());
            return ResponseEntity.ok().body(resultPage);
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<AircraftFlightsExemptionViewModel> getOne(@PathVariable Integer id) {
        LOG.debug("REST request to get one exempt aircraft flight by id {}", id);
        final AircraftFlightsExemption item = aircraftFlightsExemptionService.getOne(id);
        final AircraftFlightsExemptionViewModel dto = aircraftFlightsExemptionMapper.toViewModel(item);
        return Optional.ofNullable(dto)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasAuthority('exempt_aircraft_flights_modify')")
    @PostMapping
    public ResponseEntity<AircraftFlightsExemptionViewModel> create(
        @Valid @RequestBody AircraftFlightsExemptionViewModel dto) throws URISyntaxException {

        LOG.debug("REST request to create exempt aircraft flight: {}", dto);

        if (dto.getId() != null) {
            throw new CustomParametrizedException(ErrorConstants.ERR_ID_MAY_BE_NULL, "id");
        }
        final AircraftFlightsExemption itemToCreate = aircraftFlightsExemptionMapper.toModel(dto);
        final AircraftFlightsExemption createdItem = aircraftFlightsExemptionService.create(itemToCreate);
        final AircraftFlightsExemptionViewModel resultDto = aircraftFlightsExemptionMapper.toViewModel(createdItem);
        return ResponseEntity.created(new URI("/api/exempt-aircraft-flights/" + createdItem.getId()))
            .body(resultDto);
    }

    @PreAuthorize("hasAuthority('exempt_aircraft_flights_modify')")
    @PutMapping(value = "/{id}")
    public ResponseEntity<AircraftFlightsExemptionViewModel> update(@PathVariable Integer id,
                                                             @RequestBody AircraftFlightsExemptionViewModel dto){
        LOG.debug("REST request to update exempt aircraft flight: {}", dto);

        final AircraftFlightsExemption itemToUpdate = aircraftFlightsExemptionMapper.toModel(dto);
        final AircraftFlightsExemption updatedItem = aircraftFlightsExemptionService.update(id, itemToUpdate);
        final AircraftFlightsExemptionViewModel updatedDto = aircraftFlightsExemptionMapper.toViewModel(updatedItem);
        return ResponseEntity.ok().body(updatedDto);
    }

    @PreAuthorize("hasAuthority('exempt_aircraft_flights_modify')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        LOG.debug("REST request to delete exempt aircraft flight by id: {}", id);
        aircraftFlightsExemptionService.delete(id);
        return ResponseEntity.ok().build();
    }
}
