package ca.ids.abms.modules.chargesadjustment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class ChargesAdjustmentService {
    private static final Logger LOG = LoggerFactory.getLogger(ChargesAdjustmentService.class);

    private ChargesAdjustmentRepository chargesAdjustmentRepository;

    public ChargesAdjustmentService(ChargesAdjustmentRepository chargesAdjustmentRepository) {
        this.chargesAdjustmentRepository = chargesAdjustmentRepository;
    }

    public ChargesAdjustment save(ChargesAdjustment chargesAdjustment) {
        LOG.debug("Save charge adjustment {}", chargesAdjustment);
        return chargesAdjustmentRepository.save(chargesAdjustment);
    }

    public List<ChargesAdjustment> findAllByTransactionId(Integer id) {
        LOG.debug("Request to find charge adjustments by transaction id {}", id);
        return chargesAdjustmentRepository.findAllByTransactionId(id);
    }
}
