package ca.ids.abms.plugins.kcaa.erp.modules.receiptheader;

import ca.ids.abms.plugins.kcaa.erp.config.KcaaErpDatabaseConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReceiptHeaderService {

    private static final Logger LOG = LoggerFactory.getLogger(ReceiptHeaderService.class);

    private final ReceiptHeaderRepository receiptHeaderRepository;

    public ReceiptHeaderService(ReceiptHeaderRepository receiptHeaderRepository) {
        this.receiptHeaderRepository = receiptHeaderRepository;
    }

    @Transactional(readOnly = true, value = KcaaErpDatabaseConfig.TRANSACTION_MANAGER)
    public List<ReceiptHeader> findByTimestamp(byte[] timestampFrom, byte[] timestampTo) {
        LOG.debug("Request to get receipt headers by timestamp between : from {}, to {}", timestampFrom, timestampTo);
        return receiptHeaderRepository.findByTimestampBetween(timestampFrom, timestampTo);
    }

    @Transactional(readOnly = true, value = KcaaErpDatabaseConfig.TRANSACTION_MANAGER)
    public byte[] findLatestTimestamp() {
        LOG.trace("Request to get receipt headers latest timestamp");
        return receiptHeaderRepository.findLatestTimestamp();
    }
}
