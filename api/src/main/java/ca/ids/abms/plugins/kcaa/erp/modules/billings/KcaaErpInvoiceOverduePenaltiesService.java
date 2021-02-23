package ca.ids.abms.plugins.kcaa.erp.modules.billings;

import ca.ids.abms.modules.billings.InvoiceOverduePenalty;
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
public class KcaaErpInvoiceOverduePenaltiesService {

    private static final Logger LOG = LoggerFactory.getLogger(KcaaErpInvoiceOverduePenaltiesService.class);

    private final SalesLineMapper salesLineMapper;

    private final SalesLineService salesLineService;

    private final SalesLineUtility salesLineUtility;

    public KcaaErpInvoiceOverduePenaltiesService(
        final SalesLineMapper salesLineMapper,
        final SalesLineService salesLineService,
        final SalesLineUtility salesLineUtility
    ) {
        this.salesLineMapper = salesLineMapper;
        this.salesLineService = salesLineService;
        this.salesLineUtility = salesLineUtility;
    }

    @Transactional(value = KcaaErpDatabaseConfig.TRANSACTION_MANAGER)
    public void create(final InvoiceOverduePenalty invoiceOverduePenalty, final Integer lineNo) {
        LOG.debug("Save Kcaa Erp system with saved invoice overdue penalty {}", invoiceOverduePenalty);
        salesLineService.insert(parse(invoiceOverduePenalty, lineNo));
    }

    PluginSqlStatement createStatement(final InvoiceOverduePenalty invoiceOverduePenalty, final Integer lineNo) {
        return salesLineUtility.insertStatement(parse(invoiceOverduePenalty, lineNo));
    }

    private SalesLine parse(final InvoiceOverduePenalty invoiceOverduePenalty, final Integer lineNo) {
        SalesLine salesLine = salesLineMapper.toSalesLine(invoiceOverduePenalty);
        salesLine.setLineNo(lineNo);
        return salesLine;
    }
}
