package ca.ids.abms.plugins.kcaa.erp.modules.receiptheader;

import ca.ids.abms.modules.transactions.Transaction;
import ca.ids.abms.plugins.kcaa.erp.utilities.DefaultValue;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class ReceiptHeaderMapperTest {

    @Test
    public void toTransactionCreatedByTest() {
        ReceiptHeaderMapper mapper = new ReceiptHeaderMapperImpl();

        ReceiptHeader receiptHeader = new ReceiptHeader();
        Transaction transaction = new Transaction();

        receiptHeader.setEnteredBy("enteredBy");
        mapper.toTransactionCreatedBy(receiptHeader, transaction);
        assertThat(transaction.getCreatedBy()).isEqualToIgnoringCase("enteredBy");

        receiptHeader.setCashierId("cachierId");
        mapper.toTransactionCreatedBy(receiptHeader, transaction);
        assertThat(transaction.getCreatedBy()).isEqualToIgnoringCase("cachierId");

        LocalDate localDate = LocalDate.now();
        receiptHeader.setCasherDate(localDate.atStartOfDay());
        mapper.toTransactionCreatedBy(receiptHeader, transaction);
        assertThat(transaction.getCreatedAt()).isEqualTo(localDate.atStartOfDay());

        LocalTime localTime = LocalTime.NOON;
        LocalDateTime time = (LocalDateTime.of(DefaultValue.LOCAL_DATE_TIME.toLocalDate(), localTime));
        receiptHeader.setCachierTime(time);
        mapper.toTransactionCreatedBy(receiptHeader, transaction);
        assertThat(transaction.getCreatedAt()).isEqualTo(localDate.atTime(localTime));
    }
}
