package ca.ids.abms.plugins.kcaa.erp.modules.flightmovement;

import ca.ids.abms.modules.billings.BillingLedger;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.reports2.common.CachedAerodromeResolver;
import ca.ids.abms.modules.reports2.common.CachedCurrencyConverter;
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
public class KcaaErpFlightMovementService {

    private static final Logger LOG = LoggerFactory.getLogger(KcaaErpFlightMovementService.class);

    private final SalesLineMapper salesLineMapper;

    private final SalesLineService salesLineService;

    private final SalesLineUtility salesLineUtility;

    public KcaaErpFlightMovementService(
        final SalesLineMapper salesLineMapper,
        final SalesLineService salesLineService,
        final SalesLineUtility salesLineUtility
    ) {
        this.salesLineMapper = salesLineMapper;
        this.salesLineService = salesLineService;
        this.salesLineUtility = salesLineUtility;
    }

    @Transactional(value = KcaaErpDatabaseConfig.TRANSACTION_MANAGER)
    public void create(final FlightMovement flightMovement, final BillingLedger billingLedger, final Integer lineNo,
                       final CachedCurrencyConverter currencyConverter, final CachedAerodromeResolver aerodromeResolver) {
        LOG.debug("Update Kcaa Erp system with saved flight movement {}", flightMovement);
        salesLineService.insert(parse(flightMovement, billingLedger, lineNo, currencyConverter, aerodromeResolver));
    }

    public PluginSqlStatement createStatement(final FlightMovement flightMovement, final BillingLedger billingLedger,
                                              final Integer lineNo, final CachedCurrencyConverter currencyConverter,
                                              final CachedAerodromeResolver aerodromeResolver) {
        return salesLineUtility.insertStatement(parse(flightMovement, billingLedger, lineNo, currencyConverter, aerodromeResolver));
    }

    private SalesLine parse(final FlightMovement flightMovement, final BillingLedger billingLedger, final Integer lineNo,
                            final CachedCurrencyConverter currencyConverter, final CachedAerodromeResolver aerodromeResolver) {
        SalesLine salesLine = salesLineMapper.toSalesLine(flightMovement, billingLedger, currencyConverter, aerodromeResolver);
        salesLine.setLineNo(lineNo);
        return salesLine;
    }
}
