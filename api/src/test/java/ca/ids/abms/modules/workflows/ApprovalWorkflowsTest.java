package ca.ids.abms.modules.workflows;

import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.modules.currencies.CurrencyRepository;
import ca.ids.abms.modules.roles.RoleRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class ApprovalWorkflowsTest {

    private ApprovalWorkflowRepository approvalWorkflowRepository = mock(ApprovalWorkflowRepository.class);
    private RoleRepository roleRepository = mock(RoleRepository.class);
    private CurrencyRepository currencyRepository = mock(CurrencyRepository.class);
    private ApprovalWorkflowService approvalWorkflowService;

    private final String PENDING = "Pending";
    private final String INTERMEDIATE_1 = "Intermediate";
    private final String INTERMEDIATE_2 = "Additional";
    private final String APPROVED = "Approved";


    @Before
    public void setup() {
        approvalWorkflowService = new ApprovalWorkflowService(approvalWorkflowRepository, roleRepository, currencyRepository);

        when(approvalWorkflowRepository.findOne(0)).thenReturn(buildInitial());
        when(approvalWorkflowRepository.findOne(1)).thenReturn(buildIntermediate());
        when(approvalWorkflowRepository.findOne(2)).thenReturn(buildApproved());
        when(approvalWorkflowRepository.getOne(0)).thenReturn(buildInitial());
        when(approvalWorkflowRepository.getOne(1)).thenReturn(buildIntermediate());
        when(approvalWorkflowRepository.getOne(2)).thenReturn(buildApproved());
        when(approvalWorkflowRepository.findOneByLevel(0)).thenReturn(buildInitial());
        when(approvalWorkflowRepository.findOneByLevel(1)).thenReturn(buildIntermediate());
        when(approvalWorkflowRepository.findOneByLevel(2)).thenReturn(buildApproved());

        when(approvalWorkflowRepository.findAllByApprovalName(PENDING)).thenReturn(Arrays.asList(buildInitial()));
        when(approvalWorkflowRepository.findAllByApprovalName(INTERMEDIATE_1)).thenReturn(Arrays.asList(buildIntermediate()));
        when(approvalWorkflowRepository.findAllByApprovalName(APPROVED)).thenReturn(Arrays.asList(buildApproved()));
        when(approvalWorkflowRepository.findAllByDelete(eq(true))).thenReturn(Arrays.asList(buildInitial()));

        when(approvalWorkflowRepository.save(any(ApprovalWorkflow.class))).thenAnswer(
            new Answer<ApprovalWorkflow>(){
                @Override
                public ApprovalWorkflow answer(InvocationOnMock invocation){
                    return invocation.getArgumentAt(0, ApprovalWorkflow.class);
                }});

        when(approvalWorkflowRepository.findAllByStatusType(StatusType.INITIAL)).thenReturn(Arrays.asList(buildInitial()));
        when(approvalWorkflowRepository.findAllByStatusTypeOrderByLevelAsc(StatusType.INTERMEDIATE)).thenReturn(Arrays.asList(buildIntermediate()));
        when(approvalWorkflowRepository.findAllByStatusType(StatusType.FINAL)).thenReturn(Arrays.asList(buildApproved()));
    }

    @Test
    public void createStatus() throws Exception {
        ApprovalWorkflow item = new ApprovalWorkflow();
        item.setLevel(3);
        item.setApprovalName(INTERMEDIATE_2);
        item.setStatusType(StatusType.INTERMEDIATE);
        ApprovalWorkflow itemCreated = approvalWorkflowService.create(3, item);
        assertThat(itemCreated).isEqualTo(item);
    }

    @Test(expected = CustomParametrizedException.class)
    public void createStatusWithTypeAlreadyExisting() throws Exception {
        ApprovalWorkflow item = new ApprovalWorkflow();
        item.setLevel(0);
        item.setApprovalName(INTERMEDIATE_2);
        item.setStatusType(StatusType.INITIAL);
        approvalWorkflowService.create(0, item);
    }

    @Test
    public void updateStatus() throws Exception {
        final ApprovalWorkflow item = new ApprovalWorkflow();
        item.setLevel(1);
        item.setApprovalName(INTERMEDIATE_2);
        item.setStatusType(StatusType.INTERMEDIATE);

        final ApprovalWorkflow updatedItem = approvalWorkflowService.update(1, item);
        assertThat(updatedItem.getId()).isEqualTo(1);
        assertThat(updatedItem.getApprovalName()).isEqualTo(INTERMEDIATE_2);
    }

    @Test(expected = CustomParametrizedException.class)
    public void updateStatusWithTypeAlreadyExisting() throws Exception {
        final ApprovalWorkflow item = new ApprovalWorkflow();
        item.setLevel(1);
        item.setApprovalName(INTERMEDIATE_2);
        item.setStatusType(StatusType.FINAL);

        approvalWorkflowService.update(6, item);
    }

    @Test
    public void getInitialFinalStatues() throws Exception {
        ApprovalWorkflow initialStatus = approvalWorkflowService.getInitialStatus();
        assertThat(initialStatus.getStatusType()).isEqualTo(StatusType.INITIAL);
        ApprovalWorkflow finalStatus = approvalWorkflowService.getApprovedStatus();
        assertThat(finalStatus.getStatusType()).isEqualTo(StatusType.FINAL);
    }

    private ApprovalWorkflow buildInitial() {
        final ApprovalWorkflow first = new ApprovalWorkflow();
        first.setId(0);
        first.setLevel(0);
        first.setApprovalName(PENDING);
        first.setStatusType(StatusType.INITIAL);
        first.setDelete(true);
        return first;
    }

    private ApprovalWorkflow buildIntermediate() {
        final ApprovalWorkflow middle = new ApprovalWorkflow();
        middle.setId(1);
        middle.setLevel(1);
        middle.setApprovalName(INTERMEDIATE_1);
        middle.setStatusType(StatusType.INTERMEDIATE);
        middle.setDelete(false);
        return middle;
    }

    private ApprovalWorkflow buildApproved() {
        final ApprovalWorkflow last = new ApprovalWorkflow();
        last.setId(2);
        last.setLevel(2);
        last.setApprovalName(APPROVED);
        last.setStatusType(StatusType.FINAL);
        last.setDelete(false);
        return last;
    }
}
