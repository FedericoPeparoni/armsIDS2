package ca.ids.abms.modules.billings;

import ca.ids.abms.config.AttachmentsConfig;
import ca.ids.abms.config.error.ErrorDTO;
import ca.ids.abms.config.error.ExceptionFactory;
import ca.ids.abms.modules.billings.utility.BillingLedgerExportUtility;
import ca.ids.abms.modules.common.controllers.MediaDocumentComponent;
import ca.ids.abms.modules.common.enumerators.InvoiceType;
import ca.ids.abms.modules.reports2.common.ReportDocument;
import ca.ids.abms.modules.reports2.common.ReportDocumentCreator;
import ca.ids.abms.modules.util.files.FileHelper;
import ca.ids.abms.modules.util.models.PageImplCustom;
import org.apache.poi.util.IOUtils;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * NOTE: BillingLedger can't be updated from UI. Update method has been removed.
 */
@RestController
@RequestMapping("/api/billing-ledgers")
@SuppressWarnings({"unused", "squid:S1452"})
public class BillingLedgerController extends MediaDocumentComponent {

    private static final Logger LOG = LoggerFactory.getLogger(BillingLedgerController.class);
    private final BillingLedgerExportUtility billingLedgerExportUtility;
    private final BillingLedgerService billingLedgerService;
    private final BillingLedgerMapper billingLedgerMapper;
    private final InvoiceLineItemMapper invoiceLineItemMapper;
    private final AttachmentsConfig attachmentsConfig;
    private final ReportDocumentCreator reportDocumentCreator;

    public BillingLedgerController(final BillingLedgerExportUtility billingLedgerExportUtility,
                                   final BillingLedgerService billingLedgerService,
                                   final BillingLedgerMapper billingLedgerMapper,
                                   final InvoiceLineItemMapper invoiceLineItemMapper,
                                   final AttachmentsConfig attachmentsConfig,
                                   final ReportDocumentCreator reportDocumentCreator) {
        this.billingLedgerExportUtility = billingLedgerExportUtility;
        this.billingLedgerService = billingLedgerService;
        this.billingLedgerMapper = billingLedgerMapper;
        this.invoiceLineItemMapper = invoiceLineItemMapper;
        this.attachmentsConfig = attachmentsConfig;
        this.reportDocumentCreator = reportDocumentCreator;
    }

