package ca.ids.abms.modules.selfcareportal.transactions;

import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.config.security.SecurityUtils;
import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.common.controllers.MediaDocumentComponent;
import ca.ids.abms.modules.reports2.common.ReportDocument;
import ca.ids.abms.modules.reports2.common.ReportDocumentCreator;
import ca.ids.abms.modules.transactions.*;
import ca.ids.abms.modules.users.User;
import ca.ids.abms.modules.users.UserService;
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
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/sc-transactions")
@SuppressWarnings({"unused", "squid:S1452"})
public class SCTransactionsController extends MediaDocumentComponent {

    private static final Logger LOG = LoggerFactory.getLogger(SCTransactionsController.class);
	private final TransactionService transactionService;
	private final TransactionMapper transactionMapper;
	private final UserService userService;
    private final ReportDocumentCreator reportDocumentCreator;

    public SCTransactionsController(final TransactionService aTransactionService,
                                    final TransactionMapper transactionMapper,
                                    final UserService userService,
                                    final ReportDocumentCreator reportDocumentCreator) {
        this.transactionService = aTransactionService;
        this.transactionMapper = transactionMapper;
        this.userService = userService;
        this.reportDocumentCreator = reportDocumentCreator;
    }

    @GetMapping
    public ResponseEntity<?> getAllSCTransactions(
        @SortDefault(sort = {"transactionDateTime", "id"}, direction = Sort.Direction.DESC) Pageable pageable,
        @RequestParam(name = "searchFilter", required = false) final String searchFilter,
        @RequestParam(name = "account", required = false) Integer accountId,
        @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate startDate,
        @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate endDate,
        @RequestParam(name = "csvExport", required = false) Boolean csvExport) {

        LOG.debug("REST request to get all transactions");

        User us = userService.getUserByLogin (SecurityUtils.getCurrentUserLogin());

        // self_care_access user can get only transactions for his own accounts
        // self_care_admin can get transactions for any self-care account
        Page<Transaction> page;
        long totalRecords;

        if (!us.getIsSelfcareUser()) {
            LOG.debug("REST request to get all transactions by filter for Self-Care accounts");
            page = transactionService.findAllTransactionsForSelfCareAccounts(pageable, searchFilter, accountId, startDate, endDate, null);
            totalRecords = transactionService.countAllForSelfCareAccounts();
        } else {
            int userId = us.getId();
            LOG.debug("REST request to get all transactions by filter for Self-Care user: {}", userId);
            page = transactionService.findAllTransactionsForSelfCareAccounts(pageable, searchFilter, accountId, startDate, endDate, userId);
            totalRecords = transactionService.countAllForSelfCareUser(userId);
        }

        if (csvExport != null && csvExport) {
            final List<Transaction> list = page.getContent();
            final List<TransactionCsvExportModel> csvExportModel = transactionMapper.toCsvModel(list);
            ReportDocument report = reportDocumentCreator.createCsvDocument("Transactions", csvExportModel,
                TransactionCsvExportModel.class, true);
            return doCreateBinaryResponse(report);
        } else {
            return ResponseEntity.ok().body(new PageImplCustom<>(transactionMapper.toViewModel(page), pageable, page.getTotalElements(), totalRecords));
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Transaction> getTransaction(@PathVariable Integer id) {
        LOG.debug("REST request to get transaction by id: {}", id);

        Transaction transaction = transactionService.getOne(id);

        if (transaction == null) {
            throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION,
                    new Exception("Self-Care account transaction not found"));
        }

        Account ac = transaction.getAccount();
        if (ac == null || !ac.hasAccountUsers()) {
            throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION,
                    new Exception("Self-Care account not found or account is not self-care one"));
        }
        User us = userService.getUserByLogin (SecurityUtils.getCurrentUserLogin());
        if (us.getIsSelfcareUser() && ac.containsAccountUser(us)){
            throw new CustomParametrizedException(ErrorConstants.ERR_ACCESS_DENIED,
                    new Exception("User doesn't have permissions for the self-care account"));
        }
        return Optional.ofNullable(transaction).map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping(value = "/getBillingLedgersByTransactionId/{transactionId}")
    public Page<TransactionPayment> getBillingLedgersByTransactionId(@PathVariable Integer transactionId, Pageable pageable) {
        LOG.debug("REST request to get a list of invoices by transaction id (transactionId)  : {}", transactionId);
        Transaction transaction = transactionService.getOne(transactionId);

        if(transaction == null) {
            throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION,
                    new Exception("Self-Care account transaction not found"));
        }

        Account ac = transaction.getAccount();

        if (ac == null || !ac.hasAccountUsers()) {
            throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION,
                    new Exception("Self-Care account not found or account is not self-care one"));
        }

        User us = userService.getUserByLogin (SecurityUtils.getCurrentUserLogin());

        if(us.getIsSelfcareUser() && !ac.containsAccountUser(us)){
            throw new CustomParametrizedException(ErrorConstants.ERR_ACCESS_DENIED,
                    new Exception("User doesn't have permissions for the self-care account"));
        }

        return transactionService.getBillingLedgersByTransactionId(transactionId, pageable);
    }
}
