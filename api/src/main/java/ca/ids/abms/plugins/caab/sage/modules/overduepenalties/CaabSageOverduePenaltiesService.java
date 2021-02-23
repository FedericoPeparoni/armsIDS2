package ca.ids.abms.plugins.caab.sage.modules.overduepenalties;

import ca.ids.abms.modules.billings.InvoiceOverduePenalty;
import ca.ids.abms.plugins.caab.sage.modules.arinvoicedetails.ARInvoiceDetailsMapper;
import ca.ids.abms.plugins.caab.sage.modules.arinvoicedetails.ARInvoiceDetailsService;
import ca.ids.abms.plugins.caab.sage.modules.arinvoicedetails.ARInvoiceDetailsUtility;
import ca.ids.abms.plugins.caab.sage.utilities.CaabSageMapperHelper;
import ca.ids.abms.util.jdbc.PluginSqlStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CaabSageOverduePenaltiesService {

    private static final Logger LOG = LoggerFactory.getLogger(CaabSageOverduePenaltiesService.class);

    private final ARInvoiceDetailsMapper arInvoiceDetailsMapper;
    private final ARInvoiceDetailsService arInvoiceDetailsService;
    private final ARInvoiceDetailsUtility arInvoiceDetailsUtility;

    private final CaabSageMapperHelper caabSageMapperHelper;

    public CaabSageOverduePenaltiesService(
        final ARInvoiceDetailsMapper arInvoiceDetailsMapper,
        final ARInvoiceDetailsService arInvoiceDetailsService,
        final ARInvoiceDetailsUtility arInvoiceDetailsUtility,
        final CaabSageMapperHelper caabSageMapperHelper
    ) {
        this.arInvoiceDetailsMapper = arInvoiceDetailsMapper;
        this.arInvoiceDetailsService = arInvoiceDetailsService;
        this.arInvoiceDetailsUtility = arInvoiceDetailsUtility;
        this.caabSageMapperHelper = caabSageMapperHelper;
    }

    public void create(final InvoiceOverduePenalty invoiceOverduePenalty) {
        LOG.debug("Save CAAB Sage system with saved invoice overdue penalty {}", invoiceOverduePenalty);
        arInvoiceDetailsService.insert(arInvoiceDetailsMapper.toARInvoiceDetails(invoiceOverduePenalty));
    }

    public PluginSqlStatement createStatement(final InvoiceOverduePenalty invoiceOverduePenalty) {
        return arInvoiceDetailsUtility.insertStatement(arInvoiceDetailsMapper.toARInvoiceDetails(invoiceOverduePenalty));
    }

    public void validate(final InvoiceOverduePenalty invoiceOverduePenalty) {
        caabSageMapperHelper.getDistributionCode(invoiceOverduePenalty);
    }
}
