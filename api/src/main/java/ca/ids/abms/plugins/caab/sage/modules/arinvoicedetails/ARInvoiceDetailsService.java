package ca.ids.abms.plugins.caab.sage.modules.arinvoicedetails;

import ca.ids.abms.plugins.caab.sage.config.CaabSageDatabaseConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ARInvoiceDetailsService {

    private static final Logger LOG = LoggerFactory.getLogger(ARInvoiceDetailsService.class);

    private final ARInvoiceDetailsRepository arInvoiceDetailsRepository;

    public ARInvoiceDetailsService(final ARInvoiceDetailsRepository arInvoiceDetailsRepository) {
        this.arInvoiceDetailsRepository = arInvoiceDetailsRepository;
    }

    @Transactional(value = CaabSageDatabaseConfig.TRANSACTION_MANAGER)
    public ARInvoiceDetails insert(final ARInvoiceDetails arInvoiceDetails) {
        LOG.debug("Insert CAAB Sage ARInvoiceDetails: {}", arInvoiceDetails);
        return arInvoiceDetailsRepository.insert(arInvoiceDetails);
    }
}
