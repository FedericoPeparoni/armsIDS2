package ca.ids.abms.modules.aircraft;

import ca.ids.abms.modules.common.controllers.MediaDocumentComponent;
import ca.ids.abms.modules.reports2.common.ReportDocument;
import ca.ids.abms.modules.reports2.common.ReportDocumentCreator;
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
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import ca.ids.abms.modules.util.models.PageImplCustom;

@RestController
@RequestMapping("/api/aircraft-types")
@SuppressWarnings({"unused", "squid:S1452"})
public class AircraftTypeController extends MediaDocumentComponent {

    private static final Logger LOG = LoggerFactory.getLogger(AircraftTypeController.class);
    private final AircraftTypeService aircraftTypeService;
    private final AircraftTypeMapper aircraftTypeMapper;
    private final ReportDocumentCreator reportDocumentCreator;

    public AircraftTypeController(final AircraftTypeService aAircraftTypeService,
                                  final AircraftTypeMapper aAircraftTypeMapper,
                                  final ReportDocumentCreator reportDocumentCreator) {
        aircraftTypeService = aAircraftTypeService;
        aircraftTypeMapper = aAircraftTypeMapper;
        this.reportDocumentCreator = reportDocumentCreator;
    }

    @GetMapping
    public ResponseEntity<?> getAllAircraftTypes(@RequestParam(name = "searchFilter", required = false) final String searchFilter,
                                                 @SortDefault(sort = { "aircraftType" }, direction = Sort.Direction.ASC) Pageable pageable,
                                                 @RequestParam(name = "csvExport", required = false) Boolean csvExport) {
        LOG.debug("REST request to get all Aircraft Types");
        Page<AircraftType> page = aircraftTypeService.findAll(pageable, searchFilter);
        long countAll = aircraftTypeService.countAllAircraftTypes();

        if (csvExport != null && csvExport) {
            final List<AircraftType> list = page.getContent();
            final List<AircraftTypeCsvExportModel> csvExportModel = aircraftTypeMapper.toCsvModel(list);
            ReportDocument report = reportDocumentCreator.createCsvDocument("Aircraft_Types", csvExportModel, AircraftTypeCsvExportModel.class, true);
            return doCreateBinaryResponse(report);
        } else {
            final Page<AircraftTypeViewModel> resultPage = new PageImplCustom<>(aircraftTypeMapper.toViewModel(page),
                pageable, page.getTotalElements(), countAll);
            return ResponseEntity.ok().body(resultPage);
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<AircraftTypeViewModel> getAircraftType(@PathVariable final Integer id) {
        LOG.debug("REST request to get AircraftType : {}", id);

        AircraftType aircraftType = aircraftTypeService.getOne(id);

        return Optional.ofNullable(aircraftType)
                .map(result -> new ResponseEntity<>(aircraftTypeMapper.toViewModel(result), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasAuthority('aircraft_type_modify')")
    @PostMapping
    public ResponseEntity<AircraftTypeViewModel> createAircraftType(@Valid @RequestBody final AircraftTypeViewModel aircraftType) throws URISyntaxException {
        LOG.debug("REST request to save AircraftType : {}", aircraftType);

        if (aircraftType.getAircraftType() == null) {
            return ResponseEntity.badRequest().body(null);
        }

        AircraftType result = aircraftTypeService.save(aircraftTypeMapper.toModel(aircraftType));
        AircraftTypeViewModel veiwModel = aircraftTypeMapper.toViewModel(result);

        return ResponseEntity.created(new URI("/api/aircraft-types/" + result.getAircraftType()))
                .body(veiwModel);
    }

    @PreAuthorize("hasAuthority('aircraft_type_modify')")
    @PutMapping(value = "/{id}")
    public ResponseEntity<AircraftTypeViewModel> updateAircraftType(@Valid @RequestBody final AircraftTypeViewModel aircraftType, @PathVariable final Integer id) {
        LOG.debug("REST request to update AircraftType : {}", aircraftType);

        AircraftType result = aircraftTypeService.update(id, aircraftTypeMapper.toModel(aircraftType));
        AircraftTypeViewModel veiwModel = aircraftTypeMapper.toViewModel(result);

        return ResponseEntity.ok().body(veiwModel);
    }

    @PreAuthorize("hasAuthority('aircraft_type_modify')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteAircraftType(@PathVariable final Integer id) {
        LOG.debug("REST request to delete AircraftType : {}", id);

        aircraftTypeService.delete(id);

        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/allMinimal")
    public Collection<AircraftTypeComboViewModel> findAllMinimal() {
        LOG.debug("REST request to get all Aircraft Types, with minimal return data");
        return aircraftTypeMapper.toComboViewModel(aircraftTypeService.findAll());
    }
}
