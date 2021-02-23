package ca.ids.abms.plugins.kcaa.erp.modules.billings;

import ca.ids.abms.modules.billings.InvoiceLineItem;
import ca.ids.abms.plugins.kcaa.erp.config.KcaaErpDatabaseConfig;
import ca.ids.abms.plugins.kcaa.erp.modules.salesline.SalesLine;
import ca.ids.abms.plugins.kcaa.erp.modules.salesline.SalesLineMapper;
import ca.ids.abms.plugins.kcaa.erp.modules.salesline.SalesLineService;
import ca.ids.abms.plugins.kcaa.erp.modules.salesline.SalesLineUtility;
import ca.ids.abms.util.jdbc.PluginSqlStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class KcaaErpInvoiceLineItemService {

    private static final Logger LOG = LoggerFactory.getLogger(KcaaErpInvoiceLineItemService.class);

    private final SalesLineMapper salesLineMapper;

    private final SalesLineService salesLineService;

    private final SalesLineUtility salesLineUtility;

    public KcaaErpInvoiceLineItemService(final SalesLineMapper salesLineMapper,
                                         final SalesLineService salesLineService,
                                         final SalesLineUtility salesLineUtility) {
        this.salesLineMapper = salesLineMapper;
        this.salesLineService = salesLineService;
        this.salesLineUtility = salesLineUtility;
    }

    @Transactional(value = KcaaErpDatabaseConfig.TRANSACTION_MANAGER)
    public void create(final InvoiceLineItem invoiceLineItem, Integer lineNo) {
        LOG.debug("Update Kcaa Erp system with saved invoice line item {}", invoiceLineItem);
        salesLineService.insert(parse(invoiceLineItem, lineNo));
    }

    PluginSqlStatement createStatement(final InvoiceLineItem invoiceLineItem, Integer lineNo) {
        return salesLineUtility.insertStatement(parse(invoiceLineItem, lineNo));
    }

    private SalesLine parse(final InvoiceLineItem invoiceLineItem, Integer lineNo) {
        SalesLine salesLine = salesLineMapper.toSalesLine(invoiceLineItem);
        salesLine.setLineNo(lineNo);
        return salesLine;
    }
}
