package ca.ids.abms.plugins.caab.sage.modules.payment;

import ca.ids.abms.modules.transactions.TransactionPayment;
import ca.ids.abms.plugins.caab.sage.modules.arpaymentdetails.ARPaymentDetailsMapper;
import ca.ids.abms.plugins.caab.sage.modules.arpaymentdetails.ARPaymentDetailsService;
import ca.ids.abms.plugins.caab.sage.modules.arpaymentdetails.ARPaymentDetailsUtility;
import ca.ids.abms.plugins.caab.sage.utilities.CaabSageMapperHelper;
import ca.ids.abms.util.jdbc.PluginSqlStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CaabSageTransactionPaymentService {

    private static final Logger LOG = LoggerFactory.getLogger(CaabSageTransactionPaymentService.class);

    private final ARPaymentDetailsMapper arPaymentDetailsMapper;
    private final ARPaymentDetailsService arPaymentDetailsService;
    private final ARPaymentDetailsUtility arPaymentDetailsUtility;

    private final CaabSageMapperHelper caabSageMapperHelper;

    public CaabSageTransactionPaymentService(
        final ARPaymentDetailsMapper arPaymentDetailsMapper,
        final ARPaymentDetailsService arPaymentDetailsService,
        final ARPaymentDetailsUtility arPaymentDetailsUtility,
        final CaabSageMapperHelper caabSageMapperHelper
    ) {
        this.arPaymentDetailsMapper = arPaymentDetailsMapper;
        this.arPaymentDetailsService = arPaymentDetailsService;
        this.arPaymentDetailsUtility = arPaymentDetailsUtility;
        this.caabSageMapperHelper = caabSageMapperHelper;
    }

    public void create(final TransactionPayment transactionPayment) {
        LOG.debug("Update CAAB Sage system with saved transaction payment: {}", transactionPayment);
        arPaymentDetailsService.insert(arPaymentDetailsMapper.toARPaymentDetails(transactionPayment));
    }

    public PluginSqlStatement createStatement(final TransactionPayment transactionPayment) {
        return arPaymentDetailsUtility.insertStatement(arPaymentDetailsMapper.toARPaymentDetails(transactionPayment));
    }

    public boolean validate(final TransactionPayment transactionPayment) {
        // validate that bank code exists for transaction payment
        return caabSageMapperHelper.getBankCode(transactionPayment.getTransaction()) != null;
    }
}
