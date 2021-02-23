package ca.ids.abms.plugins.kcaa.erp.modules.salesline;

import ca.ids.abms.plugins.kcaa.erp.config.KcaaErpDatabaseConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SalesLineService {

    private static final Logger LOG = LoggerFactory.getLogger(SalesLineService.class);

    private static final Integer LINE_ITEM_INCREMENT = 10000;

    private final SalesLineRepository salesLineRepository;

    public SalesLineService(SalesLineRepository salesLineRepository) {
        this.salesLineRepository = salesLineRepository;
    }

    @Transactional(value = KcaaErpDatabaseConfig.TRANSACTION_MANAGER)
    public SalesLine insert(SalesLine salesLine) {
        LOG.debug("Insert Kcaa Erp sales line : {}", salesLine);
        return salesLineRepository.insert(salesLine);
    }

    @Transactional(value = KcaaErpDatabaseConfig.TRANSACTION_MANAGER)
    public SalesLine update(SalesLine salesLine) {
        LOG.debug("Update Kcaa Erp sales line : {}", salesLine);
        return salesLineRepository.update(salesLine);
    }

    @Transactional(readOnly = true, value = KcaaErpDatabaseConfig.TRANSACTION_MANAGER)
    public SalesLine findOne(String documentNo, Integer lineNo) {
        LOG.debug("Request to find sales line : {}, {}", documentNo, lineNo);
        return salesLineRepository.findOne(documentNo, lineNo);
    }

    /**
     * Utility to get the next line number value following Kcaa Erp
     * System preferred incremental logic.
     *
     * @param previousLineNumber previous line number
     * @return next line number
     */
    public Integer nextLineNumber(Integer previousLineNumber) {
        return previousLineNumber + LINE_ITEM_INCREMENT;
    }
}
