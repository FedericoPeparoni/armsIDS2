package ca.ids.abms.modules.invoices;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import ca.ids.abms.modules.reports2.InvoiceTemplateCategory;
import ca.ids.abms.modules.reports2.common.ReportHelper;

public class InvoiceTemplateServiceTest {

    private InvoiceTemplateRepository invoiceTemplateRepository;
    private InvoiceTemplateService invoiceTemplateService;
    private InvoiceTemplateMapper invoiceTemplateMapper;
    private ReportHelper reportHelper;

    @Test
    public void testFindAllInvoiceTemplates() {

        Page<InvoiceTemplateViewModel> results = invoiceTemplateService.findAll(mock(Pageable.class), "");

        assertThat(results.getTotalElements()).isEqualTo(InvoiceTemplateCategory.values().length);
    }

    @Test
    public void testFindDefaultInvoiceTemplateByCategory() {
        InvoiceTemplate result = invoiceTemplateService.getInvoiceTemplateFromDbOrDefaultByCategory(InvoiceTemplateCategory.TRANSACTION_RECEIPT, false);
        assertThat(result.getInvoiceTemplateName()).isEqualTo(InvoiceTemplateCategory.TRANSACTION_RECEIPT.getReadableValue());
    }

    @Before
    public void setup() {
        invoiceTemplateRepository = mock(InvoiceTemplateRepository.class);
        invoiceTemplateMapper = mock(InvoiceTemplateMapper.class);
        reportHelper = mock(ReportHelper.class);        
        invoiceTemplateService = new InvoiceTemplateService(invoiceTemplateRepository,invoiceTemplateMapper,reportHelper);
    }
}
