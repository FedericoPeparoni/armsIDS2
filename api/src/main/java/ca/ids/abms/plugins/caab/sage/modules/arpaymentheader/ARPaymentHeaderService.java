package ca.ids.abms.plugins.caab.sage.modules.arpaymentheader;

import ca.ids.abms.plugins.caab.sage.config.CaabSageDatabaseConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ARPaymentHeaderService {

    private static final Logger LOG = LoggerFactory.getLogger(ARPaymentHeaderService.class);

    private final ARPaymentHeaderRepository arPaymentHeaderRepository;

    public ARPaymentHeaderService(final ARPaymentHeaderRepository arPaymentHeaderRepository) {
        this.arPaymentHeaderRepository = arPaymentHeaderRepository;
    }

    @Transactional(value = CaabSageDatabaseConfig.TRANSACTION_MANAGER)
    public ARPaymentHeader insert(final ARPaymentHeader arPaymentHeader) {
        LOG.debug("Insert CAAB Sage ARPaymentHeader: {}", arPaymentHeader);
        return arPaymentHeaderRepository.insert(arPaymentHeader);
    }
}
