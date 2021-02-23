package ca.ids.abms.modules.transactions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public class TransactionTypeServiceTest {

    private TransactionTypeRepository transactionTypeRepository;
    private TransactionTypeService transactionTypeService;

    @Test
    public void getAllTransactionTypes() throws Exception {
        List<TransactionType> transactionTypes = Collections.singletonList(new TransactionType());

        when(transactionTypeRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(transactionTypes));

        List<TransactionType> results = transactionTypeService.findAll();

        assertThat(results.equals(transactionTypes));
    }

    @Before
    public void setup() {
        transactionTypeRepository = mock(TransactionTypeRepository.class);
        transactionTypeService = new TransactionTypeService(transactionTypeRepository);
    }
}
