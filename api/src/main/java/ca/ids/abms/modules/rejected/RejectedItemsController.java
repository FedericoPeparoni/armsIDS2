package ca.ids.abms.modules.rejected;

import ca.ids.abms.config.error.ExceptionFactory;
import ca.ids.abms.modules.common.controllers.MediaDocumentComponent;
import ca.ids.abms.modules.reports2.common.ReportDocument;
import ca.ids.abms.modules.reports2.common.ReportDocumentCreator;
import ca.ids.abms.modules.util.models.PageImplCustom;
import ca.ids.abms.util.billingcontext.BillingContext;
import ca.ids.abms.util.billingcontext.BillingContextKey;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/rejected-items")
@SuppressWarnings({"unused", "squid:S1452"})
public class RejectedItemsController extends MediaDocumentComponent {

    private static final Logger LOG = LoggerFactory.getLogger(RejectedItemsController.class);

    private final RejectedItemService rejectedItemService;
    private final RejectedItemMapper rejectedItemMapper;
    private final RejectedItemHandlerChain rejectedItemHandlerChain;
    private final ReportDocumentCreator reportDocumentCreator;

    public RejectedItemsController (final RejectedItemService rejectedItemService,
                                    final RejectedItemMapper rejectedItemMapper,
                                    final RejectedItemHandlerChain rejectedItemHandlerChain,
                                    final ReportDocumentCreator reportDocumentCreator) {
        this.rejectedItemService = rejectedItemService;
        this.rejectedItemMapper = rejectedItemMapper;
        this.rejectedItemHandlerChain = rejectedItemHandlerChain;
        this.reportDocumentCreator = reportDocumentCreator;
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<RejectedItemViewModel> getOne (@PathVariable final Integer id) {
        LOG.debug("REST request to get a rejected item with ID: {}", id);

        RejectedItemViewModel item = rejectedItemMapper.toViewModel(rejectedItemService.getOne(id));

        return Optional.ofNullable(item).map(result -> new ResponseEntity<>(result, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<?> findAllRejectedItemsByFilters (
        @RequestParam(value = RejectedItemQuery.FILTER_BY_RECORD_TYPE, required = false) String filterByRecordType,
        @RequestParam(value = RejectedItemQuery.FILTER_BY_TEXT_SEARCH, required = false) String textSearch,
        @RequestParam(value = RejectedItemQuery.FILTER_BY_REJECTED_START_DATE, required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate startDateFilter,
        @RequestParam(value = RejectedItemQuery.FILTER_BY_REJECTED_END_DATE, required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate endDateFilter,
        @RequestParam(value = RejectedItemQuery.FILTER_BY_STATUS, required = false) String filterByStatus,
        @RequestParam(value = RejectedItemQuery.FILTER_BY_ORIGINATOR, required = false) String filterByOriginator,
        @RequestParam(value = RejectedItemQuery.FILTER_BY_FILE_NAME, required = false) String filterByFileName,
        @SortDefault.SortDefaults({@SortDefault(sort = {"rejectedDateTime"}, direction = Sort.Direction.DESC),}) Pageable pageable,
        @RequestParam(name = "csvExport", required = false) Boolean csvExport) {

        LOG.debug("REST request to get all Rejected Items with filters: textSearch: {} startDate: {} endDate: {} recordType: {} status: {} originator: {} fileName: {}" ,
            textSearch, startDateFilter, endDateFilter, filterByRecordType, filterByStatus, filterByOriginator, filterByFileName);

        final Page<RejectedItem> page = rejectedItemService.findAllRejectedItemsByFilters(textSearch, filterByRecordType,
            startDateFilter, endDateFilter, filterByStatus, filterByOriginator, filterByFileName, pageable);

        if (csvExport != null && csvExport) {
            final List<RejectedItem> list = page.getContent();
            final List<RejectedItemCsvExportModel> csvExportModel = rejectedItemMapper.toCsvModel(list);
            ReportDocument report = reportDocumentCreator.createCsvDocument("Rejected_Items", csvExportModel, RejectedItemCsvExportModel.class, true);
            return doCreateBinaryResponse(report);
        } else {
            final Page<RejectedItemViewModel> resultPage = new PageImplCustom<>(rejectedItemMapper.toViewModel(page), pageable,
                page.getTotalElements(), rejectedItemService.countAll());
            return ResponseEntity.ok().body(resultPage);
        }
    }

    @PreAuthorize("hasAuthority('rejected_data_modify')")
    @PutMapping(value = "/{id}")
    public ResponseEntity<RejectedItemViewModel> fix (@RequestBody final RejectedItemViewModel itemDto,
                                                     @PathVariable final Integer id,
                                                     @RequestParam(value = RejectedItemQuery.UPDATE_MERGE_UPLOAD, required = false) final Boolean merge) {
        LOG.debug("REST request to fix a rejected item: {}", itemDto);

        try {

            // define merge waypoint billing context, always clear at end of block
            BillingContext.put(BillingContextKey.MERGE_WAYPOINTS, merge);

            // fix rejected item and return updated result
            RejectedItem updatedItem = rejectedItemHandlerChain.fixRejectedItem(id, itemDto);
            return ResponseEntity.ok().body(rejectedItemMapper.toViewModel(updatedItem));

        } finally {

            // must always clear the billing context when used
            BillingContext.clear();
        }
    }

    @GetMapping(value = "/parse/{id}")
    public ResponseEntity<RejectedItemViewModel> parseOne (@PathVariable final Integer id,
                                                          @RequestBody final RejectedItemViewModel itemDto) {
        LOG.debug("REST request to parse a raw text into a json text for the rejected item with ID: {}", id);
        if (itemDto.getRawText() == null) {
            throw ExceptionFactory.getInvalidDataException("Empty field", RejectedItem.class, "jsonText");
        }
        final byte[] jsonText = rejectedItemHandlerChain.getJsonText(id, itemDto.getRawText());
        itemDto.setJsonText(jsonText);
        return ResponseEntity.ok().body(itemDto);
    }

    @PreAuthorize("hasAuthority('rejected_data_modify')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete (@PathVariable final Integer id) {
        LOG.debug("REST request to delete a rejected item with ID: {}", id);
        rejectedItemService.delete(id);
        return ResponseEntity.ok().build();
    }
}
