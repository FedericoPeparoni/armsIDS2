package ca.ids.abms.modules.atcmovements;

import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.config.error.PartialParametersException;
import ca.ids.abms.modules.common.controllers.BulkLoaderComponent;
import ca.ids.abms.modules.common.controllers.MediaDocumentComponent;
import ca.ids.abms.modules.common.dto.BulkLoaderSummary;
import ca.ids.abms.modules.dataimport.CsvImportServiceImp;
import ca.ids.abms.modules.dataimport.DataImportService;
import ca.ids.abms.modules.flightmovementsbuilder.FlightMovementBuilderException;
import ca.ids.abms.modules.reports2.common.ReportDocument;
import ca.ids.abms.modules.reports2.common.ReportDocumentCreator;
import ca.ids.abms.modules.util.models.PageImplCustom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/atc-movement-log")
@SuppressWarnings("squid:S1452")
public class AtcMovementLogController extends MediaDocumentComponent implements BulkLoaderComponent {

    private static final Logger LOG = LoggerFactory.getLogger(AtcMovementLogController.class);

    private final AtcMovementLogService atcMovementLogService;
    private final AtcMovementLogMapper atcMovementLogMapper;
    private final ReportDocumentCreator reportDocumentCreator;

    @Autowired
    @Qualifier(DATA_IMPORT_SERVICE)
    private CsvImportServiceImp<AtcMovementLogCsvViewModel> dataImportService;

    @Qualifier(ATC_LOG_LOADER)
    private AtcMovementLogBulkLoader movementLogsUploadService;

    public AtcMovementLogController(final AtcMovementLogService atcMovementLogService,
                                    final AtcMovementLogMapper atcMovementLogMapper,
                                    final ReportDocumentCreator reportDocumentCreator,
                                    final AtcMovementLogBulkLoader movementLogsUploadService) {
        this.atcMovementLogService = atcMovementLogService;
        this.atcMovementLogMapper = atcMovementLogMapper;
        this.reportDocumentCreator = reportDocumentCreator;
        this.movementLogsUploadService = movementLogsUploadService;
    }

    @GetMapping
    public ResponseEntity<?> findAll (
        @RequestParam(value = AtcMovementLogQuery.FILTER_BY_TEXT_SEARCH, required = false) String textSearch,
        @RequestParam(value = AtcMovementLogQuery.FILTER_BY_START_DATE, required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate startDate,
        @RequestParam(value = AtcMovementLogQuery.FILTER_BY_END_DATE, required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate endDate,
        @SortDefault.SortDefaults({
            @SortDefault(sort = {"dateOfContact"}, direction = Sort.Direction.DESC),
            @SortDefault(sort = {"departureTime"}, direction = Sort.Direction.DESC)}) Pageable pageable,
        @RequestParam(name = "csvExport", required = false) Boolean csvExport) {

        LOG.debug("REST request to get all ATC Movement Logs with filters: textSearch: {} startDate: {} endDate: {}",
            textSearch, startDate, endDate);

        final Page<AtcMovementLog> page = atcMovementLogService.findAll(pageable, textSearch, startDate, endDate);

        if (csvExport != null && csvExport) {
            final List<AtcMovementLog> list = page.getContent();
            final List<AtcMovementLogCsvExportModel> csvExportModel = atcMovementLogMapper.toCsvModel(list);
            ReportDocument report = reportDocumentCreator.createCsvDocument("Atc_Movement_Logs", csvExportModel, AtcMovementLogCsvExportModel.class, true);
            return doCreateBinaryResponse(report);
        } else {
            final Page<AtcMovementLogViewModel> resultPage = new PageImplCustom<>(atcMovementLogMapper.toViewModel(page), pageable, page.getTotalElements(), atcMovementLogService.countAll());
            return ResponseEntity.ok().body(resultPage);
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<AtcMovementLogViewModel> getOne(@PathVariable final Integer id) {
        final AtcMovementLog item = atcMovementLogService.getOne(id);
        final AtcMovementLogViewModel dto = atcMovementLogMapper.toViewModel(item);
        return Optional.ofNullable(dto)
            .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasAuthority('flight_log_modify')")
    @PostMapping
    public ResponseEntity<AtcMovementLogViewModel> create(@Valid @RequestBody final AtcMovementLogViewModel dto) throws URISyntaxException {

        if (dto.getId() != null) {
            throw new CustomParametrizedException(ErrorConstants.ERR_ID_MAY_BE_NULL, "id");
        }

        final AtcMovementLog itemToCreate = atcMovementLogMapper.toModel(dto);
        final AtcMovementLog createdItem;
        try {
            atcMovementLogService.validateAerodromeIdentifiers(itemToCreate);
            createdItem = atcMovementLogService.create(itemToCreate);
        } catch (FlightMovementBuilderException e) {
            throw new PartialParametersException(e.getFlightMovementBuilderIssue().toValue(), e.getLocalizedMessage());
        }
        final AtcMovementLogViewModel resultDto = atcMovementLogMapper.toViewModel(createdItem);
        return ResponseEntity.created(new URI("/api/atc-movement-log/" + createdItem.getId()))
            .body(resultDto);
    }

    @PreAuthorize("hasAuthority('flight_log_modify')")
    @PutMapping(value = "/{id}")
    public ResponseEntity<AtcMovementLogViewModel> update(@PathVariable final Integer id,
                                                          @RequestBody final AtcMovementLogViewModel dto){
        final AtcMovementLog itemToUpdate = atcMovementLogMapper.toModel(dto);
        final AtcMovementLog updatedItem;
        try {
            atcMovementLogService.validateAerodromeIdentifiers(itemToUpdate);
            updatedItem = atcMovementLogService.update(id, itemToUpdate);
        } catch (FlightMovementBuilderException e) {
            throw new PartialParametersException(e.getFlightMovementBuilderIssue().toValue(), e.getLocalizedMessage());
        }
        final AtcMovementLogViewModel updatedDto = atcMovementLogMapper.toViewModel(updatedItem);
        return ResponseEntity.ok().body(updatedDto);
    }

    @PreAuthorize("hasAuthority('flight_log_modify')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable final Integer id) {
        atcMovementLogService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('flight_log_modify')")
    @PutMapping(value = "/upload", headers = ("content-type=multipart/*"))
    public ResponseEntity<BulkLoaderSummary> upload (@RequestParam("file") final MultipartFile file,
                                                     @RequestParam(required = false) Map<String, String> params) {
        List<AtcMovementLogCsvViewModel> movements;
        try {
            movements = dataImportService.parseFromMultipartFile(
                file, DataImportService.STRATEGY.BIND_BY_POSITION, AtcMovementLogCsvViewModel.class);
        } catch (Exception ex) {
            LOG.error("Cannot read the file {} because: {}", file.getOriginalFilename(), ex.getMessage());
            throw new CustomParametrizedException(ErrorConstants.ERR_FILE_VALIDATION, ex, file.getOriginalFilename());
        }
        final BulkLoaderSummary summary = movementLogsUploadService.bulkLoad(movements, file);
        return ResponseEntity.ok().body(summary);
    }
}
