package ca.ids.abms.modules.transactions.utility;

import ca.ids.abms.config.error.ErrorDTO;
import ca.ids.abms.modules.transactions.Transaction;
import ca.ids.abms.modules.transactions.TransactionService;
import edu.emory.mathcs.backport.java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TransactionExportUtilityTest {

    private TransactionService transactionService;

    private TransactionExportUtility transactionExportUtility;

    @Before
    public void setup() {
        transactionService = mock(TransactionService.class);

        PlatformTransactionManager transactionManager = mock(PlatformTransactionManager.class);
        when(transactionManager.getTransaction(any()))
            .thenReturn(mock(TransactionStatus.class));

        transactionExportUtility = new TransactionExportUtility(transactionManager, transactionService);
    }

    @Test
    public void exportAllTest() {

        // validate that successful export returns null
        when(transactionService.findAllUnexported()).thenReturn(Collections.singletonList(new Transaction()));
        assertThat(transactionExportUtility.exportAll()).isNull();

        // validate that unsuccessful export returns ErrorDTO
        doThrow(new RuntimeException()).when(transactionService).export(any(Transaction.class));
        assertThat(transactionExportUtility.exportAll()).isInstanceOf(ErrorDTO.class);
    }

    @Test
    public void exportListTest() {

        // validate that successful export returns null
        when(transactionService.getOne(1)).thenReturn(new Transaction());
        assertThat(transactionExportUtility.exportList(Collections.singletonList(1))).isNull();

        // validate that ErrorDTO returned when no transaction exists
        assertThat(transactionExportUtility.exportList(Collections.singletonList(0))).isInstanceOf(ErrorDTO.class);

        // validate null is returned when no ids params
        assertThat(transactionExportUtility.exportList(Collections.emptyList())).isNull();
        assertThat(transactionExportUtility.exportList(null)).isNull();

        // validate that unsuccessful export returns ErrorDTO
        doThrow(new RuntimeException()).when(transactionService).export(any(Transaction.class));
        assertThat(transactionExportUtility.exportList(Collections.singletonList(1))).isInstanceOf(ErrorDTO.class);
    }
}
