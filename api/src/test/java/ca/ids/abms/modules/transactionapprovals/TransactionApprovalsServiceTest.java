package ca.ids.abms.modules.transactionapprovals;

import ca.ids.abms.modules.pendingtransactionapprovals.PendingTransactionApprovals;
import ca.ids.abms.modules.pendingtransactionapprovals.enumerate.PendingTransactionAction;
import ca.ids.abms.modules.pendingtransactions.PendingTransaction;
import ca.ids.abms.modules.transactions.Transaction;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TransactionApprovalsServiceTest {

    private TransactionApprovalsRepository transactionApprovalsRepository;
    private TransactionApprovalsService transactionApprovalsService;

    @Before
    public void setup() {
        transactionApprovalsRepository = mock(TransactionApprovalsRepository.class);
        transactionApprovalsService = new TransactionApprovalsService(transactionApprovalsRepository);
    }

    @Test
    public void getAllTransactionApprovals() {
        List<TransactionApprovals> transactionApprovals = Collections.singletonList(new TransactionApprovals());

        when(transactionApprovalsRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(transactionApprovals));

        Page<TransactionApprovals> results = transactionApprovalsService.findAll(mock(Pageable.class));
        assertThat(results.getTotalElements()).isEqualTo(transactionApprovals.size());
    }

    @Test
    public void saveTransactionApprovals() {
        TransactionApprovals transactionApproval = new TransactionApprovals();

        transactionApproval.setAction(PendingTransactionAction.APPROVAL.toValue());
        transactionApproval.setApprovalDateTime(LocalDateTime.now());
        transactionApproval.setApprovalLevel(1);
        transactionApproval.setApprovalNotes("test");
        transactionApproval.setApproverName("Erica Movato");
        transactionApproval.setTransaction(new Transaction());

        when(transactionApprovalsRepository.save(any(TransactionApprovals.class))).thenReturn(transactionApproval);

        TransactionApprovals result = transactionApprovalsService.save(transactionApproval);
        assertThat(result.getApproverName()).isEqualTo(transactionApproval.getApproverName());
        assertThat(result.getApprovalDateTime()).isEqualTo(transactionApproval.getApprovalDateTime());
        assertThat(result.getApprovalLevel()).isEqualTo(transactionApproval.getApprovalLevel());
    }

    @Test
    public void addTransactionApprovalsFromPendingTransactionApprovals() {
        TransactionApprovals transactionApproval = new TransactionApprovals();
        transactionApproval.setAction(PendingTransactionAction.APPROVAL.toValue());
        transactionApproval.setApprovalDateTime(LocalDateTime.now());
        transactionApproval.setApprovalLevel(2);
        transactionApproval.setApprovalNotes("notes");
        transactionApproval.setApproverName("Erica Movato");
        transactionApproval.setTransaction(new Transaction());

        when(transactionApprovalsRepository.save(any(TransactionApprovals.class))).thenReturn(transactionApproval);

        PendingTransactionApprovals pendingTransactionApprovals = new PendingTransactionApprovals(
            new PendingTransaction(), PendingTransactionAction.APPROVAL.toValue(), "Erica Movato",
            LocalDateTime.now(), 2, "notes");

        TransactionApprovals result = transactionApprovalsService.addTransactionApprovalsFromPendingTransactionApprovals(pendingTransactionApprovals, new Transaction());
        assertThat(result.getApproverName()).isEqualTo("Erica Movato");
        assertThat(result.getApprovalLevel()).isEqualTo(2);
        assertThat(result.getApprovalNotes()).isEqualTo("notes");
    }

    @Test
    public void addTransactionApprovals() {
        TransactionApprovals transactionApproval = new TransactionApprovals();
        transactionApproval.setAction(PendingTransactionAction.REJECTION.toValue());
        transactionApproval.setApprovalDateTime(LocalDateTime.now());
        transactionApproval.setApprovalLevel(3);
        transactionApproval.setApprovalNotes("notes");
        transactionApproval.setApproverName("Erica Movato");
        transactionApproval.setTransaction(new Transaction());

        when(transactionApprovalsRepository.save(any(TransactionApprovals.class))).thenReturn(transactionApproval);

        TransactionApprovals result = transactionApprovalsService.addTransactionApprovals(new Transaction(), "Erica Movato",
        "notes", 3, PendingTransactionAction.REJECTION);

        assertThat(result.getApproverName()).isEqualTo("Erica Movato");
        assertThat(result.getApprovalLevel()).isEqualTo(3);
        assertThat(result.getApprovalNotes()).isEqualTo("notes");
        assertThat(result.getAction()).isEqualTo(PendingTransactionAction.REJECTION.toValue());
    }
}
