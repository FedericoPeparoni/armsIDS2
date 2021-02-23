package ca.ids.abms.modules.pendingtransactions;

import ca.ids.abms.config.security.SecurityUtils;
import ca.ids.abms.modules.billings.BillingLedger;
import ca.ids.abms.modules.billings.BillingLedgerCsvExportModel;
import ca.ids.abms.modules.billings.BillingLedgerMapper;
import ca.ids.abms.modules.common.controllers.MediaDocumentComponent;
import ca.ids.abms.modules.common.dto.MediaDocument;
import ca.ids.abms.modules.pendingtransactionapprovals.PendingTransactionApprovals;
import ca.ids.abms.modules.pendingtransactionapprovals.PendingTransactionApprovalsCsvExportModel;
import ca.ids.abms.modules.pendingtransactionapprovals.PendingTransactionApprovalsMapper;
import ca.ids.abms.modules.pendingtransactionapprovals.PendingTransactionApprovalsService;
import ca.ids.abms.modules.pendingtransactionapprovals.enumerate.PendingTransactionAction;
import ca.ids.abms.modules.pendingtransactions.enumerate.PendingTransactionDocumentType;
import ca.ids.abms.modules.reports2.common.ReportDocument;
import ca.ids.abms.modules.reports2.common.ReportDocumentCreator;
import ca.ids.abms.modules.users.User;
import ca.ids.abms.modules.users.UserService;
import ca.ids.abms.modules.util.models.PageImplCustom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/pending-transactions")
@SuppressWarnings({"unused", "squid:S1452"})
public class PendingTransactionsController extends MediaDocumentComponent {

    private static final Logger LOG = LoggerFactory.getLogger(PendingTransactionsController.class);

    private final PendingTransactionService pendingTransactionService;
    private final PendingTransactionMapper pendingTransactionMapper;
    private final PendingTransactionApprovalsService pendingTransactionApprovalsService;
    private final UserService userService;
    private final BillingLedgerMapper billingLedgerMapper;
    private final PendingChargeAdjustmentMapper pendingChargeAdjustmentMapper;
    private final PendingTransactionApprovalsMapper pendingTransactionApprovalsMapper;
    private final ReportDocumentCreator reportDocumentCreator;

    public PendingTransactionsController(final PendingTransactionService pendingTransactionService,
                                         final PendingTransactionMapper pendingTransactionMapper,
                                         final PendingTransactionApprovalsService pendingTransactionApprovalsService,
                                         final UserService userService,
                                         final BillingLedgerMapper billingLedgerMapper,
                                         final PendingChargeAdjustmentMapper pendingChargeAdjustmentMapper,
                                         final PendingTransactionApprovalsMapper pendingTransactionApprovalsMapper,
                                         final ReportDocumentCreator reportDocumentCreator) {
        this.pendingTransactionService = pendingTransactionService;
        this.pendingTransactionMapper = pendingTransactionMapper;
        this.pendingTransactionApprovalsService = pendingTransactionApprovalsService;
        this.userService = userService;
        this.billingLedgerMapper = billingLedgerMapper;
        this.pendingChargeAdjustmentMapper = pendingChargeAdjustmentMapper;
        this.pendingTransactionApprovalsMapper = pendingTransactionApprovalsMapper;
        this.reportDocumentCreator = reportDocumentCreator;
    }

