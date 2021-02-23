package ca.ids.abms.modules.radarsummary;

import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.config.error.ExceptionFactory;
import ca.ids.abms.modules.common.controllers.BulkLoaderComponent;
import ca.ids.abms.modules.common.dto.BulkLoaderSummary;
import ca.ids.abms.modules.flightmovementsbuilder.FlightMovementBuilderException;
import ca.ids.abms.modules.radarsummary.enumerators.RadarSummaryFormat;
import ca.ids.abms.modules.reports2.common.ReportControllerBase;
import ca.ids.abms.modules.reports2.common.ReportDocument;
import ca.ids.abms.modules.reports2.common.ReportDocumentCreator;
import ca.ids.abms.modules.util.models.PageImplCustom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@RequestMapping("/api/radar-summaries")
@SuppressWarnings({"unused", "squid:S1452"})
public class RadarSummaryController extends ReportControllerBase implements BulkLoaderComponent {

    private static final Logger LOG = LoggerFactory.getLogger(RadarSummaryController.class);

    private static final String PARAM_FORMAT = "format";
    private static final String PARAM_MERGE = "merge";

    private final RadarSummaryImporter radarSummaryImporter;
    private final RadarSummaryMapper radarSummaryMapper;
    private final RadarSummaryService radarSummaryService;
    private final ReportDocumentCreator reportDocumentCreator;

