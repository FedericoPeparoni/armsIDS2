package ca.ids.abms.modules.flight;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import ca.ids.abms.modules.common.controllers.MediaDocumentComponent;
import ca.ids.abms.modules.common.dto.UploadReportParsedItems;
import ca.ids.abms.modules.reports2.common.ReportDocument;
import ca.ids.abms.modules.reports2.common.ReportDocumentCreator;
import ca.ids.abms.modules.util.models.PageImplCustom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.modules.common.dto.UploadReportViewModel;
import ca.ids.abms.modules.dataimport.CsvImportServiceImp;
import ca.ids.abms.modules.dataimport.DataImportService;

@RestController
@RequestMapping("/api/flight-schedules")
@SuppressWarnings({"unused", "squid:S1452"})
public class FlightScheduleController extends MediaDocumentComponent {

    private static final Logger LOG = LoggerFactory.getLogger(FlightScheduleController.class);

    private static final String DATA_IMPORT_SERVICE = "recordableRowCsvImporter";
    private static final String FLIGHT_SCHEDULE_LOADER = "flightScheduleLoader";

    private final FlightScheduleService flightScheduleService;
    private final FlightScheduleMapper flightScheduleMapper;
    private final ReportDocumentCreator reportDocumentCreator;

    @Autowired
    @Qualifier(DATA_IMPORT_SERVICE)
    private CsvImportServiceImp<FlightScheduleCsvViewModel> dataImportService;

    @Qualifier(FLIGHT_SCHEDULE_LOADER)
    private FlightScheduleLoader loaderService;