    @GetMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('transaction_pending_view') or hasAuthority('transaction_pending_modify')")
    public ResponseEntity<PendingTransactionViewModel> getOne(@PathVariable Integer id) {
        LOG.debug("REST request to get PendingTransaction ID: {}", id);

        final PendingTransaction entity = pendingTransactionService.getOne(id);

        return Optional.ofNullable(entity)
            .map(result -> new ResponseEntity<>(pendingTransactionMapper.toViewModel(entity), HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('transaction_pending_view') or hasAuthority('transaction_pending_modify')")
    public ResponseEntity<?> findAll (
        @RequestParam(value = "text_search", required = false) String textSearch,
        @RequestParam(value = "approval_name", required = false) String approvalName,
        @RequestParam(value = "by_current_role", required = false) String currentRoles,
        @SortDefault(sort = {"transactionDateTime"}, direction = Sort.Direction.DESC) Pageable pageable,
        @RequestParam(name = "csvExport", required = false) Boolean csvExport) {

        LOG.debug("REST request to get all PendingTransactions filtered by text search \"{}\" and approval name \"{}\" using the current user roles: {}",
            textSearch, approvalName, Boolean.valueOf(currentRoles));

        final Page<PendingTransaction> entities = pendingTransactionService.findAll(pageable, textSearch, approvalName, currentRoles);

        if (csvExport != null && csvExport) {
            final List<PendingTransaction> list = entities.getContent();
            final List<PendingTransactionCsvExportModel> csvExportModel = pendingTransactionMapper.toCsvModel(list);
            ReportDocument report = reportDocumentCreator.createCsvDocument("Pending_Transactions", csvExportModel, PendingTransactionCsvExportModel.class, true);
            return doCreateBinaryResponse(report);
        } else {
            long countAll = pendingTransactionService.countAll();
            return Optional.ofNullable(entities)
                .map(result -> new ResponseEntity<Page<PendingTransactionViewModel>>(
                    new PageImplCustom<>(pendingTransactionMapper.toViewModel(entities), pageable, entities.getTotalElements(), countAll),
                    HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }
    }

    @PreAuthorize("hasAuthority('transaction_pending_modify')")
    @PutMapping(value = {"/approve/{id}/{notes}", "/approve/{id}"})
    public ResponseEntity<Void> approve(@RequestParam(value = "file", required = false) final MultipartFile file,
                                        @PathVariable final Integer id,
                                        @PathVariable(required = false) final String notes) throws IOException {
        if (file != null) {
            LOG.debug("REST request to approve the PendingTransaction with ID and approval document: {}, {}", id, file.getOriginalFilename());
        } else {
            LOG.debug("REST request to approve the PendingTransaction with ID: {}", id);
        }

        boolean approve = pendingTransactionService.approve(id, file, notes);
        PendingTransaction pendingTransaction = pendingTransactionService.findOne(id);

        if (approve && pendingTransaction != null) {
            PendingTransactionApprovals approvalsInformation = new PendingTransactionApprovals(
                pendingTransaction, PendingTransactionAction.APPROVAL.toValue(), getCurrentUser().getName(),
                LocalDateTime.now(), pendingTransaction.getCurrentApprovalLevel().getLevel(), notes);
            pendingTransactionApprovalsService.save(approvalsInformation);
        }
        return approve ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasAuthority('transaction_pending_modify')")
    @PutMapping(value = "/{id}/reject")
    public ResponseEntity<Void> reject (@PathVariable final Integer id,
                                        @RequestBody final String notes) {
        LOG.debug("REST request to reject the PendingTransaction with ID: {}", id);

        boolean reject = pendingTransactionService.reject(id);
        PendingTransaction pendingTransaction = pendingTransactionService.findOne(id);

        if (pendingTransaction != null && reject) {
            PendingTransactionApprovals approvalsInformation = new PendingTransactionApprovals(
                pendingTransaction, PendingTransactionAction.REJECTION.toValue(), getCurrentUser().getName(),
                LocalDateTime.now(), pendingTransaction.getCurrentApprovalLevel().getLevel(), notes);
            pendingTransactionApprovalsService.save(approvalsInformation);
        }
        return reject ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasAuthority('transaction_pending_view')")
    @GetMapping(value = "/{id}/download/{documentType}")
    public ResponseEntity<ByteArrayResource> download(
        @PathVariable final Integer id, @PathVariable final String documentType
    ) {
        LOG.debug("REST request to download '{}' document type for PendingTransaction with ID: {}", documentType, id);
        return doCreateBinaryResponse(() -> pendingTransactionService.getMediaDocument(id,
            PendingTransactionDocumentType.forValue(documentType)));
    }

    @PreAuthorize("hasAuthority('transaction_pending_modify')")
    @PutMapping(path = { "/{id}/upload", "/{id}/upload/{documentType}" }, consumes = { "multipart/form-data" })
    public ResponseEntity<Void> upload(
        @PathVariable final Integer id,
        @PathVariable(required = false) final String documentType,
        @RequestPart(name = "file") final MultipartFile file
    ) throws IOException {

        // default document type to supporting document
        PendingTransactionDocumentType type = documentType == null
            ? PendingTransactionDocumentType.SUPPORTING
            : PendingTransactionDocumentType.forValue(documentType);

        LOG.debug("REST request to upload '{}' document type for PendingTransaction with ID: {}", type, id);

        // update existing pending transaction media document
        pendingTransactionService.setMediaDocument(id, type,
            new MediaDocument(file.getOriginalFilename(), file.getContentType(), file.getBytes()));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Return the currently logged-in user
     */
    public User getCurrentUser() {
        return userService.getUserByLogin (SecurityUtils.getCurrentUserLogin());
    }

    @GetMapping(path = "/getInvoicesByPendingTransactionId/{pendingTransactionId}")
    public ResponseEntity<?> getInvoicesByPendingTransactionId(@PathVariable final Integer pendingTransactionId,
                                                              @RequestParam(name = "csvExport", required = false) Boolean csvExport) {
        LOG.debug("REST request to get a list of invoices by pending transaction id : {}", pendingTransactionId);

        List<BillingLedger> list = pendingTransactionService.getInvoicesByPendingTransactionId(pendingTransactionId);
        if (csvExport != null && csvExport) {
            final List<BillingLedgerCsvExportModel> csvExportModel = billingLedgerMapper.toCsvModel(list);
            ReportDocument report = reportDocumentCreator.createCsvDocument("Invoices", csvExportModel, BillingLedgerCsvExportModel.class, true);
            return doCreateBinaryResponse(report);
        } else {
            return new ResponseEntity<>(list, HttpStatus.OK);
        }
    }

    @GetMapping(path = "/getChargesAdjustmentsByPendingTransactionId/{pendingTransactionId}")
    public ResponseEntity<?> getChargesAdjustmentsByPendingTransactionId(@PathVariable final Integer pendingTransactionId,
                                                                         @RequestParam(name = "csvExport", required = false) Boolean csvExport) {
        LOG.debug("REST request to get a list of charges adjustments by pending transaction id : {}", pendingTransactionId);

        List<PendingChargeAdjustment> list = pendingTransactionService.getChargesAdjustmentsByPendingTransactionId(pendingTransactionId);
        if (csvExport != null && csvExport) {
            final List<PendingChargeAdjustmentCsvExportModel> csvExportModel = pendingChargeAdjustmentMapper.toCsvModel(list);
            ReportDocument report = reportDocumentCreator.createCsvDocument("Pending_Charge_Adjustments", csvExportModel, PendingChargeAdjustmentCsvExportModel.class, true);
            return doCreateBinaryResponse(report);
        } else {
            return new ResponseEntity<>(list, HttpStatus.OK);
        }
    }

    @GetMapping(path = "/getTransactionApprovalsByPendingTransactionId/{pendingTransactionId}")
    public ResponseEntity<?> getTransactionApprovalsByPendingTransactionId(@PathVariable final Integer pendingTransactionId,
                                                                           @RequestParam(name = "csvExport", required = false) Boolean csvExport) {
        LOG.debug("REST request to get a list of transaction approvals by pending transaction id : {}", pendingTransactionId);

        List<PendingTransactionApprovals> list = pendingTransactionApprovalsService.getByPendingTransactionId(pendingTransactionId);
        if (csvExport != null && csvExport) {
            final List<PendingTransactionApprovalsCsvExportModel> csvExportModel = pendingTransactionApprovalsMapper.toCsvModel(list);
            ReportDocument report = reportDocumentCreator.createCsvDocument("Pending_Transaction_Approvals", csvExportModel, PendingTransactionApprovalsCsvExportModel.class, true);
            return doCreateBinaryResponse(report);
        } else {
            return new ResponseEntity<>(list, HttpStatus.OK);
        }
    }
}
