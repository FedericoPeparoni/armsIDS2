package ca.ids.abms.plugins.caab.sage.modules.arinvoiceheader;

import ca.ids.abms.plugins.caab.sage.config.CaabSageDatabaseConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ARInvoiceHeaderService {

    private static final Logger LOG = LoggerFactory.getLogger(ARInvoiceHeaderService.class);

    private final ARInvoiceHeaderRepository arInvoiceHeaderRepository;

    public ARInvoiceHeaderService(final ARInvoiceHeaderRepository arInvoiceHeaderRepository) {
        this.arInvoiceHeaderRepository = arInvoiceHeaderRepository;
    }

    @Transactional(value = CaabSageDatabaseConfig.TRANSACTION_MANAGER)
    public ARInvoiceHeader insert(final ARInvoiceHeader arInvoiceHeader) {
        LOG.debug("Insert CAAB Sage ARInvoiceHeader: {}", arInvoiceHeader);
        return arInvoiceHeaderRepository.insert(arInvoiceHeader);
    }
}
