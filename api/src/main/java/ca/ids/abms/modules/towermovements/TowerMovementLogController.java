package ca.ids.abms.modules.towermovements;

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
@RequestMapping("/api/tower-movement-log")
@SuppressWarnings({"unused", "squid:S1452"})
public class TowerMovementLogController extends MediaDocumentComponent implements BulkLoaderComponent {

    private static final Logger LOG = LoggerFactory.getLogger(TowerMovementLogController.class);

    private final TowerMovementLogService towerMovementLogService;
    private final TowerMovementLogMapper towerMovementLogMapper;
    private final ReportDocumentCreator reportDocumentCreator;

    @Autowired
    @Qualifier(DATA_IMPORT_SERVICE)
    private CsvImportServiceImp<TowerMovementLogCsvViewModel> dataImportService;

    @Qualifier(TOWER_LOG_LOADER)
    private TowerMovementLogBulkLoader movementLogsUploadService;

    public TowerMovementLogController(final TowerMovementLogService towerMovementLogService,
                                      final TowerMovementLogMapper towerMovementLogMapper,
                                      final ReportDocumentCreator reportDocumentCreator,
                                      final TowerMovementLogBulkLoader movementLogsUploadService) {
        this.towerMovementLogService = towerMovementLogService;
        this.towerMovementLogMapper = towerMovementLogMapper;
        this.reportDocumentCreator = reportDocumentCreator;
        this.movementLogsUploadService = movementLogsUploadService;
    }

    @GetMapping
    public ResponseEntity<?> findAll (
        @RequestParam(value = TowerMovementLogQuery.FILTER_BY_TEXT_SEARCH, required = false) String textSearch,
        @RequestParam(value = TowerMovementLogQuery.FILTER_BY_START_DATE, required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate startDate,
        @RequestParam(value = TowerMovementLogQuery.FILTER_BY_END_DATE, required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate endDate,
        @SortDefault.SortDefaults({
            @SortDefault(sort = {"dateOfContact"}, direction = Sort.Direction.DESC),
            @SortDefault(sort = {"departureContactTime"}, direction = Sort.Direction.DESC)}) Pageable pageable,
        @RequestParam(name = "csvExport", required = false) Boolean csvExport) {

        LOG.debug("REST request to get all tower movement logs, with filters search: {}, start, {}, end {}", textSearch, startDate, endDate);

        final Page<TowerMovementLog> page = towerMovementLogService.findAll(pageable, startDate, endDate, textSearch);

        if (csvExport != null && csvExport) {
            final List<TowerMovementLog> list = page.getContent();
            final List<TowerMovementLogCsvExportModel> csvExportModel = towerMovementLogMapper.toCsvModel(list);
            ReportDocument report = reportDocumentCreator.createCsvDocument("Tower_Movement_Logs", csvExportModel, TowerMovementLogCsvExportModel.class, true);
            return doCreateBinaryResponse(report);
        } else {
            final Page<TowerMovementLogViewModel> resultPage = new PageImplCustom<>(towerMovementLogMapper.toViewModel(page), pageable,
                page.getTotalElements(), towerMovementLogService.countAll());
            return ResponseEntity.ok().body(resultPage);
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<TowerMovementLogViewModel> getOne(@PathVariable final Integer id) {
        final TowerMovementLog item = towerMovementLogService.getOne(id);
        final TowerMovementLogViewModel dto = towerMovementLogMapper.toViewModel(item);
        return Optional.ofNullable(dto)
            .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasAuthority('flight_log_modify')")
    @PostMapping
    public ResponseEntity<TowerMovementLogViewModel> create(@Valid @RequestBody final TowerMovementLogViewModel dto) throws URISyntaxException {
        if (dto.getId() != null) {
            throw new CustomParametrizedException(ErrorConstants.ERR_ID_MAY_BE_NULL, "id");
        }
        final TowerMovementLog itemToCreate = towerMovementLogMapper.toModel(dto);
        final TowerMovementLog createdItem;
        try {
            towerMovementLogService.validateAerodromeIdentifiers(itemToCreate);
            createdItem = towerMovementLogService.create(itemToCreate);
        } catch (FlightMovementBuilderException e) {
            throw new PartialParametersException(e.getFlightMovementBuilderIssue().toValue(), e.getLocalizedMessage());
        }
        final TowerMovementLogViewModel resultDto = towerMovementLogMapper.toViewModel(createdItem);
        return ResponseEntity.created(new URI("/api/tower-movement-log/" + createdItem.getId()))
            .body(resultDto);
    }

    @PreAuthorize("hasAuthority('flight_log_modify')")
    @PutMapping(value = "/{id}")
    public ResponseEntity<TowerMovementLogViewModel> update(@PathVariable final Integer id,
                                                             @RequestBody final TowerMovementLogViewModel dto){
        final TowerMovementLog itemToUpdate = towerMovementLogMapper.toModel(dto);
        final TowerMovementLog updatedItem;
        try {
            towerMovementLogService.validateAerodromeIdentifiers(itemToUpdate);
            updatedItem = towerMovementLogService.update(id, itemToUpdate);
        } catch (FlightMovementBuilderException e) {
            throw new PartialParametersException(e.getFlightMovementBuilderIssue().toValue(), e.getLocalizedMessage());
        }
        final TowerMovementLogViewModel updatedDto = towerMovementLogMapper.toViewModel(updatedItem);
        return ResponseEntity.ok().body(updatedDto);
    }

    @PreAuthorize("hasAuthority('flight_log_modify')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable final Integer id) {
        towerMovementLogService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('flight_log_modify')")
    @PutMapping(value = "/upload", headers = ("content-type=multipart/*"))
    public ResponseEntity<BulkLoaderSummary> upload (@RequestParam("file") final MultipartFile file,
                                                     @RequestParam(required = false) Map<String, String> params) {
        List<TowerMovementLogCsvViewModel> movements;
        try {
            movements = dataImportService.parseFromMultipartFile(
                file, DataImportService.STRATEGY.BIND_BY_POSITION, TowerMovementLogCsvViewModel.class);
        } catch (Exception ex) {
            LOG.error("Cannot read the file {} because: {}", file.getOriginalFilename(), ex.getMessage());
            throw new CustomParametrizedException("Invalid file", ex, file.getOriginalFilename());
        }
        final BulkLoaderSummary summary = movementLogsUploadService.bulkLoad(movements, file);
        return ResponseEntity.ok().body(summary);

    }
}
