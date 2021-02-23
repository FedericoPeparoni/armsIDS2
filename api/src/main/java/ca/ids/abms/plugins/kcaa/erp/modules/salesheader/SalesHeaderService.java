package ca.ids.abms.plugins.kcaa.erp.modules.salesheader;

import ca.ids.abms.plugins.kcaa.erp.config.KcaaErpDatabaseConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SalesHeaderService {

    private static final Logger LOG = LoggerFactory.getLogger(SalesHeaderService.class);

    private final SalesHeaderRepository salesHeaderRepository;

    public SalesHeaderService(SalesHeaderRepository salesHeaderRepository) {
        this.salesHeaderRepository = salesHeaderRepository;
    }

    @Transactional(value = KcaaErpDatabaseConfig.TRANSACTION_MANAGER)
    public SalesHeader insert(SalesHeader salesHeader) {
        LOG.debug("Insert Kcaa Erp sales header : {}", salesHeader);
        return salesHeaderRepository.insert(salesHeader);
    }

    @Transactional(value = KcaaErpDatabaseConfig.TRANSACTION_MANAGER)
    public SalesHeader update(SalesHeader salesHeader) {
        LOG.debug("Update Kcaa Erp sales header : {}", salesHeader);
        return salesHeaderRepository.update(salesHeader);
    }

    @Transactional(readOnly = true, value = KcaaErpDatabaseConfig.TRANSACTION_MANAGER)
    public SalesHeader findOne(String id) {
        LOG.debug("Request to find sales header : {}", id);
        return salesHeaderRepository.findOne(id);
    }
}
