package ca.ids.abms.plugins.caab.sage.modules.flightmovements;

import ca.ids.abms.modules.billingcenters.BillingCenter;
import ca.ids.abms.modules.billings.BillingLedger;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.reports2.common.CachedCurrencyConverter;
import ca.ids.abms.plugins.caab.sage.modules.arinvoicedetails.ARInvoiceDetailsMapper;
import ca.ids.abms.plugins.caab.sage.modules.arinvoicedetails.ARInvoiceDetailsService;
import ca.ids.abms.plugins.caab.sage.modules.arinvoicedetails.ARInvoiceDetailsUtility;
import ca.ids.abms.plugins.caab.sage.modules.distributioncode.DistributionCode;
import ca.ids.abms.plugins.caab.sage.utilities.CaabSageMapperHelper;
import ca.ids.abms.util.jdbc.PluginSqlStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CaabSageFlightMovementService {

    private static final Logger LOG = LoggerFactory.getLogger(CaabSageFlightMovementService.class);

    private final ARInvoiceDetailsMapper arInvoiceDetailsMapper;
    private final ARInvoiceDetailsService arInvoiceDetailsService;
    private final ARInvoiceDetailsUtility arInvoiceDetailsUtility;

    private final CaabSageMapperHelper caabSageMapperHelper;

    public CaabSageFlightMovementService(
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

    public void create(
        final FlightMovement flightMovement, final String chargeCode, final BillingCenter billingCenter,
        final DistributionCode.FlightMovementChargeType chargeType, final BillingLedger billingLedger,
        final CachedCurrencyConverter currencyConverter
    ) {
        LOG.debug("Update CAAB Sage system with saved flight movement {}", flightMovement);
        arInvoiceDetailsService.insert(arInvoiceDetailsMapper.toARInvoiceDetails(flightMovement, billingLedger,
            billingCenter, chargeCode, chargeType, currencyConverter));
    }

    public PluginSqlStatement createStatement(
        final FlightMovement flightMovement, final String chargeCode, final BillingCenter billingCenter,
        final DistributionCode.FlightMovementChargeType chargeType, final BillingLedger billingLedger,
        final CachedCurrencyConverter currencyConverter
    ) {
        return arInvoiceDetailsUtility.insertStatement(arInvoiceDetailsMapper.toARInvoiceDetails(flightMovement, billingLedger,
            billingCenter, chargeCode, chargeType, currencyConverter));
    }

    public void validate(
        final FlightMovement flightMovement, final String chargeCode, final BillingCenter billingCenter
    ) {
        caabSageMapperHelper.getDistributionCode(flightMovement, chargeCode, billingCenter);
    }
}
