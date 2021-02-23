package ca.ids.abms.modules.exemptions.flightstatus;

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
@RequestMapping("/api/exempt-flight-status")
@SuppressWarnings({"unused", "squid:S1452"})
public class ExemptFlightStatusController extends MediaDocumentComponent {

    private static final Logger LOG = LoggerFactory.getLogger(ExemptFlightStatusController.class);

    private final ExemptFlightStatusService exemptFlightStatusService;
    private final ExemptFlightStatusMapper exemptFlightStatusMapper;
    private final ReportDocumentCreator reportDocumentCreator;

    public ExemptFlightStatusController(final ExemptFlightStatusService exemptFlightStatusService,
                                        final ExemptFlightStatusMapper exemptFlightStatusMapper,
                                        final ReportDocumentCreator reportDocumentCreator) {
        this.exemptFlightStatusService = exemptFlightStatusService;
        this.exemptFlightStatusMapper = exemptFlightStatusMapper;
        this.reportDocumentCreator = reportDocumentCreator;
    }

    @GetMapping
    public ResponseEntity<?> findAll (@RequestParam(name = "searchFilter", required = false) final String textSearch,
                                      @SortDefault.SortDefaults({
                                          @SortDefault(sort = {"flightItemType"}, direction = Sort.Direction.ASC),
                                          @SortDefault(sort = {"flightItemValue"}, direction = Sort.Direction.ASC)}) final Pageable pageable,
                                      @RequestParam(name = "csvExport", required = false) Boolean csvExport) {
        LOG.debug("REST request to get all exempt flight status");
        final Page<ExemptFlightStatus> page = exemptFlightStatusService.findAll(pageable, textSearch);

        if (csvExport != null && csvExport) {
            final List<ExemptFlightStatus> list = page.getContent();
            final List<ExemptFlightStatusCsvExportModel> csvExportModel = exemptFlightStatusMapper.toCsvModel(list);
            ReportDocument report = reportDocumentCreator.createCsvDocument("Exempt_Flight_Status", csvExportModel,
                ExemptFlightStatusCsvExportModel.class, true);
            return doCreateBinaryResponse(report);
        } else {
            final Page<ExemptFlightStatusViewModel> resultPage = new PageImplCustom<>(
                exemptFlightStatusMapper.toViewModel(page), pageable, page.getTotalElements(), exemptFlightStatusService.countAll());
            return ResponseEntity.ok().body(resultPage);
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ExemptFlightStatusViewModel> getOne(@PathVariable final Integer id) {
        LOG.debug("REST request to get the Exempt Flight Status with id {}", id);
        final ExemptFlightStatus item = exemptFlightStatusService.getOne(id);
        final ExemptFlightStatusViewModel dto = exemptFlightStatusMapper.toViewModel(item);
        return Optional.ofNullable(dto)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasAuthority('exempt_flight_status_modify')")
    @PostMapping
    public ResponseEntity<ExemptFlightStatusViewModel> create(
        @Valid @RequestBody final ExemptFlightStatusViewModel dto) throws URISyntaxException {
        LOG.debug("REST request to create the Exempt Flight Status: {}", dto);
        if (dto.getId() != null) {
            throw new CustomParametrizedException(ErrorConstants.ERR_ID_MAY_BE_NULL, "id");
        }
        if (!dto.getFlightItemType().ifItemValueIsValid(dto.getFlightItemValue())) {
            throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION, "flightItemValue");
        }
        final ExemptFlightStatus itemToCreate = exemptFlightStatusMapper.toModel(dto);
        if (exemptFlightStatusService.exists(itemToCreate)) {
            throw new CustomParametrizedException(ErrorConstants.ERR_UNIQUENESS_VIOLATION_FLIGHT_TYPE_VALUE);
        }
        final ExemptFlightStatus createdItem = exemptFlightStatusService.create(itemToCreate);
        final ExemptFlightStatusViewModel resultDto = exemptFlightStatusMapper.toViewModel(createdItem);
        return ResponseEntity.created(new URI("/api/exempt-flight-routes/" + createdItem.getId()))
            .body(resultDto);
    }

    @PreAuthorize("hasAuthority('exempt_flight_status_modify')")
    @PutMapping(value = "/{id}")
    public ResponseEntity<ExemptFlightStatusViewModel> update(@PathVariable final Integer id,
                                                             @RequestBody final ExemptFlightStatusViewModel dto){
        LOG.debug("REST request to update the Exempt Flight Status with id {}", id);
        if (!dto.getFlightItemType().ifItemValueIsValid(dto.getFlightItemValue())) {
            throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION, "flightItemValue");
        }
        final ExemptFlightStatus itemToUpdate = exemptFlightStatusMapper.toModel(dto);
        final ExemptFlightStatus updatedItem = exemptFlightStatusService.update(id, itemToUpdate);
        final ExemptFlightStatusViewModel updatedDto = exemptFlightStatusMapper.toViewModel(updatedItem);
        return ResponseEntity.ok().body(updatedDto);
    }

    @PreAuthorize("hasAuthority('exempt_flight_status_modify')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable final Integer id) {
        LOG.debug("REST request to delete the Exempt Flight Status with id {}", id);
        exemptFlightStatusService.delete(id);
        return ResponseEntity.ok().build();
    }
}
