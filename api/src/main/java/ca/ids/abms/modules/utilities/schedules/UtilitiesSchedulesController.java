package ca.ids.abms.modules.utilities.schedules;

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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/utilities-schedules")
@SuppressWarnings({"unused", "squid:S1452"})
public class UtilitiesSchedulesController extends MediaDocumentComponent {

    private static final Logger LOG = LoggerFactory.getLogger(UtilitiesSchedulesController.class);

    private final UtilitiesScheduleService utilitiesScheduleService;
    private final UtilitiesSchedulerMapper utilitiesScheduleMapper;
    private final ReportDocumentCreator reportDocumentCreator;

    public UtilitiesSchedulesController(final UtilitiesScheduleService utilitiesScheduleService,
                                        final UtilitiesSchedulerMapper utilitiesScheduleMapper,
                                        final ReportDocumentCreator reportDocumentCreator) {
        this.utilitiesScheduleService = utilitiesScheduleService;
        this.utilitiesScheduleMapper = utilitiesScheduleMapper;
        this.reportDocumentCreator = reportDocumentCreator;
    }

    @GetMapping
    public ResponseEntity<?> getAllUtilitiesScheduler(@RequestParam(value="filter", required = false) final ScheduleType filter,
                                                      @SortDefault(sort = {"scheduleId"}, direction = Sort.Direction.ASC) final Pageable pageable,
                                                      @RequestParam(name = "textFilter", required = false) final String textSearch,
                                                      @RequestParam(name = "csvExport", required = false) Boolean csvExport) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("REST request to get all schedules filtered by {}", filter != null ? filter : "nothing");
        }
        final Page<UtilitiesSchedule> page = utilitiesScheduleService.getAllUtilitiesSchedule(pageable, filter, textSearch);

        if (csvExport != null && csvExport) {
            final List<UtilitiesSchedule> list = page.getContent();
            final List<UtilitiesScheduleCsvExportModel> csvExportModel = utilitiesScheduleMapper.toCsvModel(list);
            ReportDocument report = reportDocumentCreator.createCsvDocument("Utilities_Schedules", csvExportModel,
                UtilitiesScheduleCsvExportModel.class, true);
            return doCreateBinaryResponse(report);
        } else {
            final Page<UtilitiesScheduleViewModel> resultPage = new PageImplCustom<>(
                utilitiesScheduleMapper.toViewModel(page), pageable, page.getTotalElements(), utilitiesScheduleService.countAll());
            return ResponseEntity.ok().body(resultPage);
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<UtilitiesScheduleViewModel> getUtilitiesScheduler(@PathVariable Integer id) {
        final UtilitiesSchedule schedule = utilitiesScheduleService.getOneUtilitiesSchedule(id);
        final UtilitiesScheduleViewModel scheduleDto = utilitiesScheduleMapper.toViewModel(schedule);
        return Optional.ofNullable(scheduleDto)
                .map(result -> new ResponseEntity<>(
                        result,
                        HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasAuthority('utilities_schedule_modify')")
    @PostMapping
    public ResponseEntity<UtilitiesScheduleViewModel> createUtilitiesSchedule(
            @RequestBody UtilitiesScheduleViewModel utilitiesScheduleDto) throws URISyntaxException {
        if (utilitiesScheduleDto.getScheduleId() != null) {
            return ResponseEntity.badRequest().body(null);
        }
        final UtilitiesSchedule scheduleToCreate = utilitiesScheduleMapper.toModel(utilitiesScheduleDto);
        final UtilitiesSchedule createdSchedule = utilitiesScheduleService.createUtilitiesSchedule(scheduleToCreate);
        final UtilitiesScheduleViewModel createdScheduleDto = utilitiesScheduleMapper.toViewModel(createdSchedule);
        return ResponseEntity.created(new URI("/api/utilities-schedules/" + createdSchedule.getScheduleId()))
                .body(createdScheduleDto);
    }

    @PreAuthorize("hasAuthority('utilities_schedule_modify')")
    @PutMapping(value = "/{id}")
    public ResponseEntity<UtilitiesScheduleViewModel> updateUtilitiesSchedule(
            @PathVariable Integer id, @RequestBody UtilitiesScheduleViewModel utilitiesScheduleDto){
        final UtilitiesSchedule scheduleToUpdate = utilitiesScheduleMapper.toModel(utilitiesScheduleDto);
        final UtilitiesSchedule updatedSchedule = utilitiesScheduleService
                .updateUtilitiesSchedule(id, scheduleToUpdate);
        final UtilitiesScheduleViewModel updatedScheduleDto = utilitiesScheduleMapper.toViewModel(updatedSchedule);
        return ResponseEntity.ok().body(updatedScheduleDto);
    }

    @PreAuthorize("hasAuthority('utilities_schedule_modify')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Integer id) {
        utilitiesScheduleService.deleteUtilitiesSchedule(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{id}/range-brackets")
    public ResponseEntity<Collection<UtilitiesRangeBracketViewModel>> getAllRangeBrackets(@PathVariable Integer id) {
        LOG.debug("REST request to get all range brackets by schedule id {} ", id);
        final Collection<UtilitiesRangeBracket> page = utilitiesScheduleService.getAllUtilitiesRangeBracketByScheduleId(id);
        final Collection<UtilitiesRangeBracketViewModel> results = utilitiesScheduleMapper.toViewModel(page);
        return Optional.ofNullable(results)
                .map(result -> new ResponseEntity<>(
                        result,
                        HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping(value = "/range-brackets/{bracketId}")
    public ResponseEntity<UtilitiesRangeBracketViewModel> getOneRangeBracket(@PathVariable Integer bracketId) {
        LOG.debug("REST request to get the range bracket {}", bracketId);
        final UtilitiesRangeBracket rangeBracket = utilitiesScheduleService.getOneUtilitiesRangeBracket(bracketId);
        final UtilitiesRangeBracketViewModel rangeBracketDto = utilitiesScheduleMapper.toViewModel(rangeBracket);
        return Optional.ofNullable(rangeBracketDto)
                .map(result -> new ResponseEntity<>(
                        result,
                        HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasAuthority('utilities_schedule_modify')")
    @PostMapping(value = "/{id}/range-brackets")
    public ResponseEntity<UtilitiesRangeBracketViewModel> createRangeBracket(@PathVariable Integer id,
            @RequestBody UtilitiesRangeBracketViewModel rangeBracketDto) throws URISyntaxException {
        if (rangeBracketDto.getId() != null) {
            return ResponseEntity.badRequest().body(null);
        }
        final UtilitiesRangeBracket bracketToCreate = utilitiesScheduleMapper.toModel(rangeBracketDto);
        final UtilitiesRangeBracket createdBracket = utilitiesScheduleService.createRangeBrackets(id, bracketToCreate);
        final UtilitiesRangeBracketViewModel createdBracketDto = utilitiesScheduleMapper.toViewModel(createdBracket);
        return ResponseEntity.created(new URI("/api/utilities-schedules/range-brackets/" + createdBracketDto.getId()))
                .body(createdBracketDto);
    }

    @PreAuthorize("hasAuthority('utilities_schedule_modify')")
    @PutMapping(value = "/range-brackets/{id}")
    public ResponseEntity<UtilitiesRangeBracketViewModel> updateRangeBracket(
            @PathVariable Integer id, @RequestBody UtilitiesRangeBracketViewModel bracketDto) {
        final UtilitiesRangeBracket bracketToUpdate = utilitiesScheduleMapper.toModel(bracketDto);
        final UtilitiesRangeBracket updatedBracket = utilitiesScheduleService
                .updateRangeBrackets(id, bracketToUpdate);
        final UtilitiesRangeBracketViewModel updatedBracketDto = utilitiesScheduleMapper.toViewModel(updatedBracket);
        return ResponseEntity.ok().body(updatedBracketDto);
    }

    @PreAuthorize("hasAuthority('utilities_schedule_modify')")
    @DeleteMapping(value = "/range-brackets/{id}")
    public ResponseEntity<Void> deleterangeBracket(@PathVariable Integer id) {
        utilitiesScheduleService.deleteRangeBrackets(id);
        return ResponseEntity.ok().build();
    }
}
