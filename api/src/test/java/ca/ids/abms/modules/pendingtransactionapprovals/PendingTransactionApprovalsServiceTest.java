package ca.ids.abms.modules.pendingtransactionapprovals;

import ca.ids.abms.modules.pendingtransactionapprovals.enumerate.PendingTransactionAction;
import ca.ids.abms.modules.pendingtransactions.PendingTransaction;
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

public class PendingTransactionApprovalsServiceTest {
    private PendingTransactionApprovalsRepository pendingTransactionApprovalsRepository;
    private PendingTransactionApprovalsService pendingTransactionApprovalsService;

    @Before
    public void setup() {
        pendingTransactionApprovalsRepository = mock(PendingTransactionApprovalsRepository.class);
        pendingTransactionApprovalsService = new PendingTransactionApprovalsService(pendingTransactionApprovalsRepository);
    }

    @Test
    public void getAllPendingTransactionApprovals() {
        List<PendingTransactionApprovals> pendingTransactionApprovals = Collections.singletonList(new PendingTransactionApprovals());

        when(pendingTransactionApprovalsRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(pendingTransactionApprovals));

        Page<PendingTransactionApprovals> results = pendingTransactionApprovalsService.findAll(mock(Pageable.class));
        assertThat(results.getTotalElements()).isEqualTo(pendingTransactionApprovals.size());
    }

    @Test
    public void savePendingTransactionApprovals() {
        PendingTransactionApprovals pendingTransactionApprovals = new PendingTransactionApprovals();
        pendingTransactionApprovals.setAction(PendingTransactionAction.APPROVAL.toValue());
        pendingTransactionApprovals.setApprovalDateTime(LocalDateTime.now());
        pendingTransactionApprovals.setApprovalLevel(1);
        pendingTransactionApprovals.setApprovalNotes("test");
        pendingTransactionApprovals.setApproverName("Erica Movato");
        pendingTransactionApprovals.setPendingTransaction(new PendingTransaction());

        when(pendingTransactionApprovalsRepository.save(any(PendingTransactionApprovals.class))).thenReturn(pendingTransactionApprovals);

        PendingTransactionApprovals result = pendingTransactionApprovalsService.save(pendingTransactionApprovals);
        assertThat(result.getApproverName()).isEqualTo(pendingTransactionApprovals.getApproverName());
        assertThat(result.getApprovalDateTime()).isEqualTo(pendingTransactionApprovals.getApprovalDateTime());
        assertThat(result.getApprovalLevel()).isEqualTo(pendingTransactionApprovals.getApprovalLevel());
    }

    @Test
    public void getByPendingTransactionId() {
        PendingTransactionApprovals pendingTransactionApprovals = new PendingTransactionApprovals();
        pendingTransactionApprovals.setAction(PendingTransactionAction.APPROVAL.toValue());
        pendingTransactionApprovals.setApprovalDateTime(LocalDateTime.now());
        pendingTransactionApprovals.setApprovalLevel(1);
        pendingTransactionApprovals.setApprovalNotes("test");
        pendingTransactionApprovals.setApproverName("Erica Movato");
        pendingTransactionApprovals.setPendingTransaction(new PendingTransaction());

        List<PendingTransactionApprovals> list = Collections.singletonList(pendingTransactionApprovals);

        when(pendingTransactionApprovalsRepository.findAllByPendingTransactionId(2)).thenReturn(list);

        List<PendingTransactionApprovals> result = pendingTransactionApprovalsService.getByPendingTransactionId(2);
        assertThat(result.size()).isEqualTo(1);
    }
}