    public RadarSummaryController(final RadarSummaryImporter radarSummaryImporter,
                                  final RadarSummaryMapper radarSummaryMapper,
                                  final RadarSummaryService radarSummaryService,
                                  final ReportDocumentCreator reportDocumentCreator) {
        this.radarSummaryImporter = radarSummaryImporter;
        this.radarSummaryMapper = radarSummaryMapper;
        this.radarSummaryService = radarSummaryService;
        this.reportDocumentCreator = reportDocumentCreator;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('radar_summary_modify')")
    public ResponseEntity<RadarSummaryViewModel> createRadarSummary(@Valid @RequestBody final RadarSummaryViewModel radarSummaryViewModel) throws URISyntaxException {
        LOG.debug("REST request to save RadarSummary : {}", radarSummaryViewModel);
        try {
            RadarSummary radarSummary = radarSummaryMapper.toModel(radarSummaryViewModel);
            RadarSummary radarSummaryCreate = radarSummaryService.create(radarSummary);
            RadarSummaryViewModel result = radarSummaryMapper.toViewModel(radarSummaryCreate);

            return ResponseEntity.created(new URI("/api/radar-summaries/" + result.getId()))
                .body(result);

        } catch(FlightMovementBuilderException e){
            throw ExceptionFactory.getInvalidDataException(e.getFlightMovementBuilderIssue().toValue(), RadarSummary.class);
        }
    }

    @PreAuthorize("hasAuthority('radar_summary_modify')")
    @PutMapping(value = "/{id}")
    public ResponseEntity<RadarSummaryViewModel> updateRadarSummary(@RequestBody final RadarSummaryViewModel radarSummaryViewModel,
                                                                    @PathVariable final Integer id) {
        LOG.debug("REST request to update RadarSummary : {}", radarSummaryViewModel);
        try {
            RadarSummary radarSummary = radarSummaryMapper.toModel(radarSummaryViewModel);
            RadarSummary radarSummaryUpdate = radarSummaryService.update(id, radarSummary);
            RadarSummaryViewModel result = radarSummaryMapper.toViewModel(radarSummaryUpdate);

            return ResponseEntity.ok().body(result);
        } catch(FlightMovementBuilderException e){
            throw ExceptionFactory.getInvalidDataException(e.getFlightMovementBuilderIssue().toValue(), RadarSummary.class);
        }
    }

    @PreAuthorize("hasAuthority('radar_summary_modify')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteRadarSummary(@PathVariable final Integer id) {
        LOG.debug("REST request to delete RadarSummary : {}", id);
        radarSummaryService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Page<RadarSummaryViewModel>> getAllRadarSummary(
            @SortDefault.SortDefaults({
                @SortDefault(sort = {"dayOfFlight"}, direction = Sort.Direction.DESC),
                @SortDefault(sort = {"departureTime"}, direction = Sort.Direction.DESC),
                @SortDefault(sort = {"flightIdentifier"}, direction = Sort.Direction.ASC),
                @SortDefault(sort = {"id"},direction = Sort.Direction.DESC)
            })  Pageable pageable) {
        LOG.debug("REST request to get all RadarSummary");
        final Page<RadarSummary> page = radarSummaryService.findAll(pageable);
        final Page<RadarSummaryViewModel> resultPage= new PageImplCustom<>(radarSummaryMapper.toViewModel(page),pageable,page.getTotalElements(),
            radarSummaryService.countAll());
        return ResponseEntity.ok().body(resultPage);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<RadarSummaryViewModel> getRadarSummary(@PathVariable final Integer id) {
        LOG.debug("REST request to get RadarSummary : {}", id);

        RadarSummary radarSummary = radarSummaryService.findOne(id);

        return Optional.ofNullable(radarSummary)
            .map(result -> new ResponseEntity<>(radarSummaryMapper.toViewModel(result), HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasAuthority('radar_summary_modify')")
    @PutMapping(value = "/upload", headers = ("content-type=multipart/*"), consumes = "multipart/form-data")
    public ResponseEntity<BulkLoaderSummary> upload (
        @RequestParam("file") final MultipartFile multipartFile,
        @RequestParam(required = false) final Map<String, String> params) {

        // validate that content type is not null
        if (multipartFile.getContentType() == null) {
            return ResponseEntity.badRequest().body(null);
        }

        // set format type from params, default to 'RAYTHEON-A' if not found
        RadarSummaryFormat format = params == null || params.get(PARAM_FORMAT) == null || params.get(PARAM_FORMAT).isEmpty()
            ? RadarSummaryFormat.RAYTHEON_A
            : RadarSummaryFormat.forName(params.get(PARAM_FORMAT));

        // US100976: set waypoints merge flag from params, default true
        Boolean mergeWaypoints = params == null || params.get(PARAM_MERGE) == null || params.get(PARAM_MERGE).isEmpty()
            ? Boolean.TRUE : Boolean.valueOf(params.get(PARAM_MERGE));

        try {

            // upload file based on provided format to parse the file
            final BulkLoaderSummary summary = radarSummaryImporter.upload(multipartFile, format, mergeWaypoints);

            // return summary of bulk load
            return ResponseEntity.ok().body(summary);

        } catch (Exception ex) {
            LOG.error("Cannot read the file {} because: {}", multipartFile.getOriginalFilename(), ex.getMessage());
            throw new CustomParametrizedException("Invalid file", ex, multipartFile.getOriginalFilename());
        }
    }

    @GetMapping(value = "/filters")
    public ResponseEntity<?> findAllRadarSummaryByFilter(
        @RequestParam(name = "search", required = false) final String textSearch,
        @SortDefault.SortDefaults({
            @SortDefault(sort = {"dayOfFlight"}, direction=Sort.Direction.DESC),
            @SortDefault(sort = {"flightIdentifier"}, direction = Sort.Direction.ASC)})  Pageable pageable,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate start,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate end,
        @RequestParam(name = "csvExport", required = false) Boolean csvExport) {

        LOG.debug("REST request to get All RadarSummary; textSearch: {} startDate: {} endDate: {}", textSearch, start, end);

        final Page<RadarSummary> page = radarSummaryService.findAllRadarSummaryByFilter(pageable, textSearch, start, end);

        if (csvExport != null && csvExport) {
            final List<RadarSummary> list = page.getContent();
            final List<RadarSummaryCsvExportModel> csvExportModel = radarSummaryMapper.toCsvModel(list);
            ReportDocument report = reportDocumentCreator.createCsvDocument("Radar_Summary", csvExportModel, RadarSummaryCsvExportModel.class, true);
            return doCreateBinaryResponse(report);
        } else {
            final Page<RadarSummaryViewModel> resultPage = new PageImplCustom<>(radarSummaryMapper.toViewModel(page), pageable,
                page.getTotalElements(), radarSummaryService.countAll());
            return ResponseEntity.ok().body(resultPage);
        }
    }
}
