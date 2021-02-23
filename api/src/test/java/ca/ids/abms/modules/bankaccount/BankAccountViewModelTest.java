package ca.ids.abms.modules.bankaccount;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class BankAccountViewModelTest {

    private static final Integer ID = 1;
    private static final String NAME = "Mock Bank Account";
    private static final String NUMBER = "MOCK_NUMBER";

    private BankAccountViewModel bankAccountViewModel;

    @Before
    public void setup() {
        bankAccountViewModel = mockBankAccountViewModel();
    }

    @Test
    public void getterTest() {
        assertThat(bankAccountViewModel.getId()).isEqualTo(ID);
        assertThat(bankAccountViewModel.getName()).isEqualTo(NAME);
        assertThat(bankAccountViewModel.getNumber()).isEqualTo(NUMBER);
    }

    @Test
    public void setterTest() {
        bankAccountViewModel.setId(ID);
        bankAccountViewModel.setName(NAME);
        bankAccountViewModel.setNumber(NUMBER);

        // validate all values still match by getters
        getterTest();
    }

    private static BankAccountViewModel mockBankAccountViewModel() {
        BankAccountViewModel bankAccountViewModel = new BankAccountViewModel();
        bankAccountViewModel.setId(ID);
        bankAccountViewModel.setName(NAME);
        bankAccountViewModel.setNumber(NUMBER);
        return bankAccountViewModel;
    }
}
