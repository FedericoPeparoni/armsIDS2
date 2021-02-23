package ca.ids.abms.modules.bankaccount;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class BankAccountTest {

    private static final Integer ID = 1;
    private static final String NAME = "Mock Bank Account";
    private static final String NUMBER = "MOCK_NUMBER";

    private BankAccount bankAccount;

    @Before
    public void setup() {
        bankAccount = mockBankAccount();
    }

    @Test
    public void getterTest() {
        assertThat(bankAccount.getId()).isEqualTo(ID);
        assertThat(bankAccount.getName()).isEqualTo(NAME);
        assertThat(bankAccount.getNumber()).isEqualTo(NUMBER);
    }

    @Test
    public void setterTest() {
        bankAccount.setId(ID);
        bankAccount.setName(NAME);
        bankAccount.setNumber(NUMBER);

        // validate all values still match by getters
        getterTest();
    }

    @Test
    public void toStringTest() {
        assertThat(bankAccount.toString()).isEqualTo(mockBankAccount().toString());
    }

    @Test
    public void equalTest() {
        assertThat(bankAccount).isEqualTo(mockBankAccount());
        assertThat(bankAccount).isEqualTo(bankAccount);
        assertThat(bankAccount).isNotEqualTo("mock string");

        BankAccount bankAccount1 = mockBankAccount();
        BankAccount bankAccount2 = mockBankAccount();
        bankAccount2.setId(0);

        assertThat(bankAccount1).isNotEqualTo(bankAccount2);

        bankAccount1.setId(null);
        bankAccount2.setId(null);

        assertThat(bankAccount1).isEqualTo(bankAccount2);
    }

    private static BankAccount mockBankAccount() {
        BankAccount bankAccount = new BankAccount();
        bankAccount.setId(ID);
        bankAccount.setName(NAME);
        bankAccount.setNumber(NUMBER);
        return bankAccount;
    }
}
