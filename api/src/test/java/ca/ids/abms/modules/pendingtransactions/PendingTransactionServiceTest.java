package ca.ids.abms.modules.pendingtransactions;

import ca.ids.abms.modules.billings.BillingLedgerRepository;
import ca.ids.abms.modules.currencies.Currency;
import ca.ids.abms.modules.pendingtransactionapprovals.PendingTransactionApprovalsService;
import ca.ids.abms.modules.roles.Role;
import ca.ids.abms.modules.transactionapprovals.TransactionApprovalsService;
import ca.ids.abms.modules.transactions.TransactionService;
import ca.ids.abms.modules.users.User;
import ca.ids.abms.modules.users.UserRole;
import ca.ids.abms.modules.users.UserService;
import ca.ids.abms.modules.util.models.CurrencyUtils;
import ca.ids.abms.modules.workflows.ApprovalWorkflow;
import ca.ids.abms.modules.workflows.ApprovalWorkflowRepository;
import ca.ids.abms.modules.workflows.ApprovalWorkflowService;
import ca.ids.abms.modules.workflows.StatusType;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class PendingTransactionServiceTest {

    private PendingTransactionRepository pendingTransactionRepository = mock(PendingTransactionRepository.class);
    private ApprovalWorkflowService approvalWorkflowService = mock(ApprovalWorkflowService.class);
    private CurrencyUtils currencyUtils = mock(CurrencyUtils.class);
    private BillingLedgerRepository billingLedgerRepository = mock(BillingLedgerRepository.class);
    private UserService userService = mock(UserService.class);
    private PendingTransactionMapper pendingTransactionMapper = mock(PendingTransactionMapper.class);
    private TransactionService transactionService = mock(TransactionService.class);
    private ApprovalWorkflowRepository approvalWorkflowRepository = mock(ApprovalWorkflowRepository.class);
    private PendingTransactionApprovalsService pendingTransactionApprovalsService = mock(PendingTransactionApprovalsService.class);
    private TransactionApprovalsService transactionApprovalsService = mock(TransactionApprovalsService.class);
    private PendingTransactionService pendingTransactionService;
    private PendingChargeAdjustmentRepository pendingChargeAdjustmentRepository = mock(PendingChargeAdjustmentRepository.class);

    @Before
    public void setup() {
        pendingTransactionService = new PendingTransactionService(pendingTransactionRepository,
            pendingTransactionMapper, approvalWorkflowService, userService, currencyUtils, billingLedgerRepository,
            transactionService, approvalWorkflowRepository, pendingTransactionApprovalsService, pendingChargeAdjustmentRepository, transactionApprovalsService);

        when(pendingTransactionRepository.findAll(any(Specification.class), any(Pageable.class)))
            .thenReturn(getAllTransactions());

        when(pendingTransactionRepository.getOne(eq(1)))
            .thenReturn(getFirstPendingTransaction());


        when(pendingTransactionRepository.getOne(eq(2)))
            .thenReturn(getSecondPendingTransaction());

        when(approvalWorkflowRepository.findByApprovalDocumentRequiredTrue()).thenReturn(null);

        when(approvalWorkflowService.getApprovedStatus()).thenReturn(WORKFLOW.get(StatusType.FINAL));
        when(approvalWorkflowService.getDeletedStatus()).thenReturn(WORKFLOW.get(StatusType.INITIAL));
        when(approvalWorkflowService.getInitialStatus()).thenReturn(WORKFLOW.get(StatusType.INITIAL));
    }

    @Test
    public void findAll() throws Exception {
        Page<PendingTransaction> transactions = pendingTransactionService.findAll(null, null, null, null);

        assertThat(transactions.getTotalElements()).isEqualTo(1);
        assertThat(transactions.getContent().get(0)).isEqualTo(getFirstPendingTransaction());

    }

    @Test
    public void getOne() throws Exception {
        when(userService.getAuthenticatedUser())
            .thenReturn(getManagerUser());

        PendingTransaction pendingTransaction = pendingTransactionService.getOne(1);

        assertThat(pendingTransaction).isNotNull();
        assertThat(pendingTransaction.getId()).isEqualTo(1);
        assertThat(pendingTransaction.getCanApprove()).isFalse();
        assertThat(pendingTransaction.getCanReject()).isFalse();

        when(userService.getAuthenticatedUser())
            .thenReturn(getDirectorUser());

        pendingTransaction = pendingTransactionService.getOne(1);

        assertThat(pendingTransaction.getCanApprove()).isTrue();
        assertThat(pendingTransaction.getCanReject()).isTrue();
    }

    @Test
    public void approveTransaction() throws Exception {
        when(userService.getAuthenticatedUser())
            .thenReturn(getManagerUser());

        PendingTransaction pendingTransaction = getSecondPendingTransaction();

        when(pendingTransactionRepository.save(pendingTransaction))
            .thenReturn(pendingTransaction);

        boolean updated = pendingTransactionService.approve(2, null, null);

        assertThat (updated).isTrue();
        verify(pendingTransactionRepository).save(any(PendingTransaction.class));
    }

    @Test
    public void reject() throws Exception {
        when(userService.getAuthenticatedUser())
            .thenReturn(getManagerUser());

        PendingTransaction pendingTransaction = getSecondPendingTransaction();

        when(pendingTransactionRepository.save(pendingTransaction))
            .thenReturn(pendingTransaction);

        boolean updated = pendingTransactionService.reject(2);

        assertThat (updated).isTrue();
        verify(pendingTransactionRepository).delete(eq(2));
    }

    private User getDirectorUser () {
        User user = new User();
        user.setId(1000);
        UserRole userRole = new UserRole();
        userRole.setId(987);
        userRole.setUser(user);
        userRole.setRole(getDirectorRole());
        Set<UserRole> userRoles = new HashSet<>(1);
        userRoles.add(userRole);
        user.setUserRoles(userRoles);
        return user;
    }

    private User getManagerUser () {
        User user = new User();
        user.setId(1001);
        UserRole userRole = new UserRole();
        userRole.setId(876);
        userRole.setUser(user);
        userRole.setRole(getManagerRole());
        Set<UserRole> userRoles = new HashSet<>(1);
        userRoles.add(userRole);
        user.setUserRoles(userRoles);
        return user;
    }


    private static Role getManagerRole () {
        Role role = new Role();
        role.setId(100);
        role.setName("Manager");
        return role;
    }

    private static Role getDirectorRole () {
        Role role = new Role();
        role.setId(101);
        role.setName("Director");
        return role;
    }

    private Page<PendingTransaction> getAllTransactions() {
        List<PendingTransaction> pendingTransactions = new ArrayList<>(1);
        pendingTransactions.add(getFirstPendingTransaction());

        Page<PendingTransaction> transactions = new PageImpl<PendingTransaction>(pendingTransactions);
        return transactions;
    }

    private PendingTransaction getFirstPendingTransaction() {
        PendingTransaction pendingTransaction = new PendingTransaction();
        pendingTransaction.setId(1);
        pendingTransaction.setCurrentApprovalLevel(WORKFLOW.get(StatusType.INTERMEDIATE));
        pendingTransaction.setLocalAmount(100.0);
        pendingTransaction.setLocalCurrency(USD);
        pendingTransaction.setPaymentAmount(100.0);
        pendingTransaction.setPaymentCurrency(USD);
        List<PendingChargeAdjustment> pendingChargeAdjustments = new ArrayList<>(1);
        pendingChargeAdjustments.add(getPendingChargeAdjustment());
        pendingTransaction.setPendingChargeAdjustments(pendingChargeAdjustments);
        return pendingTransaction;
    }

    private PendingTransaction getSecondPendingTransaction() {
        PendingTransaction pendingTransaction = new PendingTransaction();
        pendingTransaction.setId(2);
        pendingTransaction.setCurrentApprovalLevel(WORKFLOW.get(StatusType.INITIAL));
        pendingTransaction.setLocalAmount(100.0);
        pendingTransaction.setLocalCurrency(USD);
        pendingTransaction.setPaymentAmount(100.0);
        pendingTransaction.setPaymentCurrency(USD);
        pendingTransaction.setApprovalDocument(null);
        List<PendingChargeAdjustment> pendingChargeAdjustments = new ArrayList<>(1);
        pendingChargeAdjustments.add(getPendingChargeAdjustment());
        pendingTransaction.setPendingChargeAdjustments(pendingChargeAdjustments);
        return pendingTransaction;
    }

    private PendingTransaction getApprovedPendingTransaction() {
        PendingTransaction pendingTransaction = new PendingTransaction();
        pendingTransaction.setId(1);
        pendingTransaction.setCurrentApprovalLevel(WORKFLOW.get(StatusType.FINAL));
        pendingTransaction.setPreviousApprovalLevel(WORKFLOW.get(StatusType.INTERMEDIATE));
        pendingTransaction.setLocalAmount(100.0);
        pendingTransaction.setLocalCurrency(USD);
        pendingTransaction.setPaymentAmount(100.0);
        pendingTransaction.setPaymentCurrency(USD);
        List<PendingChargeAdjustment> pendingChargeAdjustments = new ArrayList<>(1);
        pendingChargeAdjustments.add(getPendingChargeAdjustment());
        pendingTransaction.setPendingChargeAdjustments(pendingChargeAdjustments);
        return pendingTransaction;
    }


    private PendingChargeAdjustment getPendingChargeAdjustment() {
        PendingChargeAdjustment pendingChargeAdjustment = new PendingChargeAdjustment();
        pendingChargeAdjustment.setId(10);
        pendingChargeAdjustment.setAerodrome("FBSK");
        pendingChargeAdjustment.setChargeAmount(10.0d);
        pendingChargeAdjustment.setChargeDescription("Test");
        return pendingChargeAdjustment;
    }

    private final static HashMap<StatusType, ApprovalWorkflow> WORKFLOW = new HashMap(4);
    {
        final ApprovalWorkflow initial = new ApprovalWorkflow();
        initial.setId(1);
        initial.setLevel(1);
        initial.setThresholdAmount(500.0);
        initial.setThresholdCurrency(USD);
        initial.setApprovalName("Pending");
        initial.setStatusType(StatusType.INITIAL);
        initial.setApprovalGroup(getManagerRole());
        initial.setDelete(true);
        final ApprovalWorkflow middle = new ApprovalWorkflow();
        middle.setId(2);
        middle.setLevel(2);
        middle.setApprovalName("Middle");
        middle.setStatusType(StatusType.INTERMEDIATE);
        middle.setApprovalGroup(getDirectorRole());
        middle.setDelete(false);
        final ApprovalWorkflow approved = new ApprovalWorkflow();
        approved.setId(3);
        approved.setLevel(3);
        approved.setApprovalName("Final");
        approved.setStatusType(StatusType.FINAL);
        approved.setDelete(false);
        /*final ApprovalWorkflow rejected = new ApprovalWorkflow();
        rejected.setId(4);
        rejected.setLevel(4);
        rejected.setApprovalName("Rejected");
        rejected.setStatusType(StatusType.DELETED);*/
        initial.setRejected(null);
        initial.setApprovalUnder(middle);
        initial.setApprovalOver(approved);
        middle.setRejected(initial);
        middle.setApprovalUnder(approved);
        middle.setApprovalOver(approved);
        WORKFLOW.put(StatusType.INITIAL, initial);
        WORKFLOW.put(StatusType.INTERMEDIATE, middle);
        WORKFLOW.put(StatusType.FINAL, approved);
        // WORKFLOW.put(StatusType.DELETED, rejected);
    }

    private static ca.ids.abms.modules.currencies.Currency USD = buildCurrency();

    private static Currency buildCurrency() {
        Currency usd = new Currency();
        usd.setDecimalPlaces(2);
        usd.setId(1);
        usd.setSymbol("$");
        usd.setActive(true);
        usd.setCurrencyCode("USD");
        return usd;
    }
}
