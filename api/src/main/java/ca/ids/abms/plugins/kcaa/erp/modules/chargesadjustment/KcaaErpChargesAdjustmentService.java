package ca.ids.abms.plugins.kcaa.erp.modules.chargesadjustment;

import ca.ids.abms.modules.chargesadjustment.ChargesAdjustment;
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
public class KcaaErpChargesAdjustmentService {

    private static final Logger LOG = LoggerFactory.getLogger(KcaaErpChargesAdjustmentService.class);

    private final SalesLineMapper salesLineMapper;

    private final SalesLineService salesLineService;

    private final SalesLineUtility salesLineUtility;

    public KcaaErpChargesAdjustmentService(
        final SalesLineMapper salesLineMapper,
        final SalesLineService salesLineService,
        final SalesLineUtility salesLineUtility
    ) {
        this.salesLineMapper = salesLineMapper;
        this.salesLineService = salesLineService;
        this.salesLineUtility = salesLineUtility;
    }

    @Transactional(value = KcaaErpDatabaseConfig.TRANSACTION_MANAGER)
    public void create(final ChargesAdjustment chargesAdjustment, Integer lineNo) {
        LOG.debug("Update Kcaa Erp system with saved charges adjustment {}", chargesAdjustment);
        salesLineService.insert(parse(chargesAdjustment, lineNo));
    }

    public PluginSqlStatement createStatement(final ChargesAdjustment chargesAdjustment, Integer lineNo) {
        return salesLineUtility.insertStatement(parse(chargesAdjustment, lineNo));
    }

    private SalesLine parse(final ChargesAdjustment chargesAdjustment, final Integer lineNo) {
        SalesLine salesLine = salesLineMapper.toSalesLine(chargesAdjustment);
        salesLine.setLineNo(lineNo);
        return salesLine;
    }
}
