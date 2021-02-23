package ca.ids.abms.modules.reports2.transaction;

import com.codahale.metrics.annotation.Timed;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ca.ids.abms.modules.reports2.common.ReportControllerBase;
import ca.ids.abms.modules.transactions.TransactionService;

@RestController
@RequestMapping(value = {"api/reports2/transaction-receipt", "api/reports/transaction-receipt"})
public class TransactionReceiptController extends ReportControllerBase {

    private final TransactionService transactionService;

    public TransactionReceiptController (final TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * Get receipt document of the given transaction
     *
     * <H2>Usage</h2>
     *
     * <code><b><pre>
     *    GET /api/reports/transaction-receipt?transactionId=NNN
     * </pre></b></code>
     */
    @Timed
    @GetMapping
    @Transactional (readOnly = true)
    public ResponseEntity <?> getTransactionReceipt (@RequestParam final Integer transactionId) {
        return doCreateBinaryResponse (()->transactionService.getTransactionReceipt (transactionId));
    }


}