    @GetMapping
    @SuppressWarnings("squid:S00107")
    public ResponseEntity<?> getAllBillingLedgers (
        @RequestParam(name = "statusFilter", required = false) String statusFilter,
        @RequestParam(name = "search", required = false) final String textSearch,
        @RequestParam(name = "exportedFilter", required = false) Boolean exportedFilter,
        @RequestParam(name = "account", required = false) Integer accountId,
        @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate startDate,
        @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate endDate,
        @RequestParam(name = "dateOfIssue", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate dateOfIssue,
        @RequestParam(name = "flightIdOrRegistration", required = false) final String flightIdOrRegistration,
        @RequestParam(name = "createdByUserId", required = false) final Integer createdByUserId,
        @RequestParam(name = "billingCentre", required = false) Integer billingCentreId,
        @SortDefault(sort = {"invoiceDateOfIssue", "invoiceNumber"}, direction = Sort.Direction.DESC) Pageable pageable,
        @RequestParam(name = "csvExport", required = false) Boolean csvExport) {
        
        LOG.debug("REST request to get all billing ledgers");

        LocalDateTime startDateFilter = null;
        LocalDateTime endDateFilter = null;

        if (startDate != null) {
            startDateFilter = startDate.atStartOfDay();
        }

        if (endDate != null) {
            endDateFilter = endDate.atTime(LocalTime.MAX);
        }

        final Page<BillingLedger> page = billingLedgerService.findAll (statusFilter, textSearch, pageable, null, null, exportedFilter,
            accountId, startDateFilter, endDateFilter, dateOfIssue, flightIdOrRegistration, createdByUserId, billingCentreId);

        if (csvExport != null && csvExport) {
            final List<BillingLedger> list = page.getContent();
            final List<BillingLedgerCsvExportModel> csvExportModel = billingLedgerMapper.toCsvModel(list);
            ReportDocument report = reportDocumentCreator.createCsvDocument("Invoices", csvExportModel, BillingLedgerCsvExportModel.class, true);
            return doCreateBinaryResponse(report);
        } else {
            long countAll = billingLedgerService.countAllBillingLedgers();
            final Page<BillingLedgerViewModel> resultPage = new PageImplCustom<>(billingLedgerMapper.toViewModel(page), pageable, page.getTotalElements(), countAll);
            return ResponseEntity.ok().body(resultPage);
        }
    }

    @GetMapping(value = "/getUnpaidBillingLedgersByAccountAndCurrency")
    public Page<BillingLedgerViewModel> getUnpaidBillingLedgersByAccountAndCurrency(@RequestParam final Integer accountId,
                                                                                    @RequestParam final Integer currencyId,
                                                                                    @SortDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {
        LOG.debug("REST request to get unpaid billing ledgers filtered by account and currency");

        final Page<BillingLedger> page = billingLedgerService.getUnpaidBillingLedgersByAccountAndCurrency(accountId, currencyId, pageable);
        return new PageImpl<>(billingLedgerMapper.toViewModel(page), pageable, page.getTotalElements());
    }

    @GetMapping(value = "/getAllBillingLedgersByAccountAndCurrency")
    public Page<BillingLedgerViewModel> getAllBillingLedgersByAccountAndCurrency(@RequestParam final Integer accountId,
                                                                                 @RequestParam final Integer currencyId,
                                                                                 @SortDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {
        LOG.debug("REST request to get all billing ledgers filtered by account and currency");

        final Page<BillingLedger> page = billingLedgerService.getAllBillingLedgersByAccountAndCurrency(accountId, currencyId, pageable);
        return new PageImpl<>(billingLedgerMapper.toViewModel(page), pageable, page.getTotalElements());
    }

    @GetMapping(value = "/getTotalAmountForInvoicesByAccountIdAndCurrency")
    public double getTotalAmountForInvoicesByAccountIdAndCurrency(@RequestParam final Integer accountId,
                                                                  @RequestParam final Integer currencyId) {
        LOG.debug("REST request to get total amount for billing ledgers filtered by account and currency");
        Double amount = null;
        if (accountId != null && currencyId != null) {
            amount = billingLedgerService.getTotalAmountForInvoicesByAccountIdAndCurrency(accountId, currencyId);
        }
        return amount == null ? 0.0d : amount;
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<BillingLedgerViewModel> getBillingLedger(@PathVariable Integer id) {
        LOG.debug("REST request to get billing ledger : {}", id);

        BillingLedgerViewModel billingLedger = billingLedgerMapper.toViewModel(billingLedgerService.getOne(id));

        return Optional.ofNullable(billingLedger).map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping(value = "/{id}/upload", headers = ("content-type=multipart/*"))
    public ResponseEntity<BillingLedgerViewModel> upload(
        @PathVariable final Integer id, @RequestParam("file") final MultipartFile file
    ) throws URISyntaxException, IOException {
        if (file.getContentType() == null
            || !attachmentsConfig.getBillingLedgerDocuments().containsKey(file.getContentType())) {
            throw ExceptionFactory.getInvalidFileException(BillingLedger.class);
        }
        final BillingLedger template = billingLedgerService.uploadInvoiceDocument(id, file);
        return ResponseEntity.created(new URI("/api/billing-ledgers/" + template.getId() + "/document")).body(
            billingLedgerMapper.toViewModel(template));
    }

    /**
     * Marks Invoice as approved
     */
    @PreAuthorize("hasAuthority('invoices_approve')")
    @PutMapping("/{id}/approved")
    public ResponseEntity<BillingLedger> changeStateFromNewToApproved(@PathVariable Integer id) {
        return ResponseEntity.ok().body(billingLedgerService.approve(id));
    }

    /**
     * Marks Invoice as published
     */
    @PreAuthorize("hasAuthority('invoices_publish')")
    @PutMapping("/{id}/published")
    public ResponseEntity<BillingLedger> changeStateFromApprovedToPublished(@PathVariable Integer id) {
        return ResponseEntity.ok().body(billingLedgerService.approve(id));
    }

    /**
     * Marks Invoice as void
     */
    @PreAuthorize("hasAuthority('invoices_void')")
    @PutMapping("/{id}/void")
    public ResponseEntity<BillingLedger> changeStateToVoid(@PathVariable Integer id) {
        return ResponseEntity.ok().body(billingLedgerService.reject(id));
    }

    /**
     * Marks Invoice as void
     * For invoices created NOT from Point Of Sale - the state must be New or Approved;
     * For invoices created from Point Of Sale - possible to void only Published invoice
     */
    @PreAuthorize("hasAuthority('invoices_void')")
    @PutMapping("/{id}/void-published")
    public ResponseEntity<BillingLedger> changeStateFromPublishToVoid(@PathVariable Integer id) {
        return ResponseEntity.ok().body(billingLedgerService.rejectPublishedInvoice(id));
    }

    @GetMapping(value = "/{id}/download")
    public void download (@PathVariable final Integer id, final HttpServletResponse response) throws IOException {
        final BillingLedger billingLedger = billingLedgerService.getOne(id);
        if (billingLedger != null && billingLedger.getInvoiceDocument() != null) {
            try (ByteArrayInputStream is = new ByteArrayInputStream(billingLedger.getInvoiceDocument())) {
                response.addHeader("Content-disposition", "attachment;filename=\"" + FileHelper.doGetDownloadFileName(billingLedger.getInvoiceFileName()) + "\"");
                response.setContentType(FileHelper.doGetDownloadContentType(billingLedger.getInvoiceDocumentType()));
                response.setContentLength(billingLedger.getInvoiceDocument().length);
                IOUtils.copy(is, response.getOutputStream());
                response.flushBuffer();
            } catch (IOException ioe) {
                LOG.error("Cannot download the document with ID {}", id);
                throw ioe;
            }
        } else {
            String error = String.format("Document does not exist for billing ledger with ID %s", id);
            LOG.error(error);
            throw new ResourceNotFoundException(error);
        }
    }

    /**
     * @deprecated use `download` method
     */
    @Deprecated
    @GetMapping(value = "/{id}/document/json")
    public ResponseEntity<BillingLedgerViewModel> downloadEmbedded (@PathVariable final Integer id) {
        final BillingLedger billingLedger = billingLedgerService.getOne(id);
        final BillingLedgerViewModel billingLedgerDto = billingLedgerMapper.toViewModel(billingLedger);
        billingLedgerDto.setDocumentContents(billingLedger.getInvoiceDocument());
        return ResponseEntity.ok().body(billingLedgerDto);
    }

    @GetMapping(value = "/getLineItemsByInvoiceId/{id}")
    public ResponseEntity<?> getLineItemsByInvoiceId(@PathVariable final Integer id,
                                                     @RequestParam(name = "csvExport", required = false) Boolean csvExport) {

        LOG.debug("REST request to get line items for invoice number: {}", id);

        List<InvoiceLineItem> list = billingLedgerService.getLineItemsByInvoiceId(id);
        if (csvExport != null && csvExport) {
            final List<InvoiceLineItemCsvExportModel> csvExportModel = invoiceLineItemMapper.toCsvModel(list);
            ReportDocument report = reportDocumentCreator.createCsvDocument("Invoice_Line_Items", csvExportModel, InvoiceLineItemCsvExportModel.class, true);
            return doCreateBinaryResponse(report);
        } else {
            return ResponseEntity.ok().body(list);
        }
    }

    /**
     * Get if billing ledger exporting is supported.
     *
     * @return true if exporting supported
     */
    @GetMapping(value = "/export-support")
    public ResponseEntity<Boolean> getExportSupport() {
        LOG.debug("REST request to get export support");
        return ResponseEntity.ok().body(billingLedgerService.isExportSupport());
    }

    /**
     * Get if billing ledger exporting is supported for type.
     *
     * @param type billing ledger invoice type to check
     * @return true if exporting supported
     */
    @GetMapping(value = "/export-support/{type}")
    public ResponseEntity<Boolean> getExportSupport(@PathVariable String type) {
        LOG.debug("REST request to get export support for type '{}'", type);
        return ResponseEntity.ok().body(billingLedgerService
            .isExportSupport(InvoiceType.forValue(type)));
    }

    /**
     * Manually export list of billing ledgers.
     *
     * @param ids list of billing ledger ids to export
     * @return result errors
     */
    @PostMapping(value = "/export")
    public ResponseEntity<ErrorDTO> export(@RequestBody final List<Integer> ids) {
        LOG.debug("REST request to export billing ledger ids {}", ids);
        ErrorDTO result = billingLedgerExportUtility.exportList(ids);
        if (result != null)
            return ResponseEntity.badRequest().body(result);
        else
            return ResponseEntity.ok().body(null);
    }

    /**
     * Manually export all billing ledgers.
     *
     * @return result errors
     */
    @PostMapping(value = "/export-all")
    public ResponseEntity<ErrorDTO> exportAll() {
        LOG.debug("REST request to export all billing ledgers");
        ErrorDTO result = billingLedgerExportUtility.exportAll();
        if (result != null)
            return ResponseEntity.badRequest().body(result);
        else
            return ResponseEntity.ok().body(null);
    }
}
