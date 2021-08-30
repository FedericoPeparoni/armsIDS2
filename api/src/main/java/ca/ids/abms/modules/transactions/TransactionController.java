package ca.ids.abms.modules.transactions;

import ca.ids.abms.config.error.ErrorDTO;
import ca.ids.abms.config.security.SecurityUtils;
import ca.ids.abms.modules.billings.BillingLedger;
import ca.ids.abms.modules.billings.BillingLedgerCsvExportModel;
import ca.ids.abms.modules.billings.BillingLedgerMapper;
import ca.ids.abms.modules.common.controllers.MediaDocumentComponent;
import ca.ids.abms.modules.currencies.Currency;
import ca.ids.abms.modules.reports2.common.ReportDocument;
import ca.ids.abms.modules.reports2.common.ReportDocumentCreator;
import ca.ids.abms.modules.transactionapprovals.TransactionApprovals;
import ca.ids.abms.modules.transactionapprovals.TransactionApprovalsCsvExportModel;
import ca.ids.abms.modules.transactionapprovals.TransactionApprovalsMapper;
import ca.ids.abms.modules.transactionapprovals.TransactionApprovalsService;
import ca.ids.abms.modules.transactions.enumerate.TransactionDocumentType;
import ca.ids.abms.modules.transactions.utility.TransactionExportUtility;
import ca.ids.abms.modules.users.User;
import ca.ids.abms.modules.users.UserService;
import ca.ids.abms.modules.util.models.PageImplCustom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static ca.ids.abms.modules.transactions.TransactionController.ENDPOINT;

