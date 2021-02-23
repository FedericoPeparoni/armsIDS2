package ca.ids.abms.modules.chargesadjustment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/charges-adjustment")
public class ChargesAdjustmentController {

    private static final Logger LOG = LoggerFactory.getLogger(ChargesAdjustmentController.class);
    private final ChargesAdjustmentService chargesAdjustmentService;
    private final ChargesAdjustmentMapper chargesAdjustmentMapper;

    public ChargesAdjustmentController(final ChargesAdjustmentService chargesAdjustmentService,
                                       final ChargesAdjustmentMapper chargesAdjustmentMapper) {
        this.chargesAdjustmentService = chargesAdjustmentService;
        this.chargesAdjustmentMapper = chargesAdjustmentMapper;
    }

    @GetMapping(value = "/byTransaction/{id}")
    public List<ChargesAdjustmentViewModel> findAll(@PathVariable Integer id) {
        LOG.debug("REST request to get all Charges Adjustment by transaction id: {}", id);
        final List<ChargesAdjustment> list = chargesAdjustmentService.findAllByTransactionId(id);
        return chargesAdjustmentMapper.toViewModel(list);
    }
}
