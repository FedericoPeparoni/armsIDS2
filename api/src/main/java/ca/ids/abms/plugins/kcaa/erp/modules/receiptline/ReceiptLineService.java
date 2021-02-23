package ca.ids.abms.plugins.kcaa.erp.modules.receiptline;

import ca.ids.abms.plugins.kcaa.erp.config.KcaaErpDatabaseConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReceiptLineService {

    private static final Logger LOG = LoggerFactory.getLogger(ReceiptLineService.class);

    private final ReceiptLineRepository receiptLineRepository;

    public ReceiptLineService(ReceiptLineRepository receiptLineRepository) {
        this.receiptLineRepository = receiptLineRepository;
    }

    @Transactional(readOnly = true, value = KcaaErpDatabaseConfig.TRANSACTION_MANAGER)
    public List<ReceiptLine> findByReceiptHeaderNo(String receiptHeaderNo) {
        LOG.debug("Request to get receipt line by receipt header no {}", receiptHeaderNo);
        return receiptLineRepository.findByReceiptHeaderNo(receiptHeaderNo);
    }
}
