package ca.ids.abms.modules.airspaces;

import ca.ids.abms.modules.common.controllers.MediaDocumentComponent;
import ca.ids.abms.modules.reports2.common.ReportDocument;
import ca.ids.abms.modules.reports2.common.ReportDocumentCreator;
import ca.ids.abms.modules.util.models.PageImplCustom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/airspaces")
@SuppressWarnings({"unused", "squid:S1452"})
public class AirspaceController extends MediaDocumentComponent {

    private static final Logger LOG = LoggerFactory.getLogger(AirspaceController.class);
    private final AirspaceService airspaceService;
    private final AirspaceMapper airspaceMapper;
    private final ReportDocumentCreator reportDocumentCreator;

    public AirspaceController(final AirspaceService aAirspaceService,
                              final AirspaceMapper aAirspaceMapper,
                              final ReportDocumentCreator reportDocumentCreator) {
        airspaceService = aAirspaceService;
        airspaceMapper = aAirspaceMapper;
        this.reportDocumentCreator = reportDocumentCreator;
    }

    @PreAuthorize("hasAuthority('airspace_modify')")
    @PostMapping(value = "/fromnavdb/{id}")
    public ResponseEntity<AirspaceViewModel> createAirspaceFromNavDbByID(@PathVariable Integer id) {
        LOG.debug("REST request to createAirspaceFromNavDbByID airspace : {}", id);

        Airspace airspace = airspaceService.createAirspaceFromNavDbByID(id);

        return Optional.ofNullable(airspace)
                .map(result -> new ResponseEntity<>(airspaceMapper.toViewModel(result), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping(value ="/fromnavdb")
    public Page<AirspaceViewModel> getAllFromNavDB(Pageable pageable) {
        LOG.debug("REST request to getAllFromNavDB all airspaces");
        return new PageImpl<>(airspaceMapper.toViewModel(airspaceService.getAllFromNavDb(pageable)));
    }

    @GetMapping
    public ResponseEntity<?> getAll(@SortDefault(sort = {"airspaceName"}, direction = Sort.Direction.ASC) Pageable pageable,
                                    @RequestParam(name = "searchFilter", required = false) final String searchFilter,
                                    @RequestParam(name = "csvExport", required = false) Boolean csvExport) {
        LOG.debug("REST request to getAll airspace from billing DB");

        final Page<Airspace> page = airspaceService.findAll(pageable, searchFilter);

        if (csvExport != null && csvExport) {
            final List<Airspace> list = page.getContent();
            final List<AirspaceCsvExportModel> csvExportModel = airspaceMapper.toCsvModel(list);
            ReportDocument report = reportDocumentCreator.createCsvDocument("Airspaces", csvExportModel, AirspaceCsvExportModel.class, true);
            return doCreateBinaryResponse(report);
        } else {
            final Page<AirspaceViewModel> resultPage = new PageImplCustom<>(
                airspaceMapper.toViewModel(page), pageable, page.getTotalElements(), airspaceService.countAll());
            return ResponseEntity.ok().body(resultPage);
        }
    }

    @PreAuthorize("hasAuthority('airspace_modify')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        LOG.debug("REST request to getAll airspace from billing DB");
        airspaceService.deleteAirspace(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('airspace_modify')")
    public ResponseEntity<AirspaceViewModel> update(@RequestBody final AirspaceViewModel airspaceDto, @PathVariable final Integer id) {
        LOG.debug("REST request to update Airspace : {}", airspaceDto);
        final Airspace airspace = airspaceMapper.toModel(airspaceDto);
        final AirspaceViewModel result = airspaceMapper.toViewModel(airspaceService.update(id, airspace));
        return ResponseEntity.ok().body(result);
    }
}
