package ca.ids.abms.plugins.caab.sage.modules.arpaymentdetails;

import ca.ids.abms.plugins.caab.sage.config.CaabSageDatabaseConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ARPaymentDetailsService {

    private static final Logger LOG = LoggerFactory.getLogger(ARPaymentDetailsService.class);

    private final ARPaymentDetailsRepository arPaymentDetailsRepository;

    public ARPaymentDetailsService(final ARPaymentDetailsRepository arPaymentDetailsRepository) {
        this.arPaymentDetailsRepository = arPaymentDetailsRepository;
    }

    @Transactional(value = CaabSageDatabaseConfig.TRANSACTION_MANAGER)
    public ARPaymentDetails insert(final ARPaymentDetails arPaymentDetails) {
        LOG.debug("Insert CAAB Sage ARPaymentDetails: {}", arPaymentDetails);
        return arPaymentDetailsRepository.insert(arPaymentDetails);
    }
}
