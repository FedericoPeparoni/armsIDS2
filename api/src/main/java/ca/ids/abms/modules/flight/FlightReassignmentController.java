package ca.ids.abms.modules.flight;

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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/flight-reassignments")
@SuppressWarnings({"unused", "squid:S1452"})
public class FlightReassignmentController extends MediaDocumentComponent {

    private static final Logger LOG = LoggerFactory.getLogger(FlightReassignmentController.class);

    private final FlightReassignmentService flightReassignmentService;
    private final FlightReassignmentMapper flightReassignmentMapper;
    private final ReportDocumentCreator reportDocumentCreator;

    public FlightReassignmentController(final FlightReassignmentService aFlightReassignmentService,
                                        final FlightReassignmentMapper aFlightReassignmentMapper,
                                        final ReportDocumentCreator reportDocumentCreator) {
        flightReassignmentService = aFlightReassignmentService;
        flightReassignmentMapper = aFlightReassignmentMapper;
        this.reportDocumentCreator = reportDocumentCreator;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('flight_reassignment_modify')")
    public ResponseEntity<FlightReassignmentViewModel> createFlightReassignment(
            @Valid @RequestBody FlightReassignmentViewModel flightReassignmentViewModel) throws URISyntaxException {
        LOG.debug("REST request to save FlightReassignment : {}", flightReassignmentViewModel);

        if (flightReassignmentViewModel.getId() != null) {
            return ResponseEntity.badRequest().body(null);
        }
        
        List<FlightReassignmentAerodrome> assignedAerodromes = getAssignedAerodromes(
                flightReassignmentViewModel);

        FlightReassignment flightReassignment = flightReassignmentMapper.toModel(flightReassignmentViewModel);
        FlightReassignment flightReassignmentCreate = flightReassignmentService.create(flightReassignment, assignedAerodromes);
        FlightReassignmentViewModel result = flightReassignmentMapper.toViewModel(flightReassignmentCreate);

        return ResponseEntity.created(new URI("/api/flight-reassignments/" + result.getId())).body(result);
    }

    @PreAuthorize("hasAuthority('flight_reassignment_modify')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteFlightReassignment(@PathVariable Integer id) {
        LOG.debug("REST request to delete FlightReassignment : {}", id);
        flightReassignmentService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<?> getAllFlightReassignment(@RequestParam(name = "filter", required = false) final String filter,
                                                      @RequestParam(name = "account", required = false) final Integer account,
                                                      @SortDefault.SortDefaults({ @SortDefault(sort = { "account" }, direction = Sort.Direction.ASC),
                                                          @SortDefault(sort = {"identifierText" }, direction = Sort.Direction.ASC) }) Pageable pageable,
                                                      @RequestParam(name = "csvExport", required = false) Boolean csvExport) {

        LOG.debug("REST request to get all FlightReassignment, filtered by text: {}, account: {}", filter, account);

        final Page<FlightReassignment> page = flightReassignmentService.findAll(account, filter, pageable);
        List<FlightReassignmentViewModel> viewModels = flightReassignmentMapper.toViewModel(page);

        if (csvExport != null && csvExport) {
            final List<FlightReassignment> list = page.getContent();
            final List<FlightReassignmentCsvExportModel> csvExportModel = flightReassignmentMapper.toCsvModel(list);
            ReportDocument report = reportDocumentCreator.createCsvDocument("Flight_Reassignments", csvExportModel,
                FlightReassignmentCsvExportModel.class, true);
            return doCreateBinaryResponse(report);
        } else {
            final Page<FlightReassignmentViewModel> resultPage = new PageImplCustom<>(viewModels, pageable, page.getTotalElements(),
                flightReassignmentService.countAll());
            return ResponseEntity.ok().body(resultPage);
        }
    }

    @GetMapping(value = "/account/{account}")
    public ResponseEntity<Page<FlightReassignmentViewModel>> getAllFlightReassignmentByAccount(
            @RequestParam(name = "search", required = false) final String filter, @PathVariable Integer account,
            @SortDefault.SortDefaults({ @SortDefault(sort = { "account" }, direction = Sort.Direction.ASC),
                    @SortDefault(sort = {
                            "identifierText" }, direction = Sort.Direction.ASC) }) Pageable pageable) {
        LOG.debug("REST request to get all FlightReassignment for account {}", account);
        final Page<FlightReassignment> page = flightReassignmentService.findAll(account, filter, pageable);
        final Page<FlightReassignmentViewModel> resultPage = new PageImplCustom<>(flightReassignmentMapper.toViewModel(page),
                pageable, page.getTotalElements(), flightReassignmentService.countAll());
        return ResponseEntity.ok().body(resultPage);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<FlightReassignmentViewModel> getFlightReassignment(@PathVariable Integer id) {
        LOG.debug("REST request to get FlightReassignment : {}", id);

        FlightReassignment flightReassignment = flightReassignmentService.findOne(id);

        return Optional.ofNullable(flightReassignment)
                .map(result -> new ResponseEntity<>(flightReassignmentMapper.toViewModel(result), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasAuthority('flight_reassignment_modify')")
    @PutMapping(value = "/{id}")
    public ResponseEntity<FlightReassignmentViewModel> updateFlightReassignment(
            @RequestBody FlightReassignmentViewModel flightReassignmentViewModel, @PathVariable Integer id) {
        LOG.debug("REST request to update FlightReassignment : {}", flightReassignmentViewModel);

        List<FlightReassignmentAerodrome> assignedAerodromes = getAssignedAerodromes(
                flightReassignmentViewModel);
        
        FlightReassignment flightReassignment = flightReassignmentMapper.toModel(flightReassignmentViewModel);
        FlightReassignment flightReassignmentUpdate = flightReassignmentService.update(id, flightReassignment, assignedAerodromes);
        FlightReassignmentViewModel result = flightReassignmentMapper.toViewModel(flightReassignmentUpdate);

        return ResponseEntity.ok().body(result);
    }
    
    private List<FlightReassignmentAerodrome> getAssignedAerodromes(
            FlightReassignmentViewModel aFlightReassignmentViewModel) {
        List<FlightReassignmentAerodrome> clusters = new ArrayList<>();
        Collection<String> identifiers = aFlightReassignmentViewModel.getAerodromeIdentifiers();
        if (identifiers != null) {
            for (String identifier : identifiers) {
                FlightReassignmentAerodrome c = new FlightReassignmentAerodrome();
                c.setAerodromeIdentifier(identifier);
                clusters.add(c);
            }
        }
        return clusters;
    }
}
