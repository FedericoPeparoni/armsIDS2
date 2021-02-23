package ca.ids.abms.plugins.kcaa.eaip.modules.requisition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/eaip_requisitions")
public class RequisitionController {

    private static final Logger LOG = LoggerFactory.getLogger(RequisitionController.class);

    private final RequisitionService requisitionService;

    public RequisitionController(final RequisitionService requisitionService) {
        this.requisitionService = requisitionService;
    }

    @GetMapping
    public ResponseEntity<ca.ids.abms.plugins.kcaa.eaip.modules.requisition.Requisition> findRequisitionByReqNumber(
        @RequestParam(name = "reqNumber") String reqNumber
    ) {
        LOG.debug("REST request to get Requisition by RequisitionNumber: {}", reqNumber);
        Requisition requisition = requisitionService.findByReqNumber(reqNumber);

        return ResponseEntity.ok().body(requisition);
    }
}
