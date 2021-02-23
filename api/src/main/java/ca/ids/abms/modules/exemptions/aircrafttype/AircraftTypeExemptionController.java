package ca.ids.abms.modules.exemptions.aircrafttype;

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
@RequestMapping("/api/aircraft-type-exemptions")
@SuppressWarnings({"unused", "squid:S1452"})
public class AircraftTypeExemptionController extends MediaDocumentComponent {

    private static final Logger LOG = LoggerFactory.getLogger(AircraftTypeExemptionController.class);

    private final AircraftTypeExemptionService aircraftTypeExemptionService;
    private final AircraftTypeExemptionMapper aircraftTypeExemptionMapper;
    private final ReportDocumentCreator reportDocumentCreator;

    public AircraftTypeExemptionController(final AircraftTypeExemptionService aircraftTypeExemptionService,
                                           final AircraftTypeExemptionMapper aircraftTypeExemptionMapper,
                                           final ReportDocumentCreator reportDocumentCreator) {
        this.aircraftTypeExemptionService = aircraftTypeExemptionService;
        this.aircraftTypeExemptionMapper = aircraftTypeExemptionMapper;
        this.reportDocumentCreator = reportDocumentCreator;
    }

    @PreAuthorize("hasAuthority('exempt_aircraft_type_modify')")
    @PostMapping
    public ResponseEntity<AircraftTypeExemptionViewModel> create(@Valid @RequestBody final AircraftTypeExemptionViewModel dto) throws URISyntaxException {
        LOG.debug("REST request to create a new aircraft type exemption");
        if (dto.getId() != null) {
            return ResponseEntity.badRequest().body(null);
        }
        final AircraftTypeExemption model = aircraftTypeExemptionMapper.toModel(dto);
        final AircraftTypeExemption result = aircraftTypeExemptionService.create(model, dto.getAircraftType());
        final AircraftTypeExemptionViewModel resultDto = aircraftTypeExemptionMapper.toViewModel(result);

        return ResponseEntity.created(new URI("/api/aircraft-type-exemptions/" + result.getId())).body(resultDto);
    }

    @PreAuthorize("hasAuthority('exempt_aircraft_type_modify')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable final Integer id) {
        LOG.debug("REST request to remove the aircraft type exemption with id {}", id);
        aircraftTypeExemptionService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<?> findAll(@RequestParam(name = "searchFilter", required = false) final String textSearch,
                                     @SortDefault(sort = {"aircraftType.aircraftType"}, direction = Sort.Direction.ASC)
                                         Pageable pageable,
                                     @RequestParam(name = "csvExport", required = false) Boolean csvExport) {
        LOG.debug("REST request to get all aircraft type exemptions");
        final Page<AircraftTypeExemption> page = aircraftTypeExemptionService.findAll(pageable, textSearch);

        if (csvExport != null && csvExport) {
            final List<AircraftTypeExemption> list = page.getContent();
            final List<AircraftTypeExemptionCsvExportModel> csvExportModel = aircraftTypeExemptionMapper.toCsvModel(list);
            ReportDocument report = reportDocumentCreator.createCsvDocument("Aircraft_Type_Exemptions", csvExportModel,
                AircraftTypeExemptionCsvExportModel.class, true);
            return doCreateBinaryResponse(report);
        } else {
            final Page<AircraftTypeExemptionViewModel> resultPage = new PageImplCustom<>(aircraftTypeExemptionMapper.toViewModel(page),
                pageable, page.getTotalElements(), aircraftTypeExemptionService.countAll());
            return ResponseEntity.ok().body(resultPage);
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<AircraftTypeExemptionViewModel> getOne(@PathVariable final Integer id) {
        LOG.debug("REST request to get the aircraft type exemption with id {}", id);
        final AircraftTypeExemption model = aircraftTypeExemptionService.getOne(id);
        final AircraftTypeExemptionViewModel formulaDto = aircraftTypeExemptionMapper.toViewModel(model);
        return Optional.ofNullable(formulaDto).map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasAuthority('exempt_aircraft_type_modify')")
    @PutMapping(value = "/{id}")
    public ResponseEntity<AircraftTypeExemptionViewModel> update(@PathVariable final Integer id,
                                                                 @Valid @RequestBody final AircraftTypeExemptionViewModel dto) {
        LOG.debug("REST request to update a aircraft type exemption with id {}", id);
        final AircraftTypeExemption model = aircraftTypeExemptionMapper.toModel(dto);
        AircraftTypeExemption result = aircraftTypeExemptionService.update(id, model, dto.getAircraftType());
        final AircraftTypeExemptionViewModel resultDto = aircraftTypeExemptionMapper.toViewModel(result);
        return ResponseEntity.ok().body(resultDto);
    }
}
