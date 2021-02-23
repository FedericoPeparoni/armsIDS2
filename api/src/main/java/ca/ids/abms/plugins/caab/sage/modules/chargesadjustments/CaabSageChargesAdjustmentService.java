package ca.ids.abms.plugins.caab.sage.modules.chargesadjustments;

import ca.ids.abms.modules.chargesadjustment.ChargesAdjustment;
import ca.ids.abms.plugins.caab.sage.modules.arinvoicedetails.ARInvoiceDetailsMapper;
import ca.ids.abms.plugins.caab.sage.modules.arinvoicedetails.ARInvoiceDetailsService;
import ca.ids.abms.plugins.caab.sage.modules.arinvoicedetails.ARInvoiceDetailsUtility;
import ca.ids.abms.plugins.caab.sage.utilities.CaabSageMapperHelper;
import ca.ids.abms.util.jdbc.PluginSqlStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CaabSageChargesAdjustmentService {

    private static final Logger LOG = LoggerFactory.getLogger(CaabSageChargesAdjustmentService.class);

    private final ARInvoiceDetailsMapper arInvoiceDetailsMapper;
    private final ARInvoiceDetailsService arInvoiceDetailsService;
    private final ARInvoiceDetailsUtility arInvoiceDetailsUtility;

    private final CaabSageMapperHelper caabSageMapperHelper;

    public CaabSageChargesAdjustmentService(
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

    public void create(final ChargesAdjustment chargesAdjustment) {
        LOG.debug("Update CAAB Sage system with saved charges adjustment: {}", chargesAdjustment);
        arInvoiceDetailsService.insert(arInvoiceDetailsMapper.toARInvoiceDetails(chargesAdjustment));
    }

    public PluginSqlStatement createStatement(final ChargesAdjustment chargesAdjustment) {
        return arInvoiceDetailsUtility.insertStatement(arInvoiceDetailsMapper.toARInvoiceDetails(chargesAdjustment));
    }

    public boolean validate(final ChargesAdjustment chargesAdjustment) {
        // validate that distribution code exists for charge adjustment
        return caabSageMapperHelper.getDistributionCode(chargesAdjustment) != null;
    }
}
