package ca.ids.abms.plugins.caab.sage.modules.billings;

import ca.ids.abms.modules.billings.InvoiceLineItem;
import ca.ids.abms.plugins.caab.sage.modules.arinvoicedetails.ARInvoiceDetailsMapper;
import ca.ids.abms.plugins.caab.sage.modules.arinvoicedetails.ARInvoiceDetailsService;
import ca.ids.abms.plugins.caab.sage.modules.arinvoicedetails.ARInvoiceDetailsUtility;
import ca.ids.abms.plugins.caab.sage.utilities.CaabSageMapperHelper;
import ca.ids.abms.util.jdbc.PluginSqlStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CaabSageLineItemService {

    private static final Logger LOG = LoggerFactory.getLogger(CaabSageLineItemService.class);

    private final ARInvoiceDetailsMapper arInvoiceDetailsMapper;
    private final ARInvoiceDetailsService arInvoiceDetailsService;
    private final ARInvoiceDetailsUtility arInvoiceDetailsUtility;

    private final CaabSageMapperHelper caabSageMapperHelper;

    public CaabSageLineItemService(
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

    public void create(final InvoiceLineItem invoiceLineItem) {
        LOG.debug("Update CAAB Sage system with saved invoice line item: {}", invoiceLineItem);
        arInvoiceDetailsService.insert(arInvoiceDetailsMapper.toARInvoiceDetails(invoiceLineItem));
    }

    PluginSqlStatement createStatement(final InvoiceLineItem invoiceLineItem) {
        return arInvoiceDetailsUtility.insertStatement(arInvoiceDetailsMapper.toARInvoiceDetails(invoiceLineItem));
    }

    public void validate(final InvoiceLineItem invoiceLineItem) {
        caabSageMapperHelper.getDistributionCode(invoiceLineItem);
    }
}
