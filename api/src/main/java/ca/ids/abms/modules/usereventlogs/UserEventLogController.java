package ca.ids.abms.modules.usereventlogs;

import ca.ids.abms.modules.common.controllers.MediaDocumentComponent;
import ca.ids.abms.modules.reports2.common.ReportDocument;
import ca.ids.abms.modules.reports2.common.ReportDocumentCreator;
import ca.ids.abms.modules.util.models.PageImplCustom;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-event-logs")
@SuppressWarnings({"unused", "squid:S1452"})
public class UserEventLogController extends MediaDocumentComponent {

    private static final Logger LOG = LoggerFactory.getLogger(UserEventLogController.class);

    private UserEventLogService userEventLogService;
    private UserEventLogMapper userEventLogMapper;
    private final ReportDocumentCreator reportDocumentCreator;

    public UserEventLogController(final UserEventLogService aUserEventLogService,
                                  final UserEventLogMapper aUserEventLogMapper,
                                  final ReportDocumentCreator reportDocumentCreator) {
        userEventLogService = aUserEventLogService;
        userEventLogMapper = aUserEventLogMapper;
        this.reportDocumentCreator = reportDocumentCreator;
    }

    @GetMapping
    public ResponseEntity<?> findAllEventLogsByFilters(@RequestParam(value = UserEventLogQuery.FILTER_BY_TEXT_SEARCH, required = false) String textSearch,
                                                       @SortDefault.SortDefaults({
                                                           @SortDefault(sort = {"dateTime"}, direction = Sort.Direction.DESC),}) Pageable pageable,
                                                       @RequestParam(name = "csvExport", required = false) Boolean csvExport) {
        final Page<UserEventLog> page = userEventLogService.findAllEventLogsByFilters(textSearch, pageable);

        if (csvExport != null && csvExport) {
            final List<UserEventLog> list = page.getContent();
            final List<UserEventLogCsvExportModel> csvExportModel = userEventLogMapper.toCsvModel(list);
            ReportDocument report = reportDocumentCreator.createCsvDocument("User_Event_Logs", csvExportModel, UserEventLogCsvExportModel.class, true);
            return doCreateBinaryResponse(report);
        } else {
            final Page<UserEventLogViewModel> resultPage = new PageImplCustom<>(userEventLogMapper.toViewModel(page), pageable,
                page.getTotalElements(), userEventLogService.countAll());
            return ResponseEntity.ok().body(resultPage);
        }
    }

    @PreAuthorize("hasAuthority('user_event_modify')")
    @PutMapping(value = "/{id}")
    public ResponseEntity<UserEventLogViewModel> updateUserEventLog(@RequestBody UserEventLogViewModel userEventLog,
                                                                    @PathVariable Integer id) {
        LOG.debug("REST request to update User Event Log : {}", userEventLog);

        UserEventLog result = userEventLogService.update(id, userEventLogMapper.toModel(userEventLog));
        UserEventLogViewModel viewModel = userEventLogMapper.toViewModel(result);
        return ResponseEntity.ok().body(viewModel);
    }

    @PreAuthorize("hasAuthority('user_event_modify')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteUserEventLog(@PathVariable Integer id) {
        LOG.debug("REST request to delete User Event Log : {}", id);

        userEventLogService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<Void> createEvent(@RequestBody UserEventLogViewModel userEventLog,
                                            HttpServletRequest request) {
        LOG.debug("REST request to create an event");
        userEventLog.setIpAddress(userEventLogService.getIpAddressFromRequest(request));
        userEventLogService.save(userEventLogMapper.toModel(userEventLog));

        return ResponseEntity.ok().build();
    }
}