@RestController
@RequestMapping(ENDPOINT)
@SuppressWarnings({"unused", "squid:S1452"})
public class TransactionController extends MediaDocumentComponent {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionController.class);

    static final String ENDPOINT = "/api/transactions";

    private final TransactionExportUtility transactionExportUtility;
    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;
    private final UserService userService;
    private final BillingLedgerMapper billingLedgerMapper;
    private final TransactionPaymentMapper transactionPaymentMapper;
    private final TransactionApprovalsMapper transactionApprovalsMapper;
    private final TransactionApprovalsService transactionApprovalsService;
    private final ReportDocumentCreator reportDocumentCreator;

    @SuppressWarnings("squid:S00107") // Contractor has more parameters than authorized
    public TransactionController(final TransactionExportUtility transactionExportUtility,
                                 final TransactionService aTransactionService,
                                 final TransactionMapper transactionMapper,
                                 final UserService userService,
                                 final BillingLedgerMapper billingLedgerMapper,
                                 final TransactionPaymentMapper transactionPaymentMapper,
                                 final TransactionApprovalsMapper transactionApprovalsMapper,
                                 TransactionApprovalsService transactionApprovalsService, final ReportDocumentCreator reportDocumentCreator) {
        this.transactionExportUtility = transactionExportUtility;
        this.transactionService = aTransactionService;
        this.transactionMapper = transactionMapper;
        this.userService = userService;
        this.billingLedgerMapper = billingLedgerMapper;
        this.transactionPaymentMapper = transactionPaymentMapper;
        this.transactionApprovalsMapper = transactionApprovalsMapper;
        this.transactionApprovalsService = transactionApprovalsService;
        this.reportDocumentCreator = reportDocumentCreator;
    }

    @GetMapping
    public ResponseEntity<?> getAllTransactions(
        @RequestParam(name = "searchFilter", required = false) final String searchFilter,
        @RequestParam(name = "exportedFilter", required = false) Boolean exportedFilter,
        @RequestParam(name = "account", required = false) Integer accountId,
        @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate startDate,
        @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate endDate,
        @SortDefault(sort = { "transactionDateTime" }, direction = Sort.Direction.DESC) Pageable pageable,
        @RequestParam(name = "csvExport", required = false) Boolean csvExport) throws Exception {

        LOG.debug("REST request to get all transactions");


        if (csvExport != null && csvExport) {
            return getAllTransactionsCSV(searchFilter, exportedFilter, accountId, startDate, endDate);
        } else {
            final Page<Transaction> page = transactionService.findAll(pageable, searchFilter, exportedFilter, accountId, startDate, endDate);
            long countAllItem = transactionService.countAllTransactions();
            return ResponseEntity.ok().body(new PageImplCustom<>(transactionMapper.toViewModel(page), pageable, page.getTotalElements(), countAllItem));
        }
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Transaction> getTransaction(@PathVariable Integer id) {
        LOG.debug("REST request to get transaction : {}", id);

        Transaction transaction = transactionService.getOne(id);

        return Optional.ofNullable(transaction).map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping(path = { "", "/upload"}, consumes = { "multipart/form-data" })
    @PreAuthorize("hasAuthority('transaction_modify')")
    @Transactional
    @SuppressWarnings("squid:S1075")
    public ResponseEntity<TransactionViewModel> create(@RequestPart(name = "properties") @Valid final TransactionViewModel transactionViewModel,
                                                       @RequestPart(name = "file", required = false) final MultipartFile file) throws IOException, URISyntaxException {
        LOG.debug("REST request to save transaction : {}", transactionViewModel);

        Transaction transactionEntity = transactionMapper.toModel(transactionViewModel);

        // add supporting document file if provided
        if (file != null && file.getBytes() != null) {
            transactionEntity.setSupportingDocument(file.getBytes());
            transactionEntity.setSupportingDocumentName(file.getOriginalFilename());
            transactionEntity.setSupportingDocumentType(file.getContentType());
        }

        Transaction resultEntity = transactionService.createTransactionByUI(transactionEntity, true);
        TransactionViewModel result = transactionMapper.toViewModel(resultEntity);


        if (result == null || result.getId() == null)
            return ResponseEntity.created(new URI(ENDPOINT)).body(result);
        else {
            result.setInterestInvoiceError(transactionService.checkIfInterestInvoiceNeeded(resultEntity.getBillingLedgerIds()));
            return ResponseEntity.created(new URI(ENDPOINT + "/" + result.getId())).body(result);
        }
    }

    @PostMapping(path = "/calculateAmountForTransactionPayments")
    @PreAuthorize("hasAuthority('transaction_modify')")
    public ResponseEntity<HashMap<Integer, Double>> calculateAmountForTransactionPayments(@Valid @RequestBody TransactionViewModel transactionViewModel) {

        Transaction transactionEntity = transactionMapper.toModel(transactionViewModel);
        HashMap<Integer, Double> result = transactionService.calculateAmountForTransactionPayments(transactionEntity);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Returns a list of allowed currencies for the transaction
     */
    @GetMapping(path = "/getCurrencyListByAccountId/{accountId}")
    public ResponseEntity<List<Currency>> getCurrencyListForTransaction(@PathVariable Integer accountId) {
        LOG.debug("REST request to get a list of allowed currencies for the transaction, accountId  : {}", accountId);

        final List<Currency> result = transactionService.getCurrencyListForTransactionByAccountId(accountId);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(path = "/getTransactionByBillingLedgerId/{invoiceId}")
    public ResponseEntity<?> getTransactionByBillingLedgerId(@PathVariable Integer invoiceId,
                                                             @RequestParam(name = "csvExport", required = false) Boolean csvExport) {
        LOG.debug("REST request to get a list of Transactions by invoice id : {}", invoiceId);

        List<Transaction> list = transactionService.getTransactionByBillingLedgerId(invoiceId);
        if (csvExport != null && csvExport) {
            final List<TransactionCsvExportModel> csvExportModel = transactionMapper.toCsvModel(list);
            ReportDocument report = reportDocumentCreator.createCsvDocument("Transactions", csvExportModel,
                TransactionCsvExportModel.class, true);
            return doCreateBinaryResponse(report);
        } else {
            return new ResponseEntity<>(list, HttpStatus.OK);
        }
    }

    @GetMapping(path = "/getTransactionPaymentsByBillingLedgerId/{invoiceId}")
    public ResponseEntity<?> getTransactionPaymentsByBillingLedgerId(@PathVariable Integer invoiceId,
                                                                            @RequestParam(name = "csvExport", required = false) Boolean csvExport) {
        LOG.debug("REST request to get a list of Transactions Payments by invoice id : {}", invoiceId);

        List<TransactionPayment> list = transactionService.getTransactionPaymentsByBillingLedgerId(invoiceId);
        if (csvExport != null && csvExport) {
            final List<TransactionPaymentCsvExportModel> csvExportModel = transactionPaymentMapper.toCsvModel(list);
            ReportDocument report = reportDocumentCreator.createCsvDocument("Transaction_Payments", csvExportModel,
                TransactionPaymentCsvExportModel.class, true);
            return doCreateBinaryResponse(report);
        } else {
            return new ResponseEntity<>(list, HttpStatus.OK);
        }
    }

    @GetMapping(path = "/getTransactionPaymentsByTransactionId/{transactionId}")
    public ResponseEntity<?> getTransactionPaymentsByTransactionId(@PathVariable final Integer transactionId,
                                                                   Pageable pageable,
                                                                   @RequestParam(name = "csvExport", required = false) Boolean csvExport) {
        LOG.debug("REST request to get a list of transaction payments by transaction id : {}", transactionId);
        Page<TransactionPayment> page = transactionService.getAllTransactionPaymentsByTransactionId(transactionId, pageable);
        if (csvExport != null && csvExport) {
            final List<TransactionPayment> list = page.getContent();
            final List<TransactionPaymentCsvExportModel> csvExportModel = transactionPaymentMapper.toCsvModel(list);
            ReportDocument report = reportDocumentCreator.createCsvDocument("Transaction_Payments", csvExportModel,
                TransactionPaymentCsvExportModel.class, true);
            return doCreateBinaryResponse(report);
        } else {
            return new ResponseEntity<>(page, HttpStatus.OK);
        }
    }

    @GetMapping(path = "/getBillingLedgersByTransactionId/{transactionId}")
    public ResponseEntity<?> getBillingLedgersByTransactionId(@PathVariable final Integer transactionId,
                                                              Pageable pageable,
                                                              @RequestParam(name = "csvExport", required = false) Boolean csvExport) {
        LOG.debug("REST request to get a list of invoices by transaction id : {}", transactionId);
        Page<BillingLedger> page = transactionService.getAllBillingLedgerByTransactionId(transactionId, pageable);
        if (csvExport != null && csvExport) {
            final List<BillingLedger> list = page.getContent();
            final List<BillingLedgerCsvExportModel> csvExportModel = billingLedgerMapper.toCsvModel(list);
            ReportDocument report = reportDocumentCreator.createCsvDocument("Invoices", csvExportModel, BillingLedgerCsvExportModel.class, true);
            return doCreateBinaryResponse(report);
        } else {
            return new ResponseEntity<>(page, HttpStatus.OK);
        }
    }

    @GetMapping(path = "/getTransactionApprovalsByTransactionId/{transactionId}")
    public ResponseEntity<?> getTransactionApprovalsByTransactionId(@PathVariable final Integer transactionId,
                                                                    @RequestParam(name = "csvExport", required = false) Boolean csvExport) {
        LOG.debug("REST request to get a list of transaction approvals by transaction id : {}", transactionId);

        List<TransactionApprovals> list = transactionApprovalsService.getAllTransactionApprovalsByTransactionId(transactionId);
        if (csvExport != null && csvExport) {
            final List<TransactionApprovalsCsvExportModel> csvExportModel = transactionApprovalsMapper.toCsvModel(list);
            ReportDocument report = reportDocumentCreator.createCsvDocument("Transaction_Approvals", csvExportModel,
                TransactionApprovalsCsvExportModel.class, true);
            return doCreateBinaryResponse(report);
        } else {
            return new ResponseEntity<>(list, HttpStatus.OK);
        }
    }

    @GetMapping(path = "/getDebitNoteByTransactionId/{transactionId}")
    public ResponseEntity<?> getDebitNoteByTransactionId(@PathVariable final Integer transactionId,
                                                         @RequestParam(name = "csvExport", required = false) Boolean csvExport) {
        LOG.debug("REST request to get a debit note by transaction id : {}", transactionId);

        BillingLedger bl = transactionService.getDebitNoteBillingLedgerByTransactionId(transactionId);
        if (csvExport != null && csvExport) {
        	List<BillingLedger> list = new ArrayList<BillingLedger>();
            final List<BillingLedgerCsvExportModel> csvExportModel = billingLedgerMapper.toCsvModel(list);
            ReportDocument report = reportDocumentCreator.createCsvDocument("Transaction_Approvals", csvExportModel,
            		BillingLedgerCsvExportModel.class, true);
            return doCreateBinaryResponse(report);
        } else {
            return new ResponseEntity<>(bl, HttpStatus.OK);
        }
    }


    @GetMapping(path = "/getPaymentMechanismList")
    public ResponseEntity<List<String>> listPaymentMechanism() {
        LOG.debug("REST request to get a list of TransactionPaymentMechanism");

        List<String> result = transactionService.getPaymentMechanismList();

        User currentUser = userService.getUserByLogin(SecurityUtils.getCurrentUserLogin());
        boolean canCreateAdjustment = currentUser.getPermissions().contains("transaction_adjustment_create");
        if (!canCreateAdjustment) {
            result.removeIf(p -> p.equalsIgnoreCase("adjustment"));
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Get if transaction exporting is supported.
     *
     * @return true if exporting supported
     */
    @GetMapping(path = "/export-support")
    public ResponseEntity<TransactionExportSupport> exportSupport() {
        LOG.debug("REST request to get transaction export support");
        return ResponseEntity.ok().body(transactionService.exportSupport());
    }

    /**
     * Get if transaction exporting is supported for mechanism.
     *
     * @param mechanism transaction mechanism to check
     * @return true if exporting supported
     */
    @GetMapping(path = "/export-support/{mechanism}")
    public ResponseEntity<Boolean> exportSupport(@PathVariable String mechanism) {
        LOG.debug("REST request to get export support for mechanism '{}'", mechanism);
        return ResponseEntity.ok()
                .body(transactionService.isExportSupport(TransactionPaymentMechanism.valueOf(mechanism)));
    }

    /**
     * Manually export list of transactions.
     *
     * @param ids list of transaction ids to export
     * @return result errors
     */
    @PostMapping(path = "/export")
    public ResponseEntity<ErrorDTO> export(@RequestBody final List<Integer> ids) {
        LOG.debug("REST request to export transaction ids {}", ids);
        ErrorDTO result = transactionExportUtility.exportList(ids);
        if (result != null)
            return ResponseEntity.badRequest().body(result);
        else
            return ResponseEntity.ok().body(null);
    }

    /**
     * Manually export all transactions.
     *
     * @return result errors
     */
    @PostMapping(path = "/export-all")
    public ResponseEntity<ErrorDTO> exportAll() {
        LOG.debug("REST request to export all transactions");
        ErrorDTO result = transactionExportUtility.exportAll();
        if (result != null)
            return ResponseEntity.badRequest().body(result);
        else
            return ResponseEntity.ok().body(null);
    }

    @GetMapping(path = "/{id}/download/{documentType}")
    public ResponseEntity<ByteArrayResource> download(@PathVariable final Integer id,
                                                      @PathVariable final String documentType) {
        LOG.debug("REST request to download '{}' document type for Transaction with ID: {}", documentType, id);
        return doCreateBinaryResponse(() -> transactionService.getMediaDocument(id,
            TransactionDocumentType.forValue(documentType)));
    }


    //CSV Export
    private ResponseEntity<?> getAllTransactionsCSV(final String searchFilter,
                                                    Boolean exportedFilter,
                                                    Integer accountId,
                                                    final LocalDate startDate,
                                                    LocalDate endDate) throws Exception {
        LOG.debug("REST request to get all transactions CSV");
        Pageable pageable;
        int pageSize = 10000;
        int currentPage = 0;

        ReportDocument doc = null;
        //TODO: use For
        while(true) {
            pageable = new PageRequest(currentPage, pageSize);
            final Page<Transaction> page = transactionService.findAllForDownload(pageable, searchFilter, exportedFilter, accountId, startDate, endDate);
            if(!page.hasContent()) {
                break;
            }

            //Convert to List
            final List<Transaction> list = new ArrayList();
            list.addAll(page.getContent());
            LOG.debug("grandezza lista " + list.size());
            LOG.debug("Page: " + (currentPage));
            final List<TransactionCsvExportModel> csvExportModel = transactionMapper.toCsvModel(list);
            currentPage = currentPage + 1;
            if(doc == null) {
                doc = reportDocumentCreator.createCsvDocument("Transactions", csvExportModel, TransactionCsvExportModel.class, true);
            }else {
                reportDocumentCreator.appendToCsvDocument(doc, csvExportModel,TransactionCsvExportModel.class, true, false);
            }
        }
        //Flush
        doc.getOutputStream().flush();
        doc.getOutputStream().close();
        return doCreateResource(doc);
        //return doCreateStreamingResponse(doc);

    }
}