    public FlightScheduleController(final FlightScheduleService flightScheduleService,
                                    final FlightScheduleMapper flightScheduleMapper,
                                    final ReportDocumentCreator reportDocumentCreator,
                                    final FlightScheduleLoader loaderService) {
        this.flightScheduleService = flightScheduleService;
        this.flightScheduleMapper = flightScheduleMapper;
        this.reportDocumentCreator = reportDocumentCreator;
        this.loaderService = loaderService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('flight_schedule_modify')")
    public ResponseEntity<FlightScheduleViewModel> createFlightSchedule(
        @Valid @RequestBody final FlightScheduleViewModel flightScheduleViewModel) throws URISyntaxException {
        LOG.debug("REST request to save FlightSchedule : {}", flightScheduleViewModel);

        if (flightScheduleViewModel.getId() != null) {
            return ResponseEntity.badRequest().body(null);
        }

        FlightSchedule flightSchedule = flightScheduleMapper.toModel(flightScheduleViewModel);
  
        if (flightSchedule.getEndDate() != null && flightSchedule.getStartDate().isAfter(flightSchedule.getEndDate())) {
            throw new CustomParametrizedException("Start date is after ene date");
        }
            
        FlightSchedule flightScheduleCreate = flightScheduleService.create(flightSchedule);
        FlightScheduleViewModel result = flightScheduleMapper.toViewModel(flightScheduleCreate);

        return ResponseEntity.created(new URI("/api/flight-schedules/" + result.getId())).body(result);
    }

    @PreAuthorize("hasAuthority('flight_schedule_modify')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteFlightSchedule(@PathVariable final Integer id) {
        LOG.debug("REST request to delete FlightSchedule : {}", id);
        flightScheduleService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<?> getFlightSchedules(
        @RequestParam(name = "search", required = false) final String textSearch,
        @RequestParam(name = "accountId", required = false) final Integer accountId,
        @SortDefault.SortDefaults({
            @SortDefault(sort = { "account" }, direction = Sort.Direction.ASC),
            @SortDefault(sort = { "flightServiceNumber" }, direction = Sort.Direction.ASC)
        }) Pageable pageable,
        @RequestParam(name = "csvExport", required = false) Boolean csvExport) {

        LOG.debug("REST request to get flightSchedules, accountId = {}, search = {}", accountId, textSearch);

        final Page<FlightSchedule> page = flightScheduleService.findAll(accountId, textSearch, pageable);

        if (csvExport != null && csvExport) {
            final List<FlightSchedule> list = page.getContent();
            final List<FlightScheduleCsvExportModel> csvExportModel = flightScheduleMapper.toCsvModel(list);
            ReportDocument report = reportDocumentCreator.createCsvDocument("Flight_Schedules", csvExportModel, FlightScheduleCsvExportModel.class, true);
            return doCreateBinaryResponse(report);
        } else {
            List<FlightScheduleViewModel> viewModels = flightScheduleMapper.toViewModel(page);
            flightScheduleService.addMissingAndUnexpectedFlights(viewModels);
            final Page<FlightScheduleViewModel> resultPage = new PageImplCustom<>(viewModels, pageable, page.getTotalElements(), flightScheduleService.countAll());
            return ResponseEntity.ok().body(resultPage);
        }
    }

    @GetMapping(value = "/account/{accountId}")
    public ResponseEntity<Page<FlightScheduleViewModel>> getAllFlightScheduleByAccount(
        @RequestParam(name = "search", required = false) final String textSearch,
        @PathVariable Integer accountId,
        @SortDefault.SortDefaults({
            @SortDefault(sort = { "account" }, direction = Sort.Direction.ASC),
            @SortDefault(sort = { "flightServiceNumber" }, direction = Sort.Direction.ASC)
        }) Pageable pageable
    ) {
        LOG.debug("REST request to get all FlightSchedule for account {}", accountId);
        final Page<FlightSchedule> page = flightScheduleService.findAll(accountId, textSearch, pageable);
        final Page<FlightScheduleViewModel> resultPage = new PageImpl<>(flightScheduleMapper.toViewModel(page),
                pageable, page.getTotalElements());
        return ResponseEntity.ok().body(resultPage);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<FlightScheduleViewModel> getFlightSchedule(@PathVariable Integer id) {
        LOG.debug("REST request to get FlightSchedule : {}", id);
        FlightSchedule flightSchedule = flightScheduleService.findOne(id);
        return Optional.ofNullable(flightSchedule)
                .map(result -> new ResponseEntity<>(flightScheduleMapper.toViewModel(result), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasAuthority('flight_schedule_modify')")
    @PutMapping(value = "/pre-upload", headers = ("content-type=multipart/*"), consumes = "multipart/form-data")
    public ResponseEntity<List<UploadReportViewModel>> preUpload(
        @RequestParam("file") final MultipartFile file,
        @RequestParam(value = "startDate")@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime startDate) {

        LOG.debug("REST request to pre upload FlightSchedule file");
        if (file.getContentType() == null) {
            return ResponseEntity.badRequest().body(null);
        }
        List<FlightScheduleCsvViewModel> scheduled;
        try {
            scheduled = dataImportService.parseFromMultipartFile(file, DataImportService.STRATEGY.BIND_BY_POSITION,
                    FlightScheduleCsvViewModel.class);
            scheduled.forEach(x -> x.setStartDate(startDate));
        } catch (Exception ex) {
            LOG.error("Cannot read the file {} because: {}", file.getOriginalFilename(), ex.getMessage());
            throw new CustomParametrizedException("Invalid file", ex, file.getOriginalFilename());
        }

        //noinspection unchecked
        final List<UploadReportViewModel> summary = loaderService.checkBulkUpload(scheduled);
        return ResponseEntity.ok().body(summary);
    }

    @PreAuthorize("hasAuthority('flight_schedule_modify')")
    @PutMapping(value = "/{id}")
    public ResponseEntity<FlightScheduleViewModel> updateFlightSchedule(@PathVariable final Integer id,
                                                                        @Valid @RequestBody final FlightScheduleViewModel flightScheduleViewModel) {
        LOG.debug("REST request to update FlightSchedule : {}", flightScheduleViewModel);
        FlightSchedule flightSchedule = flightScheduleMapper.toModel(flightScheduleViewModel);

        if (flightSchedule.getEndDate() != null && flightSchedule.getStartDate().isAfter(flightSchedule.getEndDate())) {
            throw new CustomParametrizedException("Start date is after ene date");
        }

        FlightSchedule flightScheduleUpdate = flightScheduleService.update(id, flightSchedule);
        FlightScheduleViewModel result = flightScheduleMapper.toViewModel(flightScheduleUpdate);

        return ResponseEntity.ok().body(result);
    }

    @PreAuthorize("hasAuthority('flight_schedule_modify')")
    @PutMapping(value = "/upload", headers = ("content-type=multipart/*"), consumes = "multipart/form-data")
    public ResponseEntity<UploadReportParsedItems> upload(@RequestParam("file") final MultipartFile file,
                                                          @RequestParam(value = "startDate")
                                                          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime startDate) {

        LOG.debug("REST request to upload FlightSchedule file");
        if (file.getContentType() == null) {
            return ResponseEntity.badRequest().body(null);
        }
        List<FlightScheduleCsvViewModel> scheduled;
        try {
            scheduled = dataImportService.parseFromMultipartFile(file, DataImportService.STRATEGY.BIND_BY_POSITION,
                    FlightScheduleCsvViewModel.class);
            scheduled.forEach(x -> x.setStartDate(startDate));
        } catch (Exception ex) {
            LOG.error("Cannot read the file {} because: {}", file.getOriginalFilename(), ex.getMessage());
            throw new CustomParametrizedException("Invalid file", ex, file.getOriginalFilename());
        }

        // noinspection unchecked
        final List<UploadReportViewModel> summary = loaderService.reportBulkUpload(scheduled, file);

        return ResponseEntity.ok().body(UploadReportParsedItems.createInstance(scheduled, summary));
    }
}
