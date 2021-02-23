package ca.ids.abms.modules.billings.utility;

import ca.ids.abms.config.error.ErrorDTO;
import ca.ids.abms.modules.billings.BillingLedger;
import ca.ids.abms.modules.billings.BillingLedgerService;
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

public class BillingLedgerExportUtilityTest {

    private BillingLedgerService billingLedgerService;

    private BillingLedgerExportUtility billingLedgerExportUtility;

    @Before
    public void setup() {
        billingLedgerService = mock(BillingLedgerService.class);

        PlatformTransactionManager transactionManager = mock(PlatformTransactionManager.class);
        when(transactionManager.getTransaction(any()))
            .thenReturn(mock(TransactionStatus.class));

        billingLedgerExportUtility = new BillingLedgerExportUtility(billingLedgerService, transactionManager);
    }

    @Test
    public void exportAllTest() {

        // validate that successful export returns null
        when(billingLedgerService.findAllUnexported()).thenReturn(Collections.singletonList(new BillingLedger()));
        assertThat(billingLedgerExportUtility.exportAll()).isNull();

        // validate that unsuccessful export returns ErrorDTO
        doThrow(new RuntimeException()).when(billingLedgerService).export(any(BillingLedger.class));
        assertThat(billingLedgerExportUtility.exportAll()).isInstanceOf(ErrorDTO.class);
    }

    @Test
    public void exportListTest() {

        // validate that successful export returns null
        when(billingLedgerService.findOne(1)).thenReturn(new BillingLedger());
        assertThat(billingLedgerExportUtility.exportList(Collections.singletonList(1))).isNull();

        // validate that ErrorDTO returned when no billing ledger exists
        assertThat(billingLedgerExportUtility.exportList(Collections.singletonList(0))).isInstanceOf(ErrorDTO.class);

        // validate null is returned when no ids params
        assertThat(billingLedgerExportUtility.exportList(Collections.emptyList())).isNull();
        assertThat(billingLedgerExportUtility.exportList(null)).isNull();

        // validate that unsuccessful export returns ErrorDTO
        doThrow(new RuntimeException()).when(billingLedgerService).export(any(BillingLedger.class));
        assertThat(billingLedgerExportUtility.exportList(Collections.singletonList(1))).isInstanceOf(ErrorDTO.class);
    }
}
