package ca.ids.abms.modules.cachedevents;

import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.modules.common.controllers.MediaDocumentComponent;
import ca.ids.abms.modules.reports2.common.ReportDocument;
import ca.ids.abms.modules.reports2.common.ReportDocumentCreator;
import ca.ids.abms.modules.util.models.PageImplCustom;
import ca.ids.spring.cache.exceptions.RetryCycleLockException;
import org.hibernate.StaleStateException;
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

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("api/cachedevents")
@SuppressWarnings({"unused", "squid:S1452"})
public class CachedEventController extends MediaDocumentComponent {

    private static final Logger LOG = LoggerFactory.getLogger(CachedEventController.class);

    private final CachedEventManager cachedEventManager;
    private final CachedEventService cachedEventService;
    private final CachedEventMapper cachedEventMapper;
    private final ReportDocumentCreator reportDocumentCreator;

    public CachedEventController(final CachedEventManager cachedEventManager,
                                 final CachedEventService cachedEventService,
                                 final CachedEventMapper cachedEventMapper,
                                 final ReportDocumentCreator reportDocumentCreator) {
        this.cachedEventManager = cachedEventManager;
        this.cachedEventService = cachedEventService;
        this.cachedEventMapper = cachedEventMapper;
        this.reportDocumentCreator = reportDocumentCreator;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('manage_cached_event_view')")
    public ResponseEntity<?> get(@RequestParam(name = "search", required = false) final String search,
                                 @SortDefault.SortDefaults({@SortDefault(sort = {"createdAt"}, direction= Sort.Direction.DESC)}) Pageable pageable,
                                 @RequestParam(name = "csvExport", required = false) Boolean csvExport) {
        LOG.debug("REST request to get all cached events.");
        Page<CachedEvent> page = cachedEventService.findAll(search, pageable);

        if (csvExport != null && csvExport) {
            final List<CachedEvent> list = page.getContent();
            final List<CachedEventCsvExportModel> csvExportModel = cachedEventMapper.toCsvModel(list);
            ReportDocument report = reportDocumentCreator.createCsvDocument("Cached_Events", csvExportModel,
                CachedEventCsvExportModel.class, true);
            return doCreateBinaryResponse(report);
        } else {
            return new ResponseEntity<>(new PageImplCustom<>(page.getContent(), pageable,
                page.getTotalElements(), cachedEventService.countAll()), HttpStatus.OK);
        }
    }

    @GetMapping("/nextretrycycle")
    @PreAuthorize("hasAuthority('manage_cached_event_view')")
    public ResponseEntity<Date> nextRetryCycle() {
        LOG.debug("REST request to get next retry cycle date.");
        return ResponseEntity.ok().body(cachedEventManager.nextRetryCycle());
    }

    @PreAuthorize("hasAuthority('manage_cached_event_modify')")
    @PutMapping("/{id}/retry")
    public ResponseEntity retry(@PathVariable Integer id) {
        LOG.debug("REST request to retry cached event with id {}.", id);
        try {
            return ResponseEntity.ok().body(cachedEventManager.retry(id));
        } catch (StaleStateException ex) {
            throw new CustomParametrizedException(ErrorConstants.ERR_CONCURRENCY_FAILURE, ex);
        } catch (RetryCycleLockException ex) {
            throw new CustomParametrizedException(ErrorConstants.ERR_RETRY_CYCLE_LOCK);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('manage_cached_event_modify')")
    public ResponseEntity delete(@PathVariable Integer id) {
        LOG.debug("REST request to delete cached event with id {}.", id);
        cachedEventService.remove(id);
        return ResponseEntity.ok().body(null);
    }
}
